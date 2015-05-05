/*
 * Copyright (c) 2013-2015 Josef Hardi <josef.hardi@gmail.com>
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import com.obidea.semantika.database.sql.base.ISqlExpression;
import com.obidea.semantika.database.sql.base.ISqlFunction;
import com.obidea.semantika.database.sql.base.ISqlJoin;
import com.obidea.semantika.database.sql.base.ISqlSubQuery;
import com.obidea.semantika.database.sql.base.ISqlTable;
import com.obidea.semantika.database.sql.base.SqlExpressionVisitorAdapter;
import com.obidea.semantika.database.sql.base.SqlJoinCondition;
import com.obidea.semantika.database.sql.base.SqlSelectItem;
import com.obidea.semantika.expression.base.QuerySet;
import com.obidea.semantika.mapping.base.sql.SqlBinaryFunction;
import com.obidea.semantika.mapping.base.sql.SqlColumn;
import com.obidea.semantika.mapping.base.sql.SqlEqualsTo;
import com.obidea.semantika.mapping.base.sql.SqlGreaterThan;
import com.obidea.semantika.mapping.base.sql.SqlGreaterThanEquals;
import com.obidea.semantika.mapping.base.sql.SqlJoin;
import com.obidea.semantika.mapping.base.sql.SqlLessThan;
import com.obidea.semantika.mapping.base.sql.SqlLessThanEquals;
import com.obidea.semantika.mapping.base.sql.SqlNotEqualsTo;
import com.obidea.semantika.mapping.base.sql.SqlQuery;
import com.obidea.semantika.mapping.base.sql.SqlSelectQuery;
import com.obidea.semantika.mapping.base.sql.SqlSubQuery;
import com.obidea.semantika.mapping.base.sql.SqlTable;
import com.obidea.semantika.mapping.base.sql.SqlUserQuery;
import com.obidea.semantika.util.LogUtils;
import com.obidea.semantika.util.Serializer;

public class QueryReducer extends SqlExpressionVisitorAdapter implements IOptimizer
{
   private SqlQuery mSqlQuery;
   private List<String> mViewExcludeList;

   private JoinConditionCache mJoinConditionCache = new JoinConditionCache();
   private JoinConditionCache mSecondaryCache = new JoinConditionCache();

   private JoinFilterCache mJoinFilterCache = new JoinFilterCache();
   private JoinFilterCache mSecondaryFilterCache = new JoinFilterCache();

   private Map<Integer, Map<String, SqlTable>> mTableRegistry = new HashMap<Integer, Map<String, SqlTable>>();
   private List<String> mConstantRegistry = new ArrayList<String>();
   private ISqlExpression mFromExpression = null;
   private Set<ISqlExpression> mQueryFilters = new HashSet<ISqlExpression>();

   private boolean mWithinLeftJoinScope = false;
   private int mScopeLevel = 0;

   private static final Logger LOG = LogUtils.createLogger("semantika.database"); //$NON-NLS-1$

   private void init(SqlQuery sqlQuery)
   {
      mSqlQuery = sqlQuery;
      mViewExcludeList = makeExcludeViewList(sqlQuery);
   }

   @Override
   public String getName()
   {
      return QueryReducer.class.toString();
   }

   @Override
   public QuerySet<SqlQuery> optimize(QuerySet<SqlQuery> querySet) throws QueryOptimizationException
   {
      QuerySet<SqlQuery> toReturn = new QuerySet<SqlQuery>();
      for (SqlQuery sqlQuery : querySet.getAll()) {
         toReturn.add(optimize(sqlQuery));
      }
      return toReturn;
   }

   @Override
   public SqlQuery optimize(SqlQuery sqlQuery) throws QueryOptimizationException
   {
      init(sqlQuery);
      
      sqlQuery.getFromExpression().accept(this);
      
      SqlQuery toReturn = new SqlSelectQuery(mSqlQuery.isDistinct());
      toReturn.setDistinct(mSqlQuery.isDistinct());
      for (SqlSelectItem selectItem : mSqlQuery.getSelectItems()) {
         toReturn.addSelectItem(selectItem);
      }
      toReturn.setFromExpression(mFromExpression);
      for (ISqlExpression filter : mSqlQuery.getWhereExpression()) {
         toReturn.addWhereExpression(filter);
      }
      for (ISqlExpression additionalFilter : mQueryFilters) {
         toReturn.addWhereExpression(additionalFilter);
      }
      if (!mSecondaryFilterCache.isEmpty()) {
         
      }
      return toReturn;
   }

   @Override
   public void visit(ISqlTable table)
   {
      if (table instanceof SqlTable) {
         visitTableExpression((SqlTable) table);
      }
   }

   private void visitTableExpression(SqlTable tableExpression)
   {
      mFromExpression = tableExpression;
   }

   @Override
   public void visit(ISqlJoin expression)
   {
      if (expression instanceof SqlJoin) {
         SqlJoin join = (SqlJoin) expression;
         if (join.isInnerJoin()) {
            visitInnerJoinExpression(join);
         }
         else if (join.isLeftJoin()) {
            visitLeftJoinExpression(join);
         }
      }
   }

   private void visitInnerJoinExpression(SqlJoin join)
   {
      ISqlExpression leftExpression = getJoinNodeExpression(join.getLeftExpression());
      
      /*
       * Collect the join conditions of the current visited join expression before
       * visiting another join node.
       */
      cacheJoinConditions(join.getJoinConditions());
      cacheJoinFilters(join.getFilters());
      
      ISqlExpression rightExpression = getJoinNodeExpression(join.getRightExpression());
      
      ISqlExpression leftEvaluatedExpression = evaluateExpression(leftExpression);
      ISqlExpression rightEvaluatedExpression = evaluateExpression(rightExpression);
      
      boolean isReduced = createReducedExpression(leftEvaluatedExpression, rightEvaluatedExpression);
      if (!isReduced) {
         createInnerJoinExpression(leftEvaluatedExpression, rightEvaluatedExpression);
      }
   }

   private void visitLeftJoinExpression(SqlJoin join)
   {
      ISqlExpression leftExpression = getJoinNodeExpression(join.getLeftExpression());
      
      /*
       * Collect the join conditions of the current visited join expression before
       * visiting another join node.
       */
      cacheJoinConditions(join.getJoinConditions());
      cacheJoinFilters(join.getFilters());
      
      broadcastEnterLeftJoinScope();
      ISqlExpression rightExpression = getJoinNodeExpression(join.getRightExpression());
      
      ISqlExpression leftEvaluatedExpression = evaluateExpression(leftExpression);
      ISqlExpression rightEvaluatedExpression = evaluateExpression(rightExpression);
      
      boolean isReduced = createReducedExpression(leftEvaluatedExpression, rightEvaluatedExpression);
      if (!isReduced) {
         createLeftJoinExpression(leftEvaluatedExpression, rightEvaluatedExpression);
      }
      broadcastExitLeftJoinScope();
   }

   private void broadcastEnterLeftJoinScope()
   {
      mWithinLeftJoinScope = true;
      mScopeLevel++;
   }

   private void broadcastExitLeftJoinScope()
   {
      mWithinLeftJoinScope = false;
      mScopeLevel--;
   }

   private ISqlExpression getJoinNodeExpression(ISqlExpression expression)
   {
      expression.accept(this);
      ISqlExpression joinNodeExpression = mFromExpression;
      return (ISqlExpression) Serializer.copy(joinNodeExpression);
   }

   /**
    * The algorithm requires the join condition in each join expression visit.
    */
   private void cacheJoinConditions(Set<SqlJoinCondition> joinConditions)
   {
      mJoinConditionCache.put(joinConditions);
   }

   private void cacheJoinFilters(Set<ISqlExpression> joinFilters)
   {
      mJoinFilterCache.put(joinFilters);
   }

   @Override
   public void visit(ISqlSubQuery subQueryExpression)
   {
      if (subQueryExpression instanceof SqlSubQuery) {
         visitSubQuery((SqlSubQuery) subQueryExpression);
      }
      else if (subQueryExpression instanceof SqlUserQuery) {
         visitUserQuery((SqlUserQuery) subQueryExpression);
      }
   }

   private void visitSubQuery(SqlSubQuery subQuery)
   {
      try {
         ISqlExpression innerExpression = unpack(subQuery);
         innerExpression.accept(this);
      }
      catch (Exception e) {
         /*
          * If an exception occurred while unpacking the sub-query then just give up
          * and use the whole sub-query.
          */
         LOG.warn("Unable to optimize sub-query: " + e.getMessage()); //$NON-NLS-1$
         mFromExpression = subQuery;
      }
   }

   private void visitUserQuery(SqlUserQuery userQuery)
   {
      mFromExpression = userQuery;
   }

   /**
    * Unpacking a sub-query means to get the original query expression inside the
    * SubQuery wrapper.
    *
    * Observe the example below:
    *
    *   ...
    *   FROM (
    *      SELECT V2.c1, V2.c2, V3.c6
    *      FROM A as V2
    *      JOIN B as V3 on V2.c1 = V3.c5
    *      WHERE V3.c6 > 1000
    *   ) as V1
    *   ...
    *
    * or written in "modified"-Prolog notation:
    *
    *   SubQuery(
    *      q(c1, c2, c6) :-
    *      Join(A(c1, c2, c3)["V2"],
    *           B(c4, c5, c6)["V3"], &eq(c1, c5)),
    *      &gt(c6,1000)
    *   )["V1"]
    *
    * Calling the unpack() method returns:
    *
    *   ...
    *   FROM A as V2
    *   JOIN B as V3 on V2.c1 = V3.c5
    *   ...
    *
    * or written in "modified"-Prolog notation:
    *
    *   Join(A(c1, c2, c3)["V2"], B(c4, c5, c6)["V3"], &eq(c1, c5))
    *
    * There are some other details to notice:
    * - The expression '&gt(c6,1000)' is stored as a global query filter, i.e.,
    *   will be mention later in the query WHERE clause.
    * - The naming for columns `V1.c1`, V1.c2` and `V1.c6` used at the outside
    *   of the sub-query needs to be renamed accordingly, i.e., `V2.c1`, V2.c2`
    *   and `V3.c6`.
    */
   private ISqlExpression unpack(SqlSubQuery subQuery) throws Exception
   {
      SqlQuery innerQuery = (SqlQuery) subQuery.getQuery();
      
      /*
       * Do column renaming in the parent query to avoid obsolete view name.
       */
      String targetView = subQuery.getViewName();
      for (SqlSelectItem selectItem : innerQuery.getSelectItems()) {
         ISqlExpression expression = selectItem.getExpression();
         if (expression instanceof SqlColumn) {
            SqlColumn innerColumn = (SqlColumn) expression;
            String targetColumn = innerColumn.getColumnName();
            String innerViewName = innerColumn.getViewName();
            mSqlQuery.changeColumnNamespace(targetColumn, targetView, innerViewName);
         }
      }
      
      /*
       * If the sub-query contains filters (e.g., &gt(c6,1000)) then add these filters
       * to the global WHERE filters in the parent query.
       */
      if (innerQuery.hasWhereExpression()) {
         if (mWithinLeftJoinScope) {
            mJoinFilterCache.put(innerQuery.getWhereExpression());
         }
         else {
            mQueryFilters.addAll(innerQuery.getWhereExpression());
         }
      }
      
      /*
       * The unpacking is done by returning the FROM expression of the inner query.
       */
      return innerQuery.getFromExpression();
   }

   /**
    * If the expression is a SQL table then check if such table has existed already
    * in the query. If true then eliminate it using PrimaryKey optimization.
    */
   private ISqlExpression evaluateExpression(ISqlExpression expression)
   {
      int scopeId = getScopeLevel();
      if (expression instanceof SqlTable) {
         SqlTable table = (SqlTable) expression;
         String tableName = table.getTableName();
         Map<String, SqlTable> localRegistry = mTableRegistry.get(scopeId);
         if (localRegistry == null) {
            localRegistry = new HashMap<String, SqlTable>();
            mTableRegistry.put(scopeId, localRegistry);
         }
         if (localRegistry.containsKey(tableName)) {
            SqlTable existingTable = localRegistry.get(tableName);
            SqlTable currentTable = table;
            table = elimiateTableWhenPossible(currentTable, existingTable);
         }
         else {
            localRegistry.put(tableName, table);
            mViewExcludeList.add(table.getAliasName());
         }
         return table;
      }
      return expression;
   }

   private int getScopeLevel()
   {
      return mScopeLevel;
   }

   private SqlTable elimiateTableWhenPossible(SqlTable currentTable, SqlTable existingTable)
   {
      String currentTableName = currentTable.getTableName();
      String currentViewName = currentTable.getAliasName();
      String existingViewName = existingTable.getAliasName();
      
      /*
       * If the current table has a view name that belongs to the exclude list then perform no reduction
       * i.e., return back the current table object.
       */
      if (mViewExcludeList.contains(currentViewName)) {
         return currentTable;
      }
      
      /*
       * If the current table expression produces a constant then ignore this expression by eliminating it.
       * However, only the successive join-with-constant expressions that are eliminated.
       */
      boolean hasConstant = checkJoinWithConstant();
      if (hasConstant) {
         if (mConstantRegistry.contains(currentTableName)) {
            mSqlQuery.changeAllColumnNamespace(currentViewName, existingViewName); //$NON-NLS-1$
            return null;
         }
         else {
            mConstantRegistry.add(currentTableName);
            return currentTable;
         }
      }
      
      /*
       * Perform join optimization on Primary Key constraint. The optimization works by observing
       * the join conditions of both tables. If the conditions use primary key to join both tables
       * then one of the table can be eliminated.
       */
      Set<SqlJoinCondition> pkConditionSet = new HashSet<SqlJoinCondition>();
      boolean allowReduction = checkReductionOnPrimaryKeyOptimization(currentTable, existingTable, pkConditionSet);
      if (allowReduction) {
         for (SqlJoinCondition joinCondition : pkConditionSet) {
            SqlColumn c1 = (SqlColumn) joinCondition.getLeftColumn();
            SqlColumn c2 = (SqlColumn) joinCondition.getRightColumn();
            if (c1.getViewName().equals(currentViewName)) {
               /*
                * An empty string in the second argument indicates the inclusion of ANY columns with
                * the view name mentioned in the first argument.
                */
               mSqlQuery.changeAllColumnNamespace(c1.getViewName(), c2.getViewName()); //$NON-NLS-1$
            }
            else if (c2.getViewName().equals(currentViewName)) {
               mSqlQuery.changeAllColumnNamespace(c2.getViewName(), c1.getViewName()); //$NON-NLS-1$
            }
         }
         return null;
      }
      
      /*
       * Give up, nothing to reduce.
       */
      return currentTable;
   }

   /**
    * Returns <code>true</code> if the table expression in a join produces a constant rather than
    * a set of tuples. This is a very optimistic check looking at the nature of the unfolded query,
    * i.e., if the join condition is empty then the table expression produces a constant.
    *
    * A table expression produces a constant when the associated filter expression in the where clause
    * uses equality in its primary key.
    *
    * Observe the example below:
    *
    *   SELECT *
    *   FROM A as V1
    *   JOIN A as V2 on TRUE
    *   JOIN B as V3 on V2.ID = V3.ID
    *   WHERE V2.ID = 10001
    *
    * and
    *
    *   SELECT *
    *   FROM A as V1
    *   JOIN A as V2 on TRUE
    *   JOIN B as V3 on V2.ID = V3.ID
    *   JOIN A as V4 on TRUE
    *   JOIN A as V5 on TRUE
    *   WHERE V2.ID = 10001
    *   WHERE V4.ID = 10001
    *   WHERE V5.ID = 10001
    *
    * Both queries produce a same number of tuples because the increasing numbers of JOIN-Table A
    * do not change the total permutation of tuples. The Join-Table A produces a constant of 1 tuple.
    */
   private boolean checkJoinWithConstant()
   {
      if (mJoinConditionCache.top().isEmpty() && !mJoinFilterCache.top().isEmpty()) {
         return true;
      }
      return false;
   }

   /**
    * Returns <code>true</code> if the join relation between the two tables, i.e., <code>currentTable</code>
    * and <code>existingTable</code> can be reduced by eliminating the <code>currentTable</code>.
    */
   private boolean checkReductionOnPrimaryKeyOptimization(SqlTable currentTable, SqlTable existingTable, Set<SqlJoinCondition> conditionSet)
   {
      String currentViewName = currentTable.getAliasName();
      int primaryKeySize = existingTable.asDatabaseObject().getPrimaryKey().getKeys().size();
      
      for (Set<SqlJoinCondition> joinConditions : mJoinConditionCache) {
         /*
          * For each set of join conditions, do a PK join condition checking to see if the
          * two columns in the condition belong to the same table and both are primary keys.
          * If the answer is true then collect this join condition into a temporary set.
          *
          * Note that due to the arbitrary selection of the set of join conditions, the 
          * method needs to check first if either one of the columns in the join condition
          * has the same view name as the current table in question.
          */
         conditionSet.clear();
         for (SqlJoinCondition joinCondition : joinConditions) {
            SqlColumn c1 = (SqlColumn) joinCondition.getLeftColumn();
            SqlColumn c2 = (SqlColumn) joinCondition.getRightColumn();
            if (isConsistent(c1, currentViewName) || isConsistent(c2, currentViewName)) {
               if (c1.isEquivalent(c2) && c1.asDatabaseObject().isPrimaryKey() && c2.asDatabaseObject().isPrimaryKey()) {
                  conditionSet.add(joinCondition);
               }
            }
         }
         /*
          * For each PK join conditions checking, if the collected join conditions cover all
          * the primary keys then return all these collected conditions to the caller for
          * next processing. Otherwise, repeat the checking. Note: the method returns boolean
          * value to give an answer whether PK optimization is possible to perform or not.
          */
         if (conditionSet.size() == primaryKeySize) {
            return true;
         }
      }
      return false;
   }

   private boolean isConsistent(SqlColumn column, String currentViewName)
   {
      return currentViewName.equals(column.getViewName());
   }

   private boolean createReducedExpression(ISqlExpression leftExpression, ISqlExpression rightExpression)
   {
      if (leftExpression == null) {
         mFromExpression = rightExpression;
         mSecondaryCache.put(getJoinConditionsFromCache());
         if (!mWithinLeftJoinScope) {
            mQueryFilters.addAll(getJoinFiltersFromCache());
         }
         else {
            mSecondaryFilterCache.put(getJoinFiltersFromCache());
         }
         return true;
      }
      else if (rightExpression == null) {
         mFromExpression = leftExpression;
         mSecondaryCache.put(getJoinConditionsFromCache());
         if (!mWithinLeftJoinScope) {
            mQueryFilters.addAll(getJoinFiltersFromCache());
         }
         else {
            mSecondaryFilterCache.put(getJoinFiltersFromCache());
         }
         return true;
      }
      return false;
   }

   private void createInnerJoinExpression(ISqlExpression leftExpression, ISqlExpression rightExpression)
   {
      Set<SqlJoinCondition> conditionSet =  getJoinConditionsFromCache();
      Set<ISqlExpression> filterSet =  getJoinFiltersFromCache();
      
      while (!mSecondaryCache.isEmpty()) {
         conditionSet.addAll(mSecondaryCache.pop());
      }
      while (!mSecondaryFilterCache.isEmpty()) {
         filterSet.addAll(mSecondaryFilterCache.pop());
      }
      mFromExpression = createSqlInnerJoin(leftExpression, rightExpression, normalize(conditionSet), filterSet);
   }

   private void createLeftJoinExpression(ISqlExpression leftExpression, ISqlExpression rightExpression)
   {
      Set<SqlJoinCondition> conditionSet =  getJoinConditionsFromCache();
      Set<ISqlExpression> filterSet =  getJoinFiltersFromCache();
      
      while (!mSecondaryCache.isEmpty()) {
         conditionSet.addAll(mSecondaryCache.pop());
      }
      while (!mSecondaryFilterCache.isEmpty()) {
         filterSet.addAll(mSecondaryFilterCache.pop());
      }
      mFromExpression = createSqlLeftJoin(leftExpression, rightExpression, normalize(conditionSet), filterSet);
   }

   /**
    * The algorithm ensures the current join condition is on the top of the cache. Therefore,
    * this method just take the head of the cache.
    */
   private Set<SqlJoinCondition> getJoinConditionsFromCache()
   {
      return mJoinConditionCache.pop();
   }

   private Set<ISqlExpression> getJoinFiltersFromCache()
   {
      return mJoinFilterCache.pop();
   }

   /**
    * Removes join condition with same columns as arguments, e.g., <code>VIEW_1.column1 = VIEW_1.column1</code>.
    * This case can occur as a side effect of PK optimization, i.e., when a join is reduced the affected
    * columns are renamed following the primary table, including the ones inside the join condition.
    */
   private static Set<SqlJoinCondition> normalize(Set<SqlJoinCondition> conditionSet)
   {
      Set<SqlJoinCondition> toReturn = new HashSet<SqlJoinCondition>();
      for (SqlJoinCondition joinCondition : conditionSet) {
         SqlColumn c1 = (SqlColumn) joinCondition.getLeftColumn();
         SqlColumn c2 = (SqlColumn) joinCondition.getRightColumn();
         if (!c1.equals(c2)) {
            toReturn.add(joinCondition);
         }
      }
      return toReturn;
   }

   private SqlJoin createSqlInnerJoin(ISqlExpression leftExpression, ISqlExpression rightExpression, Set<SqlJoinCondition> joinConditions, Set<ISqlExpression> joinFilters)
   {
      SqlJoin join = new SqlJoin();
      join.setInnerJoin(true);
      join.setLeftExpression(leftExpression);
      join.setRightExpression(rightExpression);
      join.addJoinConditions(joinConditions);
      join.addFilters(joinFilters);
      return join;
   }

   private SqlJoin createSqlLeftJoin(ISqlExpression leftExpression, ISqlExpression rightExpression, Set<SqlJoinCondition> joinConditions, Set<ISqlExpression> joinFilters)
   {
      SqlJoin join = new SqlJoin();
      join.setLeftJoin(true);
      join.setLeftExpression(leftExpression);
      join.setRightExpression(rightExpression);
      join.addJoinConditions(joinConditions);
      join.addFilters(joinFilters);
      return join;
   }

   /**
    * Make an exclude list that consists of table's view names. The list comes from the
    * existing filters in WHERE statement.
    *
    * Observe the example below:
    *
    *   SELECT V1.c1, V1.c2, V3.c4, V3.c5
    *   FROM T1 as V1
    *   JOIN T1 as V2 on V1.c1 = V2.c1
    *   JOIN T2 as V3 on V2.c1 = V3.c6
    *   WHERE V2.c2 = V3.c5
    *
    * Note that `c1` is a primary key for T1. The output of this method will produce an
    * exclude list that contains "V2" and "V3". Without the exclude list, the QueryReducer
    * algorithm will eliminate V2 from the query to optimize. However, the list will
    * prevent such elimination in the algorithm because V2 has a "relation" to V3 in order
    * to produce the right answers.
    */
   private List<String> makeExcludeViewList(SqlQuery query)
   {
      ExcludeViewHandler handler = new ExcludeViewHandler();
      for (ISqlExpression filter : query.getWhereExpression()) {
         filter.accept(handler);
      }
      return handler.getExcludeList();
   }

   /**
    * A utility class that holds information about the join conditions when the algorithm
    * traverses the <code>SqlJoin</code> object. It is mainly used for join reduction.
    */
   class JoinConditionCache implements Iterable<Set<SqlJoinCondition>>
   {
      private LinkedList<Set<SqlJoinCondition>> mJoinConditionList = new LinkedList<Set<SqlJoinCondition>>();

      public void put(Set<SqlJoinCondition> joinConditions)
      {
         mJoinConditionList.addFirst(joinConditions);
      }

      public Set<SqlJoinCondition> top()
      {
         return mJoinConditionList.peek();
      }

      public Set<SqlJoinCondition> pop()
      {
         return mJoinConditionList.pop();
      }

      public int size()
      {
         return mJoinConditionList.size();
      }

      public boolean isEmpty()
      {
         return mJoinConditionList.isEmpty();
      }

      @Override
      public Iterator<Set<SqlJoinCondition>> iterator()
      {
         return mJoinConditionList.iterator();
      }

      @Override
      public String toString()
      {
         return mJoinConditionList.toString();
      }
   }

   /**
    * A utility class that holds information about the join filters when the algorithm
    * traverses the <code>SqlJoin</code> object. It is mainly used for join reduction.
    */
   class JoinFilterCache implements Iterable<Set<ISqlExpression>>
   {
      private LinkedList<Set<ISqlExpression>> mJoinFiltersList = new LinkedList<Set<ISqlExpression>>();

      public void put(Set<ISqlExpression> joinFilters)
      {
         mJoinFiltersList.addFirst(joinFilters);
      }

      public Set<ISqlExpression> top()
      {
         return mJoinFiltersList.peek();
      }

      public Set<ISqlExpression> pop()
      {
         return mJoinFiltersList.pop();
      }

      public int size()
      {
         return mJoinFiltersList.size();
      }

      public boolean isEmpty()
      {
         return mJoinFiltersList.isEmpty();
      }

      @Override
      public Iterator<Set<ISqlExpression>> iterator()
      {
         return mJoinFiltersList.iterator();
      }

      @Override
      public String toString()
      {
         return mJoinFiltersList.toString();
      }
   }

   class ExcludeViewHandler extends SqlExpressionVisitorAdapter
   {
      private List<String> mExcludeList = new ArrayList<String>();

      public List<String> getExcludeList()
      {
         return mExcludeList;
      }

      @Override
      public void visit(ISqlFunction filter)
      {
         if (filter instanceof SqlBinaryFunction) {
            SqlBinaryFunction binaryFilter = (SqlBinaryFunction) filter;
            if ((binaryFilter instanceof SqlEqualsTo) 
                  || (binaryFilter instanceof SqlNotEqualsTo)
                  || (binaryFilter instanceof SqlGreaterThan)
                  || (binaryFilter instanceof SqlGreaterThanEquals)
                  || (binaryFilter instanceof SqlLessThan)
                  || (binaryFilter instanceof SqlLessThanEquals)) {
               ISqlExpression e1 = binaryFilter.getLeftParameterExpression();
               ISqlExpression e2 = binaryFilter.getRightParameterExpression();
               if (e1 instanceof SqlColumn && e2 instanceof SqlColumn) {
                  mExcludeList.add(((SqlColumn) e1).getViewName());
                  mExcludeList.add(((SqlColumn) e2).getViewName());
               }
            }
            else {
               binaryFilter.getLeftParameterExpression().accept(this);
               binaryFilter.getRightParameterExpression().accept(this);
            }
         }
      }
   }
}
