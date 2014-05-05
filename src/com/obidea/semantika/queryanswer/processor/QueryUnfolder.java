/*
 * Copyright (c) 2013-2014 Josef Hardi <josef.hardi@gmail.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.obidea.semantika.queryanswer.processor;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;

import com.obidea.semantika.database.sql.base.ISqlExpression;
import com.obidea.semantika.database.sql.base.ISqlFunction;
import com.obidea.semantika.database.sql.base.ISqlQuery;
import com.obidea.semantika.database.sql.base.SqlJoinCondition;
import com.obidea.semantika.database.sql.base.SqlSelectItem;
import com.obidea.semantika.exception.SemantikaRuntimeException;
import com.obidea.semantika.expression.base.AtomVisitorAdapter;
import com.obidea.semantika.expression.base.IAtom;
import com.obidea.semantika.expression.base.IFunction;
import com.obidea.semantika.expression.base.ILiteral;
import com.obidea.semantika.expression.base.IQueryExt;
import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.expression.base.IUriReference;
import com.obidea.semantika.expression.base.IVariable;
import com.obidea.semantika.expression.base.Join;
import com.obidea.semantika.expression.base.Literal;
import com.obidea.semantika.expression.base.QuerySet;
import com.obidea.semantika.expression.base.TermUtils;
import com.obidea.semantika.expression.base.UriReference;
import com.obidea.semantika.knowledgebase.TermSubstitutionBinding;
import com.obidea.semantika.knowledgebase.UnificationException;
import com.obidea.semantika.knowledgebase.Unifier;
import com.obidea.semantika.knowledgebase.model.KnowledgeBase;
import com.obidea.semantika.mapping.ImmutableMappingSet;
import com.obidea.semantika.mapping.base.IMapping;
import com.obidea.semantika.mapping.base.TripleAtom;
import com.obidea.semantika.mapping.sql.SqlColumn;
import com.obidea.semantika.mapping.sql.SqlEqualsTo;
import com.obidea.semantika.mapping.sql.SqlFunction;
import com.obidea.semantika.mapping.sql.SqlJoin;
import com.obidea.semantika.mapping.sql.SqlMappingFactory;
import com.obidea.semantika.mapping.sql.SqlQuery;
import com.obidea.semantika.mapping.sql.SqlSelectQuery;
import com.obidea.semantika.mapping.sql.SqlSubQuery;
import com.obidea.semantika.mapping.sql.SqlTable;
import com.obidea.semantika.mapping.sql.SqlUserQuery;
import com.obidea.semantika.mapping.sql.SqlValue;
import com.obidea.semantika.util.CollectionUtils;
import com.obidea.semantika.util.LogUtils;
import com.obidea.semantika.util.RdfVocabulary;
import com.obidea.semantika.util.Serializer;

/**
 * WARNING: Reading the code below can do harm to your brain and cause a severe concussion! It's
 * full of magic and mystical algorithm. Do not *I repeat* DO NOT try to modify the code unless you
 * are more powerful than the great wizard!
 */
public class QueryUnfolder extends AtomVisitorAdapter implements IUnfolder
{
   private static final URI RDF_TYPE = RdfVocabulary.TYPE.getUri();

   private ImmutableMappingSet mMappingSet;

   private List<ISqlExpression> mPartialAnswers = new ArrayList<ISqlExpression>();
   private List<TermSubstitutionBinding> mPartialBindings = new ArrayList<TermSubstitutionBinding>();

   private Set<IVariable> mQueryVars = new HashSet<IVariable>();
   private List<IVariable> mExcludeVariable = new ArrayList<IVariable>();

   private NameGenerator mNameGenerator = new NameGenerator();

   private boolean mWithinOptionalScope = false;

   private TermToSqlConverter mConverter = new TermToSqlConverter();
   private static SqlMappingFactory sSqlFactory = SqlMappingFactory.getInstance();

   private static final Logger LOG = LogUtils.createLogger("semantika.queryanswer"); //$NON-NLS-1$

   /**
    * The sole constructor.
    * 
    * @param kb
    *           The <code>KnowledgeBase</code> object required for performing query unfolding.
    */
   public QueryUnfolder(KnowledgeBase kb)
   {
      mMappingSet = kb.getMappingSet();
   }

   @Override
   public QuerySet<SqlQuery> unfold(QuerySet<? extends IQueryExt> querySet) throws QueryUnfoldingException
   {
      LOG.debug("Unfolding query..."); //$NON-NLS-1$
      QuerySet<SqlQuery> toReturn = new QuerySet<SqlQuery>();
      for (IQueryExt query : querySet.getAll()) {
         QuerySet<SqlQuery> sqlQueries = unfold(query);
         toReturn.copy(sqlQueries);
      }
      return toReturn;
   }

   @Override
   public QuerySet<SqlQuery> unfold(IQueryExt query) throws QueryUnfoldingException
   {
      /*
       * Make sure the helper lists and sets are empty before running the algorithm.
       */
      reset();
      initPartialBindings();
      initIdentifierGenerator();
      initExcludeVariableList(query);
      
      /*
       * The algorithm uses visitor pattern that traverses the tuple expression within
       * the query body. The unfolding for each tuple will produce a sub-query as
       * specified by the mapping assertion.
       */
      try {
         for (IAtom tupleExpression : query.getBody()) {
            tupleExpression.accept(this);
         }
      }
      catch (SemantikaRuntimeException e) {
         /*
          * Catch any runtime exceptions and pass them as QueryUnfoldingException
          */
         throw new QueryUnfoldingException(e);
      }
      
      QuerySet<SqlQuery> sqlQueries = new QuerySet<SqlQuery>();
      buildAnswer(sqlQueries, query);
      return sqlQueries;
   }

   private void buildAnswer(QuerySet<SqlQuery> sqlQueries, IQueryExt query)
   {
      /*
       * The main feature of this algorithm is the handling of unifier binding. The
       * algorithm doesn't immediately process the variables in the query projection
       * and in the filter; only the variables in the tuple expressions. The former
       * variables are processed in the final step using the concluding unifier.
       * Moreover, this final step is responsible for constructing the unfolded query
       * (i.e., SQL query object).
       */
      Iterator<TermSubstitutionBinding> partialBindingIter = mPartialBindings.iterator();
      for (ISqlExpression partialAnswer : mPartialAnswers) {
         TermSubstitutionBinding partialBinding = partialBindingIter.next();
         if (partialAnswer == null && partialBinding == null) {
            continue;
         }
         
         /*
          * Create a new SQL query object for each concluding unifier binding.
          */
         SqlQuery sqlQuery = new SqlSelectQuery(query.isDistinct());
         
         /*
          * Construct SQL SELECT.
          * 
          * The column projection in SQL SELECT comes from the variables in SPARQL
          * projection. These variables were replaced to become column names through
          * the concluding unifier.
          */
         for (ITerm term : query.getDistTerms()) {
            sqlQuery.addSelectItem(selectItem(term, partialBinding));
         }
         
         /*
          * Construct SQL FROM.
          * 
          * The table selection expressions are coming from the SubQueryQueue
          * that has collected Table expression or SubQuery expression.
          */
         sqlQuery.setFromExpression(partialAnswer);
         
         /*
          * Construct SQL WHERE
          * 
          * The filter expression can come from three sources:
          * 1) It can come from SPARQL FILTER expression. Similar to the construction
          *    of SQL SELECT, the variables in SPARQL FILTER are replaced by the
          *    concluding unifier to be column names.
          */
         for (IFunction function : query.getFilters()) {
            IFunction filter = TermUtils.copy(function); // make copy
            filter.apply(partialBinding);
            sqlQuery.addWhereExpression(getSqlExpression(filter));
         }
         /*
          * 2) The filtering can also come from SPARQL matching pattern, e.g., 
          *    SELECT ?v WHERE { ?v ?p "cat" }. This filtering is captured by the 
          *    unifier such that { VIEW1.COLUMN/"cat" }
          */
         for (IVariable var : partialBinding.getVariables()) {
            assignValueFilters(var, partialBinding.getTerm(var), sqlQuery);
         }
         /*
          * 3) Some extra filters for handling NULL data in database. This filtering
          *    is necessary due to SPARQL query semantic that is based on pattern
          *    matching.
          */
         for (IVariable var : getQueryVariables()) {
            assignNotNullFilters(partialBinding.getTerm(var), sqlQuery);
         }
         
         sqlQueries.add(sqlQuery);
      }
   }

   @Override
   public String getName()
   {
      return QueryUnfolder.class.getCanonicalName(); //$NON-NLS-1$
   }

   @Override
   public void visit(IAtom atom)
   {
      if (atom instanceof Join) {
         Join join = (Join) atom;
         if (join.isInnerJoin()) {
            visitInnerJoinExpression(join);
         }
         else if (join.isLeftJoin()) {
            visitLeftJoinExpression(join);
         }
      }
      else if (atom instanceof TripleAtom) {
         visitTupleExpression((TripleAtom) atom);
      }
   }

   protected void visitInnerJoinExpression(Join join)
   {
      /*
       * Evaluate the partial answer on the left-side of the JOIN query
       */
      List<ISqlExpression> leftPartialAnswers = getPartialAnswers(join.getLeftExpression());
      
      /*
       * Evaluate the partial answer on the right-side of the JOIN query
       */
      List<ISqlExpression> rightPartialAnswers = getPartialAnswers(join.getRightExpression());
      
      /*
       * Create the SQL INNER JOIN expression using the left and right partial answers obtain from processing
       * each tuple node in join expression tree.
       */
      int limit = leftPartialAnswers.size();
      for (int i = 0; i < rightPartialAnswers.size(); i++) {
         ISqlExpression leftPartialAnswer = leftPartialAnswers.get(i % limit); // a trick to arrange the join expressions.
         ISqlExpression rightPartialAnswer = rightPartialAnswers.get(i);
         if (leftPartialAnswer != null && rightPartialAnswer != null) {
            ISqlExpression leftExpression = copy(leftPartialAnswer);
            ISqlExpression rightExpression = copy(rightPartialAnswer);
            SqlJoin sqlJoin = createSqlInnerJoin(
                  leftExpression,
                  rightExpression,
                  joinConditions(mPartialBindings.get(i), CollectionUtils.union(scope(leftExpression), scope(rightExpression))),
                  joinFilters(mPartialBindings.get(i), join.getFilter(), scope(rightExpression)));
            collectPartialAnswer(mPartialAnswers, sqlJoin);
         }
         else {
            collectPartialAnswer(mPartialAnswers, null);
         }
      }
   }

   protected void visitLeftJoinExpression(Join join)
   {
      /*
       * Evaluate the partial answer on the left-side of the OPTIONAL query
       */
      List<ISqlExpression> leftPartialAnswers = getPartialAnswers(join.getLeftExpression());
      List<TermSubstitutionBinding> leftPartialBindings = getPartialBindings();
      initPartialBindings();
      
      /*
       * Evaluate the partial answer on the right-side of the OPTIONAL query. It broadcasts a flag
       * to notify the evaluator that it is currently inside the optional scope.
       */
      broadcastEnterOptionalScope();
      List<ISqlExpression> rightPartialAnswers = getPartialAnswers(join.getRightExpression());
      List<TermSubstitutionBinding> rightPartialBindings = getPartialBindings();
      
      crossPartialBindings(leftPartialBindings, rightPartialBindings);
      
      /*
       * Create the SQL LEFT JOIN expression using the left and right partial answers obtain from processing
       * each tuple node in join expression tree.
       */
      int limit = rightPartialAnswers.size();
      for (int i = 0; i < leftPartialAnswers.size(); i++) {
         for (int j = 0; j < rightPartialAnswers.size(); j++) {
            ISqlExpression leftPartialAnswer = leftPartialAnswers.get(i);
            ISqlExpression rightPartialAnswer = rightPartialAnswers.get(j);
            if (leftPartialAnswer != null && rightPartialAnswer != null) {
               ISqlExpression leftExpression = copy(leftPartialAnswer);
               ISqlExpression rightExpression = copy(rightPartialAnswer);
               int bindingIndex = (i * limit) + j;
               SqlJoin sqlJoin = createSqlLeftJoin(
                     leftExpression,
                     rightExpression,
                     joinConditions(mPartialBindings.get(bindingIndex), CollectionUtils.union(scope(leftExpression), scope(rightExpression))),
                     joinFilters(mPartialBindings.get(bindingIndex), join.getFilter(), scope(rightExpression)));
               collectPartialAnswer(mPartialAnswers, sqlJoin);
            }
            else {
               collectPartialAnswer(mPartialAnswers, null);
            }
         }
      }
      /*
       * Reset the optional flag as it exits the optional scope.
       */
      broadcastExitOptionalScope();
   }

   private void crossPartialBindings(List<TermSubstitutionBinding> leftBindings, List<TermSubstitutionBinding> rightBindings)
   {
      for (TermSubstitutionBinding outerBinding : leftBindings) {
         for (TermSubstitutionBinding innerBinding : rightBindings) {
            if (outerBinding == null) {
               mPartialBindings.add(null);
            }
            else if (innerBinding == null) {
               mPartialBindings.add(null);
            }
            else {
               try {
                  mPartialBindings.add(combine(copy(outerBinding), copy(innerBinding)));
               }
               catch (UnificationException e) {
                  mPartialBindings.add(null);
               }
            }
         }
      }
   }

   private static TermSubstitutionBinding combine(TermSubstitutionBinding outerBinding, TermSubstitutionBinding innerBinding) throws UnificationException
   {
      for (IVariable var : innerBinding.getVariables()) {
         if (outerBinding.isBound(var)) {
            ITerm term1 = outerBinding.getTerm(var);
            ITerm term2 = innerBinding.getTerm(var);
            TermSubstitutionBinding newBinding = Unifier.findSubstitution(term1, term2);
            append(newBinding, outerBinding);
         }
         else {
            outerBinding.put(var, innerBinding.getTerm(var));
         }
      }
      return outerBinding;
   }

   private static void append(TermSubstitutionBinding source, TermSubstitutionBinding target)
   {
      for (IVariable var : source.getVariables()) {
         target.put(var, source.getTerm(var));
      }
   }

   private void broadcastEnterOptionalScope()
   {
      mWithinOptionalScope = true;
   }

   private void broadcastExitOptionalScope()
   {
      mWithinOptionalScope = false;
   }

   protected void visitTupleExpression(TripleAtom tuple)
   {
      collectQueryVariables(tuple);
      String identifier = getNextIdentifier();
      List<TermSubstitutionBinding> partialBindings = getPartialBindings();
      
      /*
       * Find candidate mappings that are matched to the given query tuple atom.
       */
      for (IMapping mapping : findMappings(tuple)) {
         
         /*
          * Create a copy for every matched mapping before continue processing.
          */
         TripleAtom targetAtom = copy(mapping.getTargetAtom());
         SqlQuery sourceQuery = copy(mapping.getSourceQuery());
         
         /*
          * Refresh the column variables in the target atom using the specified identifier.
          */
         refreshVariables(targetAtom, identifier);
         
         /*
          * Refresh the column variables in the source query using the specified identifier.
          */
         ISqlExpression partialAnswer = preparePartialAnswer(sourceQuery, identifier);
         
         for (TermSubstitutionBinding phi : partialBindings) {
            
            if (phi == null) {
               /*
                * When phi = null, the algorithm continues preserving this value and iterate
                * to the next phi value.
                */
               collectPartialAnswer(mPartialAnswers, null);
               collectPartialBinding(mPartialBindings, null);
               continue;
            }
            
            try {
               /*
                * Apply the unifier to the tuple expression
                */
               TripleAtom tupleAtom = applyUnifier(tuple, phi);
               
               /*
                * Check if the tuple atom (from query) and target atom (from mapping) can be
                * unified.
                */
               TermSubstitutionBinding theta = Unifier.findSubstitution(targetAtom, tupleAtom);
               
               /*
                * If a unifier substitution was found then the tuple atom can be replaced (or
                * "unfolded") by the mapping's source query.
                */
               collectPartialAnswer(mPartialAnswers, partialAnswer);
               
               /*
                * Compose the unifiers phi and theta to construct the concluding unifier.
                */
               collectPartialBinding(mPartialBindings, compose(phi, theta));
            }
            catch (UnificationException e) {
               /*
                * If findSubstitution() got failed, e.g., mismatch URI template or mismatch
                * constant value then put the partial answer and partial bindings as null.
                */
               collectPartialAnswer(mPartialAnswers, null);
               collectPartialBinding(mPartialBindings, null);
            }
         }
      }
   }

   /*
    * Private utility methods
    */

   private void reset()
   {
      mQueryVars.clear();
      mPartialBindings.clear();
      mPartialAnswers.clear();
      mExcludeVariable.clear();
   }

   private void initPartialBindings()
   {
      mPartialBindings.add(TermSubstitutionBinding.createEmptyBinding());
   }

   private void initIdentifierGenerator()
   {
      mNameGenerator.reset();
   }

   private void initExcludeVariableList(IQueryExt query)
   {
      for (IFunction filter : query.getFilters()) {
         for (ITerm term : filter.getParameters()) {
            if (term instanceof IVariable) {
               mExcludeVariable.add(TermUtils.asVariable(term));
            }
         }
      }
   }

   private List<TermSubstitutionBinding> getPartialBindings()
   {
      List<TermSubstitutionBinding> toReturn = new ArrayList<TermSubstitutionBinding>();
      CollectionUtils.move(mPartialBindings, toReturn);
      return toReturn;
   }

   private List<ISqlExpression> getPartialAnswers(IAtom queryAssertion)
   {
      queryAssertion.accept(this);
      List<ISqlExpression> toReturn = new ArrayList<ISqlExpression>();
      CollectionUtils.move(mPartialAnswers, toReturn);
      return toReturn;
   }

   private void collectQueryVariables(TripleAtom tuple)
   {
      if (mWithinOptionalScope) { // == within the OPTIONAL scope, ignore variable
         return;
      }
      ITerm subject = TripleAtom.getSubject(tuple);
      if (subject instanceof IVariable) {
         IVariable subjectVar = TermUtils.asVariable(subject);
         if (!mExcludeVariable.contains(subjectVar)) {
            mQueryVars.add(subjectVar);
         }
      }
      ITerm object = TripleAtom.getObject(tuple);
      if (object instanceof IVariable) {
         IVariable objectVar = TermUtils.asVariable(object);
         if (!mExcludeVariable.contains(objectVar)) {
            mQueryVars.add(objectVar);
         }
      }
   }

   private Set<IVariable> getQueryVariables()
   {
      return mQueryVars;
   }

   private void refreshVariables(final TripleAtom targetAtom, final String viewName)
   {
      targetAtom.accept(new AtomVisitorAdapter()
      {
         @Override
         public void visit(IAtom atom)
         {
            if (atom instanceof TripleAtom) {
               TripleAtom targetAtom = (TripleAtom) atom;
               ITerm subject = TripleAtom.getSubject(targetAtom);
               subject.accept(this);
               ITerm object = TripleAtom.getObject(targetAtom);
               object.accept(this);
            }
         }
         @Override
         public void visit(IVariable variable)
         {
            if (variable instanceof SqlColumn) {
               SqlColumn column = (SqlColumn) variable;
               column.setViewName(viewName); // refresh variable name
            }
         }
         @Override
         public void visit(IFunction function)
         {
            for (ITerm parameter : function.getParameters()) {
               parameter.accept(this);
            }
         }
      });
   }

   private ISqlExpression preparePartialAnswer(SqlQuery sourceQuery, String identifier)
   {
      if (isSimpleQuery(sourceQuery)) {
         /*
          * If the source query contains only a single table then assign this table with
          * the identifier.
          */
         SqlTable table = sourceQuery.getAllTables().get(0);
         table.setAliasName(identifier);
         sourceQuery.changeAllColumnNamespace(table.getTableName(), identifier);
         return sourceQuery.getFromExpression(); // returns ISqlTable
      }
      else if (isUserQuery(sourceQuery)) {
         /*
          * If the source query contains a UserQuery object, i.e., a verbatim-unprocessed
          * SQL query string treated as a sub-query.
          */
         SqlUserQuery userQuery = (SqlUserQuery) sourceQuery.getFromExpression();
         userQuery.setViewName(identifier);
         sourceQuery.changeAllColumnNamespace(userQuery.getViewName(), identifier);
         return sourceQuery.getFromExpression();
      }
      else {
         /*
          * The algorithm will use the full-qualified names as its ID so alias name is no
          * longer necessary.
          */
         for (SqlSelectItem selectItem : sourceQuery.getSelectItems()) {
            selectItem.removeAliasName();
         }
         /*
          * If the source query contains complex expressions, e.g., joins or filters
          * then all involving tables must be assigned to a new (inner) identifier.
          */
         for (SqlTable table : sourceQuery.getAllTables()) {
            String nextIdentifier = getNextIdentifier();
            table.setAliasName(nextIdentifier);
            sourceQuery.changeAllColumnNamespace(table.getTableName(), nextIdentifier);
         }
         /*
          * Return the original source query with the assigned identifier.
          */
         return new SqlSubQuery(sourceQuery, identifier);
      }
   }

   private static boolean isSimpleQuery(ISqlQuery sourceQuery)
   {
      ISqlExpression fromExpression = sourceQuery.getFromExpression();
      return ((fromExpression instanceof SqlTable) && !sourceQuery.hasWhereExpression()) ? true : false;
   }

   private static boolean isUserQuery(SqlQuery sourceQuery)
   {
      ISqlExpression fromExpression = sourceQuery.getFromExpression();
      return (fromExpression instanceof SqlUserQuery) ? true : false;
   }

   private void collectPartialAnswer(List<ISqlExpression> partialAnswers, ISqlExpression partialAnswer)
   {
      partialAnswers.add(partialAnswer);
   }

   private void collectPartialBinding(List<TermSubstitutionBinding> partialBindings, TermSubstitutionBinding partialBinding)
   {
      partialBindings.add(partialBinding);
   }

   private static List<String> scope(ISqlExpression expression)
   {
      List<String> toReturn = new ArrayList<String>();
      findScope(expression, toReturn);
      return toReturn;
   }

   private static void findScope(ISqlExpression expression, List<String> toReturn)
   {
      if (expression instanceof SqlJoin) {
         SqlJoin join = (SqlJoin) expression;
         findScope(join.getLeftExpression(), toReturn);
         findScope(join.getRightExpression(), toReturn);
      }
      else if (expression instanceof SqlTable) {
         SqlTable table = (SqlTable) expression;
         toReturn.add(table.getAliasName());
      }
      else if (expression instanceof SqlSubQuery) {
         SqlSubQuery subQuery = (SqlSubQuery) expression;
         toReturn.add(subQuery.getViewName());
      }
   }

   /*
    * Construct the join conditions (i.e., the ON expression) from the partial binding.
    * If the binding substitutes an SQL column object with another SQL column object
    * then this binding is a table join condition in SQL. In addition, this binding will
    * be removed from the partial binding cache.
    */
   private Set<SqlJoinCondition> joinConditions(TermSubstitutionBinding binding, List<String> joinScope)
   {
      Set<SqlJoinCondition> toReturn = CollectionUtils.createEmptySet(SqlJoinCondition.class);
      List<IVariable> removeList = new ArrayList<IVariable>();
      for (IVariable var : binding.getVariables()) {
         ITerm term = binding.getTerm(var);
         if (var instanceof SqlColumn && term instanceof SqlColumn) {
            SqlColumn c1 = (SqlColumn) var;
            SqlColumn c2 = (SqlColumn) term;
            if (withinScope(c1, joinScope) && withinScope(c2, joinScope)) {
               toReturn.add(new SqlJoinCondition(c1, c2));
               removeList.add(var); // record the binding for removal
            }
         }
      }
      for (IVariable var : removeList) {
         binding.remove(var);
      }
      return toReturn;
   }

   /*
    * Construct the join filters (i.e., append the ON expression) from the partial binding.
    * If the binding substitutes an SQL column object with a constant and the column belongs
    * to the join scope then it is a join filter. In addition, this binding will be removed
    * from the partial binding cache.
    */
   private Set<ISqlExpression> joinFilters(TermSubstitutionBinding binding, IFunction filter, List<String> filterScope)
   {
      Set<ISqlExpression> toReturn = CollectionUtils.createEmptySet(ISqlExpression.class);
      
      List<IVariable> removeList = new ArrayList<IVariable>();
      for (IVariable var : binding.getVariables()) {
         ITerm term = binding.getTerm(var);
         if (var instanceof SqlColumn && term instanceof Literal) {
            SqlColumn c = (SqlColumn) var;
            if (withinScope(c, filterScope)) {
               toReturn.add(new SqlEqualsTo(c, getSqlExpression(term)));
               removeList.add(var); // record the binding for removal
            }
         }
      }
      for (IVariable var : removeList) {
         binding.remove(var);
      }
      
      if (filter != null) {
         filter.apply(binding);
         toReturn.add(getSqlExpression(filter));
      }
      return toReturn;
   }

   private static boolean withinScope(SqlColumn c, List<String> joinScope)
   {
      return joinScope.contains(c.getViewName()) ? true : false;
   }

   private static SqlJoin createSqlInnerJoin(ISqlExpression leftExpression, ISqlExpression rightExpression, Set<SqlJoinCondition> joinConditions, Set<ISqlExpression> joinFilters)
   {
      SqlJoin sqlJoin = new SqlJoin();
      sqlJoin.setInnerJoin(true);
      sqlJoin.setLeftExpression(leftExpression);
      sqlJoin.setRightExpression(rightExpression);
      sqlJoin.addJoinConditions(joinConditions);
      sqlJoin.addFilters(joinFilters);
      return sqlJoin;
   }

   private static SqlJoin createSqlLeftJoin(ISqlExpression leftExpression, ISqlExpression rightExpression, Set<SqlJoinCondition> joinConditions, Set<ISqlExpression> joinFilters)
   {
      SqlJoin sqlJoin = new SqlJoin();
      sqlJoin.setLeftJoin(true);
      sqlJoin.setLeftExpression(leftExpression);
      sqlJoin.setRightExpression(rightExpression);
      sqlJoin.addJoinConditions(joinConditions);
      sqlJoin.addFilters(joinFilters);
      return sqlJoin;
   }

   private String getNextIdentifier()
   {
      return mNameGenerator.getNextUniqueName();
   }

   private Set<IMapping> findMappings(TripleAtom tupleExpression)
   {
      URI signature = getTupleSignature(tupleExpression);
      Set<IMapping> matchedMappings = mMappingSet.get(signature);
      if (matchedMappings.isEmpty()) {
         throw new SemantikaRuntimeException("No mapping found for assertion: " + signature); //$NON-NLS-1$
      }
      return matchedMappings;
   }

   private URI getTupleSignature(TripleAtom expr)
   {
      IUriReference predicate = TermUtils.asUriReference(TripleAtom.getPredicate(expr));
      URI signature = UriReference.getUri(predicate);
      
      /*
       * If the expression is a class expression (i.e., predicate uses rdf:type)
       */
      if (signature.equals(RDF_TYPE)) {
         IUriReference object = TermUtils.asUriReference(TripleAtom.getObject(expr));
         signature = UriReference.getUri(object);
      }
      return signature;
   }

   /*
    * Private static utility methods
    */

   private TermSubstitutionBinding compose(TermSubstitutionBinding phi, TermSubstitutionBinding theta)
   {
      TermSubstitutionBinding toReturn = copy(phi); // make copy
      toReturn.compose(theta);
      return toReturn;
   }

   private static TripleAtom applyUnifier(TripleAtom tupleExpression, TermSubstitutionBinding binding)
   {
      if (binding == null) {
         throw new NullPointerException("No binding was found"); //$NON-NLS-1$
      }
      TripleAtom toReturn = copy(tupleExpression); // make copy
      toReturn.apply(binding);
      return toReturn;
   }

   private static ISqlExpression copy(ISqlExpression expression)
   {
      return (ISqlExpression) Serializer.copy(expression);
   }

   private static TripleAtom copy(TripleAtom expr)
   {
      return (TripleAtom) Serializer.copy(expr);
   }

   private static SqlQuery copy(SqlQuery sqlQuery)
   {
      return (SqlQuery) Serializer.copy(sqlQuery);
   }

   private static TermSubstitutionBinding copy(TermSubstitutionBinding binding)
   {
      TermSubstitutionBinding toReturn = TermSubstitutionBinding.createEmptyBinding();
      for (IVariable var : binding.getVariables()) {
         IVariable varCopy = TermUtils.copy(var);
         ITerm termCopy = TermUtils.copy(binding.getTerm(var));
         toReturn.put(varCopy, termCopy);
      }
      return toReturn;
   }

   private SqlSelectItem selectItem(ITerm term, TermSubstitutionBinding binding)
   {
      if (term instanceof IVariable) {
         IVariable var = (IVariable) term;
         if (binding.isBound(var)) {
            ITerm replacement = binding.getTerm(var);
            ISqlExpression expression = getSqlExpression(replacement);
            SqlSelectItem selectItem = new SqlSelectItem(expression);
            selectItem.setAliasName(var.getName());
            return selectItem;
         }
      }
      throw new SemantikaRuntimeException("Unexpected term type found " + term.getClass().toString()); //$NON-NLS-1$
   }

   private ISqlExpression getSqlExpression(ITerm term)
   {
      return mConverter.toSqlExpression(term);
   }

   private void assignValueFilters(IVariable var, ITerm term, SqlQuery sqlQuery)
   {
      if (var instanceof SqlColumn && term instanceof ILiteral) {
         SqlColumn column = (SqlColumn) var;
         SqlValue value = mConverter.toSqlValue((ILiteral) term);
         SqlFunction filter = sSqlFactory.createEqualsToExpression(column, value);
         sqlQuery.addWhereExpression(filter);
      }
   }

   private void assignNotNullFilters(ITerm term, SqlQuery sqlQuery)
   {
      if (term instanceof IVariable) {
         ISqlExpression expression = mConverter.toSqlExpression(term);
         ISqlFunction notNullFilter = sSqlFactory.createIsNotNullExpression(expression);
         sqlQuery.addWhereExpression(notNullFilter);
      }
      else if (term instanceof IFunction) {
         IFunction function = ((IFunction) term);
         for (ITerm t : function.getParameters()) {
            assignNotNullFilters(t, sqlQuery);
         }
      }
   }
}
