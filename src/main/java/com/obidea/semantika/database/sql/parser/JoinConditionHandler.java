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
package com.obidea.semantika.database.sql.parser;

import java.util.HashSet;
import java.util.Set;

import com.obidea.semantika.database.sql.base.SqlJoinCondition;
import com.obidea.semantika.exception.IllegalOperationException;
import com.obidea.semantika.mapping.base.sql.SqlColumn;
import com.obidea.semantika.util.Serializer;

import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnalyticExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.CastExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.ExtractExpression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.IntervalExpression;
import net.sf.jsqlparser.expression.JdbcNamedParameter;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.OracleHierarchicalExpression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.SignedExpression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.WhenClause;
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
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

public class JoinConditionHandler implements ExpressionVisitor
{
   private Set<SqlJoinCondition> mJoinCondition = new HashSet<SqlJoinCondition>();
   private SqlColumn mJoinColumn;
   private FromTablesList mFromTables;

   public JoinConditionHandler(SelectStatementHandler handler)
   {
      mFromTables = handler.getFromTablesList();
   }

   public Set<SqlJoinCondition> getJoinConditions()
   {
      return mJoinCondition;
   }

   @Override
   public void visit(Column tableColumn)
   {
      String tableName = tableColumn.getTable().getFullyQualifiedName();
      String columnName = tableColumn.getColumnName();
      mJoinColumn = mFromTables.getColumn(tableName, columnName);
   }

   @Override
   public void visit(EqualsTo equalsTo)
   {
      equalsTo.getLeftExpression().accept(this);
      SqlColumn leftColumn = copy(mJoinColumn);

      equalsTo.getRightExpression().accept(this);
      SqlColumn rightColumn = copy(mJoinColumn);

      mJoinCondition.add(new SqlJoinCondition(leftColumn, rightColumn));
   }

   @Override
   public void visit(AndExpression andExpression)
   {
      andExpression.getLeftExpression().accept(this);
      andExpression.getRightExpression().accept(this);
   }

   private SqlColumn copy(SqlColumn column)
   {
      return (SqlColumn) Serializer.copy(column);
   }

   @Override
   public void visit(NullValue nullValue)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + nullValue); //$NON-NLS-1$
   }

   @Override
   public void visit(Function function)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + function); //$NON-NLS-1$
   }

   @Override
   public void visit(JdbcParameter jdbcParameter)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + jdbcParameter); //$NON-NLS-1$
   }

   @Override
   public void visit(DoubleValue doubleValue)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + doubleValue); //$NON-NLS-1$
   }

   @Override
   public void visit(LongValue longValue)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + longValue); //$NON-NLS-1$
   }

   @Override
   public void visit(DateValue dateValue)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + dateValue); //$NON-NLS-1$
   }

   @Override
   public void visit(TimeValue timeValue)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + timeValue); //$NON-NLS-1$
   }

   @Override
   public void visit(TimestampValue timestampValue)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + timestampValue); //$NON-NLS-1$
   }

   @Override
   public void visit(Parenthesis parenthesis)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + parenthesis); //$NON-NLS-1$
   }

   @Override
   public void visit(StringValue stringValue)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + stringValue); //$NON-NLS-1$
   }

   @Override
   public void visit(Addition addition)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + addition); //$NON-NLS-1$
   }

   @Override
   public void visit(Division division)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + division); //$NON-NLS-1$
   }

   @Override
   public void visit(Multiplication multiplication)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + multiplication); //$NON-NLS-1$
   }

   @Override
   public void visit(Subtraction subtraction)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + subtraction); //$NON-NLS-1$
   }

   @Override
   public void visit(OrExpression orExpression)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + orExpression); //$NON-NLS-1$
   }

   @Override
   public void visit(Between between)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + between); //$NON-NLS-1$
   }

   @Override
   public void visit(GreaterThan greaterThan)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + greaterThan); //$NON-NLS-1$
   }

   @Override
   public void visit(GreaterThanEquals greaterThanEquals)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + greaterThanEquals); //$NON-NLS-1$
   }

   @Override
   public void visit(InExpression inExpression)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + inExpression); //$NON-NLS-1$
   }

   @Override
   public void visit(IsNullExpression isNullExpression)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + isNullExpression); //$NON-NLS-1$
   }

   @Override
   public void visit(LikeExpression likeExpression)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + likeExpression); //$NON-NLS-1$
   }

   @Override
   public void visit(MinorThan minorThan)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + minorThan); //$NON-NLS-1$
   }

   @Override
   public void visit(MinorThanEquals minorThanEquals)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + minorThanEquals); //$NON-NLS-1$
   }

   @Override
   public void visit(NotEqualsTo notEqualsTo)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + notEqualsTo); //$NON-NLS-1$
   }

   @Override
   public void visit(SubSelect subSelect)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + subSelect); //$NON-NLS-1$
   }

   @Override
   public void visit(CaseExpression caseExpression)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + caseExpression); //$NON-NLS-1$
   }

   @Override
   public void visit(WhenClause whenClause)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + whenClause); //$NON-NLS-1$
   }

   @Override
   public void visit(ExistsExpression existsExpression)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + existsExpression); //$NON-NLS-1$
   }

   @Override
   public void visit(AllComparisonExpression allComparisonExpression)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + allComparisonExpression); //$NON-NLS-1$
   }

   @Override
   public void visit(AnyComparisonExpression anyComparisonExpression)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + anyComparisonExpression); //$NON-NLS-1$
   }

   @Override
   public void visit(Concat concat)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + concat); //$NON-NLS-1$
   }

   @Override
   public void visit(Matches matches)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + matches); //$NON-NLS-1$
   }

   @Override
   public void visit(BitwiseAnd bitwiseAnd)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + bitwiseAnd); //$NON-NLS-1$
   }

   @Override
   public void visit(BitwiseOr bitwiseOr)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + bitwiseOr); //$NON-NLS-1$
   }

   @Override
   public void visit(BitwiseXor bitwiseXor)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + bitwiseXor); //$NON-NLS-1$
   }

   @Override
   public void visit(SignedExpression signed)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + signed); //$NON-NLS-1$
   }

   @Override
   public void visit(JdbcNamedParameter parameter)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + parameter); //$NON-NLS-1$
   }

   @Override
   public void visit(CastExpression cast)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + cast); //$NON-NLS-1$
   }

   @Override
   public void visit(Modulo modulo)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + modulo); //$NON-NLS-1$
   }

   @Override
   public void visit(AnalyticExpression analytic)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + analytic); //$NON-NLS-1$
   }

   @Override
   public void visit(ExtractExpression extract)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + extract); //$NON-NLS-1$
   }

   @Override
   public void visit(IntervalExpression interval)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + interval); //$NON-NLS-1$
   }

   @Override
   public void visit(OracleHierarchicalExpression oracleHierarchical)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + oracleHierarchical); //$NON-NLS-1$
   }

   @Override
   public void visit(RegExpMatchOperator regExpMatch)
   {
      throw new IllegalOperationException("Illegal SQL ON expression: " + regExpMatch); //$NON-NLS-1$
   }
}
