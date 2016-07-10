package com.obidea.semantika.database.sql.parser;

import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnalyticExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.CastExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.ExtractExpression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.HexValue;
import net.sf.jsqlparser.expression.IntervalExpression;
import net.sf.jsqlparser.expression.JdbcNamedParameter;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.JsonExpression;
import net.sf.jsqlparser.expression.KeepExpression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.MySQLGroupConcat;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.NumericBind;
import net.sf.jsqlparser.expression.OracleHierarchicalExpression;
import net.sf.jsqlparser.expression.OracleHint;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.RowConstructor;
import net.sf.jsqlparser.expression.SignedExpression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.UserVariable;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.WithinGroupExpression;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Modulo;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.expression.operators.relational.RegExpMatchOperator;
import net.sf.jsqlparser.expression.operators.relational.RegExpMySQLOperator;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

public class ExpressionAdapter implements ExpressionVisitor
{
   /**
    * Override to change default behavior.
    *
    * @param axiom
    *           visited axiom
    */
   protected void handleDefault(Expression expr) { }

   @Override
   public void visit(NullValue nullValue)
   {
      handleDefault(nullValue);
   }

   @Override
   public void visit(Function function)
   {
      handleDefault(function);
   }

   @Override
   public void visit(SignedExpression signedExpression)
   {
      handleDefault(signedExpression);
   }

   @Override
   public void visit(JdbcParameter jdbcParameter)
   {
      handleDefault(jdbcParameter);
   }

   @Override
   public void visit(JdbcNamedParameter jdbcNamedParameter)
   {
      handleDefault(jdbcNamedParameter);
   }

   @Override
   public void visit(DoubleValue doubleValue)
   {
      handleDefault(doubleValue);
   }

   @Override
   public void visit(LongValue longValue)
   {
      handleDefault(longValue);
   }

   @Override
   public void visit(HexValue hexValue)
   {
      handleDefault(hexValue);
   }

   @Override
   public void visit(DateValue dateValue)
   {
      handleDefault(dateValue);
   }

   @Override
   public void visit(TimeValue timeValue)
   {
      handleDefault(timeValue);
   }

   @Override
   public void visit(TimestampValue timestampValue)
   {
      handleDefault(timestampValue);
   }

   @Override
   public void visit(Parenthesis parenthesis)
   {
      handleDefault(parenthesis);
   }

   @Override
   public void visit(StringValue stringValue)
   {
      handleDefault(stringValue);
   }

   @Override
   public void visit(Addition addition)
   {
      handleDefault(addition);
   }

   @Override
   public void visit(Division division)
   {
      handleDefault(division);
   }

   @Override
   public void visit(Multiplication multiplication)
   {
      handleDefault(multiplication);
   }

   @Override
   public void visit(Subtraction subtraction)
   {
      handleDefault(subtraction);
   }

   @Override
   public void visit(AndExpression andExpression)
   {
      handleDefault(andExpression);
   }

   @Override
   public void visit(OrExpression orExpression)
   {
      handleDefault(orExpression);
   }

   @Override
   public void visit(Between between)
   {
      handleDefault(between);
   }

   @Override
   public void visit(EqualsTo equalsTo)
   {
      handleDefault(equalsTo);
   }

   @Override
   public void visit(GreaterThan greaterThan)
   {
      handleDefault(greaterThan);
   }

   @Override
   public void visit(GreaterThanEquals greaterThanEquals)
   {
      handleDefault(greaterThanEquals);
   }

   @Override
   public void visit(InExpression inExpression)
   {
      handleDefault(inExpression);
   }

   @Override
   public void visit(IsNullExpression isNullExpression)
   {
      handleDefault(isNullExpression);
   }

   @Override
   public void visit(LikeExpression likeExpression)
   {
      handleDefault(likeExpression);
   }

   @Override
   public void visit(MinorThan minorThan)
   {
      handleDefault(minorThan);
   }

   @Override
   public void visit(MinorThanEquals minorThanEquals)
   {
      handleDefault(minorThanEquals);
   }

   @Override
   public void visit(NotEqualsTo notEqualsTo)
   {
      handleDefault(notEqualsTo);
   }

   @Override
   public void visit(Column tableColumn)
   {
      handleDefault(tableColumn);
   }

   @Override
   public void visit(SubSelect subSelect)
   {
      handleDefault(subSelect);
   }

   @Override
   public void visit(CaseExpression caseExpression)
   {
      handleDefault(caseExpression);
   }

   @Override
   public void visit(WhenClause whenClause)
   {
      handleDefault(whenClause);
   }

   @Override
   public void visit(ExistsExpression existsExpression)
   {
      handleDefault(existsExpression);
   }

   @Override
   public void visit(AllComparisonExpression allComparisonExpression)
   {
      handleDefault(allComparisonExpression);
   }

   @Override
   public void visit(AnyComparisonExpression anyComparisonExpression)
   {
      handleDefault(anyComparisonExpression);
   }

   @Override
   public void visit(Concat concat)
   {
      handleDefault(concat);
   }

   @Override
   public void visit(Matches matches)
   {
      handleDefault(matches);
   }

   @Override
   public void visit(BitwiseAnd bitwiseAnd)
   {
      handleDefault(bitwiseAnd);
   }

   @Override
   public void visit(BitwiseOr bitwiseOr)
   {
      handleDefault(bitwiseOr);
   }

   @Override
   public void visit(BitwiseXor bitwiseXor)
   {
      handleDefault(bitwiseXor);
   }

   @Override
   public void visit(CastExpression cast)
   {
      handleDefault(cast);
   }

   @Override
   public void visit(Modulo modulo)
   {
      handleDefault(modulo);
   }

   @Override
   public void visit(AnalyticExpression aexpr)
   {
      handleDefault(aexpr);
   }

   @Override
   public void visit(WithinGroupExpression wgexpr)
   {
      handleDefault(wgexpr);
   }

   @Override
   public void visit(ExtractExpression eexpr)
   {
      handleDefault(eexpr);
   }

   @Override
   public void visit(IntervalExpression iexpr)
   {
      handleDefault(iexpr);
   }

   @Override
   public void visit(OracleHierarchicalExpression oexpr)
   {
      handleDefault(oexpr);
   }

   @Override
   public void visit(RegExpMatchOperator rexpr)
   {
      handleDefault(rexpr);
   }

   @Override
   public void visit(JsonExpression jsonExpr)
   {
      handleDefault(jsonExpr);
   }

   @Override
   public void visit(RegExpMySQLOperator regExpMySQLOperator)
   {
      handleDefault(regExpMySQLOperator);
   }

   @Override
   public void visit(UserVariable var)
   {
      handleDefault(var);
   }

   @Override
   public void visit(NumericBind bind)
   {
      handleDefault(bind);
   }

   @Override
   public void visit(KeepExpression aexpr)
   {
      handleDefault(aexpr);
   }

   @Override
   public void visit(MySQLGroupConcat groupConcat)
   {
      handleDefault(groupConcat);
   }

   @Override
   public void visit(RowConstructor rowConstructor)
   {
      handleDefault(rowConstructor);
   }

   @Override
   public void visit(OracleHint hint)
   {
      handleDefault(hint);
   }
}
