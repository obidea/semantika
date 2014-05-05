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
package com.obidea.semantika.queryanswer.parser;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Value;
import org.openrdf.query.algebra.Add;
import org.openrdf.query.algebra.And;
import org.openrdf.query.algebra.ArbitraryLengthPath;
import org.openrdf.query.algebra.Avg;
import org.openrdf.query.algebra.BNodeGenerator;
import org.openrdf.query.algebra.BindingSetAssignment;
import org.openrdf.query.algebra.Bound;
import org.openrdf.query.algebra.Clear;
import org.openrdf.query.algebra.Coalesce;
import org.openrdf.query.algebra.Compare;
import org.openrdf.query.algebra.Compare.CompareOp;
import org.openrdf.query.algebra.CompareAll;
import org.openrdf.query.algebra.CompareAny;
import org.openrdf.query.algebra.Copy;
import org.openrdf.query.algebra.Count;
import org.openrdf.query.algebra.Create;
import org.openrdf.query.algebra.Datatype;
import org.openrdf.query.algebra.DeleteData;
import org.openrdf.query.algebra.DescribeOperator;
import org.openrdf.query.algebra.Difference;
import org.openrdf.query.algebra.Distinct;
import org.openrdf.query.algebra.EmptySet;
import org.openrdf.query.algebra.Exists;
import org.openrdf.query.algebra.Extension;
import org.openrdf.query.algebra.ExtensionElem;
import org.openrdf.query.algebra.Filter;
import org.openrdf.query.algebra.FunctionCall;
import org.openrdf.query.algebra.Group;
import org.openrdf.query.algebra.GroupConcat;
import org.openrdf.query.algebra.GroupElem;
import org.openrdf.query.algebra.IRIFunction;
import org.openrdf.query.algebra.If;
import org.openrdf.query.algebra.In;
import org.openrdf.query.algebra.InsertData;
import org.openrdf.query.algebra.Intersection;
import org.openrdf.query.algebra.IsBNode;
import org.openrdf.query.algebra.IsLiteral;
import org.openrdf.query.algebra.IsNumeric;
import org.openrdf.query.algebra.IsResource;
import org.openrdf.query.algebra.IsURI;
import org.openrdf.query.algebra.Label;
import org.openrdf.query.algebra.Lang;
import org.openrdf.query.algebra.LangMatches;
import org.openrdf.query.algebra.LeftJoin;
import org.openrdf.query.algebra.Like;
import org.openrdf.query.algebra.ListMemberOperator;
import org.openrdf.query.algebra.Load;
import org.openrdf.query.algebra.LocalName;
import org.openrdf.query.algebra.MathExpr;
import org.openrdf.query.algebra.MathExpr.MathOp;
import org.openrdf.query.algebra.Max;
import org.openrdf.query.algebra.Min;
import org.openrdf.query.algebra.Modify;
import org.openrdf.query.algebra.Move;
import org.openrdf.query.algebra.MultiProjection;
import org.openrdf.query.algebra.Namespace;
import org.openrdf.query.algebra.Not;
import org.openrdf.query.algebra.Or;
import org.openrdf.query.algebra.Order;
import org.openrdf.query.algebra.OrderElem;
import org.openrdf.query.algebra.Projection;
import org.openrdf.query.algebra.ProjectionElem;
import org.openrdf.query.algebra.ProjectionElemList;
import org.openrdf.query.algebra.QueryModelNode;
import org.openrdf.query.algebra.QueryModelVisitor;
import org.openrdf.query.algebra.QueryRoot;
import org.openrdf.query.algebra.Reduced;
import org.openrdf.query.algebra.Regex;
import org.openrdf.query.algebra.SameTerm;
import org.openrdf.query.algebra.Sample;
import org.openrdf.query.algebra.Service;
import org.openrdf.query.algebra.SingletonSet;
import org.openrdf.query.algebra.Slice;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Str;
import org.openrdf.query.algebra.Sum;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.Union;
import org.openrdf.query.algebra.ValueConstant;
import org.openrdf.query.algebra.ValueExpr;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.ZeroLengthPath;

import com.obidea.semantika.datatype.DataType;
import com.obidea.semantika.expression.ExpressionObjectFactory;
import com.obidea.semantika.expression.base.ExpressionConstant;
import com.obidea.semantika.expression.base.IAtom;
import com.obidea.semantika.expression.base.IFunction;
import com.obidea.semantika.expression.base.ILiteral;
import com.obidea.semantika.expression.base.IQueryExt;
import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.expression.base.IUriReference;
import com.obidea.semantika.expression.base.IVariable;
import com.obidea.semantika.expression.base.Join;
import com.obidea.semantika.expression.base.QueryExt;
import com.obidea.semantika.expression.base.TermUtils;
import com.obidea.semantika.mapping.base.TripleAtom;
import com.obidea.semantika.util.Serializer;

public class SparqlQueryHandler implements QueryModelVisitor<SparqlParserException>
{
   private static ExpressionObjectFactory sExpressionFactory = ExpressionObjectFactory.getInstance();

   private IQueryExt mQueryExt;

   private ITerm mTerm;

   private List<IVariable> mVarList;

   private IAtom mQueryExpression;

   public SparqlQueryHandler()
   {
      mQueryExt = createEmptyQuery();
   }

   public IQueryExt getSparql()
   {
      mQueryExt.addAtom(getQueryBody());
      return mQueryExt;
   }

   protected IQueryExt createEmptyQuery()
   {
      return new QueryExt();
   }

   protected IVariable getVariable()
   {
      return TermUtils.asVariable(getTerm());
   }

   protected ILiteral getLiteral()
   {
      return TermUtils.asLiteral(getTerm());
   }

   protected IUriReference getUriReference()
   {
      return TermUtils.asUriReference(getTerm());
   }

   protected IFunction getFunction()
   {
      return TermUtils.asFunction(getTerm());
   }

   protected ITerm getTerm()
   {
      return TermUtils.copy(mTerm);
   }

   protected IAtom getQueryBody()
   {
      return (IAtom) Serializer.copy(mQueryExpression);
   }

   protected List<IVariable> getVariableList()
   {
      return mVarList;
   }

   @Override
   public void meet(QueryRoot arg0) throws SparqlParserException
   {
      // NO-OP
   }

   @Override
   public void meet(Add arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("SPARQL update string: ADD"); //$NON-NLS-1$
   }

   @Override
   public void meet(And arg0) throws SparqlParserException
   {
      arg0.getLeftArg().visit(this);
      ITerm termLeft = getTerm();
      arg0.getRightArg().visit(this);
      ITerm termRight = getTerm();
      mTerm = sExpressionFactory.formAnd(termLeft, termRight);
   }

   @Override
   public void meet(ArbitraryLengthPath arg0) throws SparqlParserException
   {
       // NO-OP
   }

   @Override
   public void meet(Avg arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("Aggregate algebra: AVG"); //$NON-NLS-1$
   }

   @Override
   public void meet(BindingSetAssignment arg0) throws SparqlParserException
   {
      // NO-OP
   }

   @Override
   public void meet(BNodeGenerator arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("Built-in call: BNODE"); //$NON-NLS-1$
   }

   @Override
   public void meet(Bound arg0) throws SparqlParserException
   {
      arg0.getArg().visit(this);
      ITerm arg = getTerm();
      mTerm = sExpressionFactory.formIsNotNull(arg);
   }

   @Override
   public void meet(Clear arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("SPARQL update string: CLEAR"); //$NON-NLS-1$      
   }

   @Override
   public void meet(Coalesce arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("Built-in call: COALESCE"); //$NON-NLS-1$
   }

   @Override
   public void meet(Compare arg0) throws SparqlParserException
   {
      arg0.getLeftArg().visit(this);
      ITerm termLeft = getTerm();
      arg0.getRightArg().visit(this);
      ITerm termRight = getTerm();
      
      CompareOp op = arg0.getOperator();
      switch (op) {
         case EQ: mTerm = sExpressionFactory.formEq(termLeft, termRight); break;
         case NE: mTerm = sExpressionFactory.formNeq(termLeft, termRight); break;
         case GT: mTerm = sExpressionFactory.formGt(termLeft, termRight); break;
         case GE: mTerm = sExpressionFactory.formGte(termLeft, termRight); break;
         case LT: mTerm = sExpressionFactory.formLt(termLeft, termRight); break;
         case LE: mTerm = sExpressionFactory.formLte(termLeft, termRight); break;
      }
   }

   @Override
   public void meet(CompareAll arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("Sub-query comparator: Compare All"); //$NON-NLS-1$
   }

   @Override
   public void meet(CompareAny arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("Sub-query comparator: Compare Any"); //$NON-NLS-1$
   }

   @Override
   public void meet(DescribeOperator arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("DESCRIBE"); //$NON-NLS-1$
   }

   @Override
   public void meet(Copy arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("SPARQL update string: COPY"); //$NON-NLS-1$
   }

   @Override
   public void meet(Count arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("Aggregate algebra: COUNT"); //$NON-NLS-1$
   }

   @Override
   public void meet(Create arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("SPARQL update string: CREATE"); //$NON-NLS-1$
   }

   @Override
   public void meet(Datatype arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("Built-in call: DATATYPE"); //$NON-NLS-1$
   }

   @Override
   public void meet(DeleteData arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("SPARQL update string: DELETE DATA"); //$NON-NLS-1$
   }

   @Override
   public void meet(Difference arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("MINUS"); //$NON-NLS-1$
   }

   @Override
   public void meet(Distinct arg0) throws SparqlParserException
   {
      mQueryExt.setDistinct(true);
      arg0.visitChildren(this);
   }

   @Override
   public void meet(EmptySet arg0) throws SparqlParserException
   {
      // NO-OP
   }

   @Override
   public void meet(Exists arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("Built-in call: EXISTS"); //$NON-NLS-1$
   }

   @Override
   public void meet(Extension arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("Extension"); //$NON-NLS-1$
   }

   @Override
   public void meet(ExtensionElem arg0) throws SparqlParserException
   {
      // NO-OP
   }

   @Override
   public void meet(Filter arg0) throws SparqlParserException
   {
      TupleExpr expr = arg0.getArg();
      if (expr != null) {
         expr.visit(this);
      }
      arg0.getCondition().visit(this);
      mQueryExt.setFilter(getFunction());
   }

   @Override
   public void meet(FunctionCall arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("External function call"); //$NON-NLS-1$
   }

   @Override
   public void meet(Group arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("GROUP BY"); //$NON-NLS-1$
   }

   @Override
   public void meet(GroupConcat arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("Aggregate Algebra: GroupConcat"); //$NON-NLS-1$
   }

   @Override
   public void meet(GroupElem arg0) throws SparqlParserException
   {
      // NO-OP
   }

   @Override
   public void meet(If arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("Built-in call: IF"); //$NON-NLS-1$
   }

   @Override
   public void meet(In arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("Sub-query comparator: IN"); //$NON-NLS-1$
   }

   @Override
   public void meet(InsertData arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("SPARQL update string: INSERT DATA"); //$NON-NLS-1$
   }

   @Override
   public void meet(Intersection arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("INTERSECT"); //$NON-NLS-1$
   }

   @Override
   public void meet(IRIFunction arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("Built-in call: IRI"); //$NON-NLS-1$
   }

   @Override
   public void meet(IsBNode arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("Built-in call: IsBLANK"); //$NON-NLS-1$
   }

   @Override
   public void meet(IsLiteral arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("Built-in call: IsLITERAL"); //$NON-NLS-1$
   }

   @Override
   public void meet(IsNumeric arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("Built-in call: IsNUMERIC"); //$NON-NLS-1$
   }

   @Override
   public void meet(IsResource arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("IsResource"); //$NON-NLS-1$
   }

   @Override
   public void meet(IsURI arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("Built-in call: IsURI"); //$NON-NLS-1$
   }

   @Override
   public void meet(org.openrdf.query.algebra.Join arg0) throws SparqlParserException
   {
      Join joinExpression = new Join();
      joinExpression.setInnerJoin(true);
      arg0.getLeftArg().visit(this);
      joinExpression.setLeftExpression(getQueryBody());
      arg0.getRightArg().visit(this);
      joinExpression.setRightExpression(getQueryBody());
      mQueryExpression = joinExpression;
   }

   @Override
   public void meet(Label arg0) throws SparqlParserException
   {
      // NO-OP
   }

   @Override
   public void meet(Lang arg0) throws SparqlParserException
   {
      arg0.getArg().visit(this);
      ITerm arg = getTerm();
      mTerm = sExpressionFactory.formLang(arg);
   }

   @Override
   public void meet(LangMatches arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("Built-in call: LANGMATCHES"); //$NON-NLS-1$
   }

   @Override
   public void meet(LeftJoin arg0) throws SparqlParserException
   {
      Join joinExpression = new Join();
      joinExpression.setLeftJoin(true);
      arg0.getLeftArg().visit(this);
      joinExpression.setLeftExpression(getQueryBody());
      arg0.getRightArg().visit(this);
      joinExpression.setRightExpression(getQueryBody());
      if (arg0.hasCondition()) {
         arg0.getCondition().visit(this);
         joinExpression.setFilter(TermUtils.asFunction(getTerm()));
      }
      mQueryExpression = joinExpression;
   }

   @Override
   public void meet(Like arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("LIKE"); //$NON-NLS-1$
   }

   @Override
   public void meet(Load arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("SPARQL update string: LOAD"); //$NON-NLS-1$
   }

   @Override
   public void meet(LocalName arg0) throws SparqlParserException
   {
      // NO-OP
   }

   @Override
   public void meet(MathExpr arg0) throws SparqlParserException
   {
      arg0.getLeftArg().visit(this);
      ITerm leftTerm = getTerm();
      arg0.getRightArg().visit(this);
      ITerm rightTerm = getTerm();
      
      MathOp op = arg0.getOperator();
      switch (op) {
         case PLUS: mTerm = sExpressionFactory.formAddition(leftTerm, rightTerm); break;
         case MINUS: mTerm = sExpressionFactory.formSubtraction(leftTerm, rightTerm); break;
         case MULTIPLY: mTerm = sExpressionFactory.formMultiplication(leftTerm, rightTerm); break;
         case DIVIDE: mTerm = sExpressionFactory.formDivision(leftTerm, rightTerm); break;
      }
   }

   @Override
   public void meet(Max arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("MAX"); //$NON-NLS-1$
   }

   @Override
   public void meet(Min arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("MIN"); //$NON-NLS-1$
   }

   @Override
   public void meet(Modify arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("SPARQL update string: MODIFY"); //$NON-NLS-1$
   }

   @Override
   public void meet(Move arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("SPARQL update string: MOVE"); //$NON-NLS-1$
   }

   @Override
   public void meet(MultiProjection arg0) throws SparqlParserException
   {
      // NO-OP
   }

   @Override
   public void meet(Namespace arg0) throws SparqlParserException
   {
      // NO-OP
   }

   @Override
   public void meet(Not arg0) throws SparqlParserException
   {
      arg0.getArg().visit(this);
      ITerm term = getTerm();
      if (term instanceof IFunction) {
         IFunction function = TermUtils.asFunction(term);
         String functionName = function.getName();
         if (functionName.equals(ExpressionConstant.IS_NOT_NULL)) {
            mTerm = sExpressionFactory.formIsNull(function.getParameter(0));
         }
         else if (functionName.equals(ExpressionConstant.IS_NULL)) {
            mTerm = sExpressionFactory.formIsNotNull(function.getParameter(0));
         }
      }
      else {
         mTerm = sExpressionFactory.formNot(term);
      }
   }

   @Override
   public void meet(Or arg0) throws SparqlParserException
   {
      arg0.getLeftArg().visit(this);
      ITerm termLeft = getTerm();
      arg0.getRightArg().visit(this);
      ITerm termRight = getTerm();
      mTerm = sExpressionFactory.formOr(termLeft, termRight);
   }

   @Override
   public void meet(Order arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("Query modifier: ORDER BY"); //$NON-NLS-1$
   }

   @Override
   public void meet(OrderElem arg0) throws SparqlParserException
   {
      // NO-OP
   }

   @Override
   public void meet(Projection arg0) throws SparqlParserException
   {
      arg0.visitChildren(this);
      for (IVariable var : getVariableList()) {
         mQueryExt.addDistTerm(var);
      }
   }

   @Override
   public void meet(ProjectionElem arg0) throws SparqlParserException
   {
      mTerm = sExpressionFactory.getVariable(arg0.getSourceName());
   }

   @Override
   public void meet(ProjectionElemList arg0) throws SparqlParserException
   {
      mVarList = new ArrayList<IVariable>();
      for (ProjectionElem el : arg0.getElements()) {
         el.visit(this);
         mVarList.add(getVariable());
      }
   }

   @Override
   public void meet(Reduced arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("Query modifier: REDUCED"); //$NON-NLS-1$
   }

   @Override
   public void meet(Regex arg0) throws SparqlParserException
   {
      arg0.getArg().visit(this);
      ITerm textArg = getTerm();
      arg0.getPatternArg().visit(this);
      ITerm patternArg = getTerm();
      
      ITerm flagArg = TermUtils.makeTypedLiteral("i", DataType.STRING); //$NON-NLS-1$ // default flag
      ValueExpr flagExpr = arg0.getFlagsArg();
      if (flagExpr != null) {
         flagExpr.visit(this);
         flagArg = getTerm();
      }
      mTerm = sExpressionFactory.formRegex(textArg, patternArg, flagArg);
   }

   @Override
   public void meet(SameTerm arg0) throws SparqlParserException
   {
      arg0.getLeftArg().visit(this);
      ITerm termLeft = getTerm();
      arg0.getRightArg().visit(this);
      ITerm termRight = getTerm();
      mTerm = sExpressionFactory.formEq(termLeft, termRight);
   }

   @Override
   public void meet(Sample arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("Aggregate algebra: SAMPLE"); //$NON-NLS-1$
   }

   @Override
   public void meet(Service arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("SERVICE"); //$NON-NLS-1$
   }

   @Override
   public void meet(SingletonSet arg0) throws SparqlParserException
   {
      // NO-OP
   }

   @Override
   public void meet(Slice arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("Query modifier: Slice"); //$NON-NLS-1$
   }

   @Override
   public void meet(StatementPattern arg0) throws SparqlParserException
   {
      arg0.getSubjectVar().visit(this);
      ITerm subject = getTerm();
      
      arg0.getPredicateVar().visit(this);
      ITerm predicate = getTerm();
      
      arg0.getObjectVar().visit(this);
      ITerm object = getTerm();
      
      mQueryExpression = new TripleAtom(subject, predicate, object);
   }

   @Override
   public void meet(Str arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("Built-in call: STR"); //$NON-NLS-1$
   }

   @Override
   public void meet(Sum arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("Aggregate Algebra: SUM"); //$NON-NLS-1$
   }

   @Override
   public void meet(Union arg0) throws SparqlParserException
   {
      throw new UnsupportedSparqlExpressionException("UNION"); //$NON-NLS-1$
   }

   @Override
   public void meet(ValueConstant arg0) throws SparqlParserException
   {
      Value value = arg0.getValue();
      visitValue(value);
   }

   @Override
   public void meet(ListMemberOperator arg0) throws SparqlParserException
   {
      // NO-OP
   }

   @Override
   public void meet(Var arg0) throws SparqlParserException
   {
      Value value = arg0.getValue();
      if (value != null) {
         visitValue(value);
      }
      else {
         mTerm = sExpressionFactory.getVariable(arg0.getName());
      }
   }

   @Override
   public void meet(ZeroLengthPath arg0) throws SparqlParserException
   {
      // NO-OP
   }

   @Override
   public void meetOther(QueryModelNode arg0) throws SparqlParserException
   {
      arg0.visit(this);
   }

   protected void visitValue(Value value) throws SparqlParserException
   {
      if (value instanceof org.openrdf.model.Literal) {
         org.openrdf.model.Literal literal = (org.openrdf.model.Literal) value;
         String lexicalValue = literal.getLabel();
         org.openrdf.model.URI dt = literal.getDatatype();
         if (dt == null) {
            /*
             * The returned datatype is null for query matching literal with string
             * type, e.g.,
             *    SELECT ?v WHERE { ?v ?p "cat" }
             */
            mTerm = sExpressionFactory.getLiteral(lexicalValue);
         }
         else {
            mTerm = sExpressionFactory.getLiteral(lexicalValue, dt.stringValue());
         }
      }
      else if (value instanceof org.openrdf.model.URI) {
         org.openrdf.model.URI uri = (org.openrdf.model.URI) value;
         URI uriRef = URI.create(uri.toString());
         mTerm = sExpressionFactory.getUriReference(uriRef);
      }
      else {
         throw new SparqlParserException("Unknown value constant class: " + value.getClass()); //$NON-NLS-1$
      }
   }
}
