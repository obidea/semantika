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
package com.obidea.semantika.mapping.sql.parser;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnalyticExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.CastExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.ExtractExpression;
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
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.expression.operators.relational.RegExpMatchOperator;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.LateralSubSelect;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.ValuesList;
import net.sf.jsqlparser.statement.select.WithItem;

import com.obidea.semantika.database.IDatabaseMetadata;
import com.obidea.semantika.database.base.ITable;
import com.obidea.semantika.database.sql.base.ISqlExpression;
import com.obidea.semantika.database.sql.base.SqlJoinCondition;
import com.obidea.semantika.database.sql.base.SqlSelectItem;
import com.obidea.semantika.mapping.sql.SqlJoin;
import com.obidea.semantika.mapping.sql.SqlMappingFactory;
import com.obidea.semantika.mapping.sql.SqlQuery;
import com.obidea.semantika.mapping.sql.SqlSelectQuery;
import com.obidea.semantika.mapping.sql.SqlTable;
import com.obidea.semantika.util.Serializer;

public class SelectStatementHandler implements SelectVisitor, FromItemVisitor, ItemsListVisitor, ExpressionVisitor
{
   private IDatabaseMetadata mMetadata;

   private boolean mHasDistinct = false;

   private SelectItemList mSelectItems = new SelectItemList();
   private FromTablesList mTableList = new FromTablesList();
   private ISqlExpression mExpression = null;
   private ISqlExpression mFromExpression = null;
   private Stack<ISqlExpression> mFilterExpressions = new Stack<ISqlExpression>();

   private int mOpenParenthesisCount = 0;

   private static SqlMappingFactory sSqlFactory = SqlMappingFactory.getInstance();

   public SelectStatementHandler(IDatabaseMetadata metadata)
   {
      mMetadata = metadata;
   }

   public SqlQuery parse(Statement statement)
   {
      Select ss = (Select) statement;
      ss.getSelectBody().accept(this);
      
      SqlQuery query = new SqlSelectQuery(hasDistinct());
      for (SqlSelectItem selectItem : getSelectItems()) {
         query.addSelectItem(selectItem);
      }
      query.setFromExpression(getFromExpression());
      for (ISqlExpression filter : getFilterExpressions()) {
         query.addWhereExpression(filter);
      }
      return query;
   }

   @Override
   public void visit(PlainSelect plainSelect)
   {
      /*
       * Check if the query uses DISTINCT flag
       */
      checkContainDistinct(plainSelect);
      
      FromItem fromItem = plainSelect.getFromItem();
      visitFromItemExpression(fromItem);
      
      /*
       * Collect the tables in the JOIN statement
       */
      List<Join> joins = plainSelect.getJoins();
      if (joins != null) {
         for (Join join : joins) {
            visitJoinExpression(join);
         }
      }
      
      /*
       * Collect the filter expressions in WHERE statement
       */
      Expression expr = plainSelect.getWhere();
      if (expr != null) {
         visitWhereExpression(expr);
      }
      
      /*
       * Collect the select item expressions in SELECT statement.
       */
      List<SelectItem> selectItemExpressions = plainSelect.getSelectItems();
      SelectItemHandler selectItemHandler = new SelectItemHandler(this);
      selectItemHandler.parse(selectItemExpressions);
   }

   private boolean hasDistinct()
   {
      return mHasDistinct;
   }

   /* package */SelectItemList getSelectItems()
   {
      return mSelectItems;
   }

   /* package */FromTablesList getFromTablesList()
   {
      return mTableList;
   }

   private ISqlExpression getFromExpression()
   {
      return mFromExpression;
   }

   private Stack<ISqlExpression> getFilterExpressions()
   {
      return mFilterExpressions;
   }

   private void checkContainDistinct(PlainSelect plainSelect)
   {
      mHasDistinct = (plainSelect.getDistinct() == null) ? false : true;
   }

   private void visitFromItemExpression(FromItem fromItem)
   {
      fromItem.accept(this);
   }

   @Override
   public void visit(ExpressionList exprList)
   {
      throw new UnsupportedSqlExpressionException("IN"); //$NON-NLS-1$
   }

   @Override
   public void visit(net.sf.jsqlparser.schema.Table table)
   {
      String tn = table.getFullyQualifiedName();
      ITable dboTable = findTableFromMetadata(tn);
      if (dboTable != null) {
         mTableList.add(dboTable);
      }
      mExpression = new SqlTable(dboTable);
      
      /*
       * To handle when the FROM expression consists only a single table
       */
      if (mFromExpression == null) {
         mFromExpression = new SqlTable(dboTable);
      }
   }

   @Override
   public void visit(SubSelect subSelect)
   {
      throw new UnsupportedSqlExpressionException("NESTED SELECT"); //$NON-NLS-1$
   }

   @Override
   public void visit(SubJoin subJoin)
   {
      throw new UnsupportedSqlExpressionException("NESTED JOIN"); //$NON-NLS-1$
   }

   protected void visitJoinExpression(Join join)
   {
      if (join.isSimple()) {
         throw new UnsupportedSqlExpressionException("SIMPLE JOIN"); //$NON-NLS-1$
      }
      else if (join.isInner()) {
         ISqlExpression leftExpression = mFromExpression;
         join.getRightItem().accept(this);
         ISqlExpression rightExpression = getExpression();
         JoinConditionHandler handler = new JoinConditionHandler(this);
         join.getOnExpression().accept(handler);
         createJoinExpression(leftExpression, rightExpression, handler.getJoinConditions());
      }
      else if (join.isOuter()) {
         throw new UnsupportedSqlExpressionException("OUTER JOIN"); //$NON-NLS-1$
      }
      else if (join.isLeft()) {
         throw new UnsupportedSqlExpressionException("LEFT JOIN"); //$NON-NLS-1$
      }
      else if (join.isRight()) {
         throw new UnsupportedSqlExpressionException("RIGHT JOIN"); //$NON-NLS-1$
      }
      else if (join.isFull()) {
         throw new UnsupportedSqlExpressionException("FULL JOIN"); //$NON-NLS-1$
      }
      else if (join.isNatural()) {
         throw new UnsupportedSqlExpressionException("NATURAL JOIN"); //$NON-NLS-1$
      }
      else {
         ISqlExpression leftExpression = mFromExpression;
         join.getRightItem().accept(this);
         ISqlExpression rightExpression = getExpression();
         JoinConditionHandler handler = new JoinConditionHandler(this);
         join.getOnExpression().accept(handler);
         createJoinExpression(leftExpression, rightExpression, handler.getJoinConditions());
      }
   }

   private void createJoinExpression(ISqlExpression leftExpression, ISqlExpression rightExpression, Set<SqlJoinCondition> joinConditions)
   {
      SqlJoin join = new SqlJoin();
      join.setInnerJoin(true);
      join.setLeftExpression(leftExpression);
      join.setRightExpression(rightExpression);
      join.addJoinConditions(joinConditions);
      mFromExpression = join;
   }

   private void visitWhereExpression(Expression expr)
   {
      expr.accept(this);
   }

   @Override
   public void visit(NullValue nullValue)
   {
      throw new UnsupportedSqlExpressionException("NULL VALUE"); //$NON-NLS-1$
   }

   @Override
   public void visit(net.sf.jsqlparser.expression.Function function)
   {
      throw new UnsupportedSqlExpressionException("CALL FUNCTION"); //$NON-NLS-1$
   }

   @Override
   public void visit(Parenthesis parenthesis)
   {
      increaseOpenParenthesisCount();
      parenthesis.getExpression().accept(this);
   }

   @Override
   public void visit(JdbcParameter jdbcParameter)
   {
      throw new UnsupportedSqlExpressionException("PREPARED STATEMENT"); //$NON-NLS-1$
   }

   @Override
   public void visit(DoubleValue doubleValue)
   {
      double value = doubleValue.getValue();
      mExpression = sSqlFactory.createNumericValueExpression(value);
   }

   @Override
   public void visit(LongValue longValue)
   {
      long value = longValue.getValue();
      mExpression = sSqlFactory.createNumericValueExpression(value);
   }

   @Override
   public void visit(DateValue dateValue)
   {
      Date value = dateValue.getValue();
      mExpression = sSqlFactory.createDateTimeValueExpression(value);
   }

   @Override
   public void visit(TimeValue timeValue)
   {
      Time value = timeValue.getValue();
      mExpression = sSqlFactory.createDateTimeValueExpression(value);
   }

   @Override
   public void visit(TimestampValue timestampValue)
   {
      Timestamp value = timestampValue.getValue();
      mExpression = sSqlFactory.createDateTimeValueExpression(value);
   }

   @Override
   public void visit(StringValue stringValue)
   {
      String value = stringValue.getValue();
      mExpression = sSqlFactory.createStringValueExpression(value);
   }

   @Override
   public void visit(Addition addition)
   {
      throw new UnsupportedSqlExpressionException("ADDTION"); //$NON-NLS-1$
   }

   @Override
   public void visit(Division division)
   {
      throw new UnsupportedSqlExpressionException("DIVISION"); //$NON-NLS-1$
   }

   @Override
   public void visit(Multiplication multiplication)
   {
      throw new UnsupportedSqlExpressionException("MULTIPLICATION"); //$NON-NLS-1$
   }

   @Override
   public void visit(Subtraction subtraction)
   {
      throw new UnsupportedSqlExpressionException("SUBSTRACT"); //$NON-NLS-1$
   }

   @Override
   public void visit(AndExpression expression)
   {
      expression.getLeftExpression().accept(this);
      expression.getRightExpression().accept(this);

      if (mOpenParenthesisCount != 0) {
         ISqlExpression rightExpression = mFilterExpressions.pop();
         ISqlExpression leftExpression = mFilterExpressions.pop();
         ISqlExpression andExpression = sSqlFactory.createAndExpression(leftExpression, rightExpression);
         mFilterExpressions.push(andExpression);
      }

      if (mOpenParenthesisCount != 0) {
         decreaseOpenParenthesisCount();
      }
   }

   @Override
   public void visit(OrExpression expression)
   {
      expression.getLeftExpression().accept(this);
      expression.getRightExpression().accept(this);

      ISqlExpression rightExpression = mFilterExpressions.pop();
      ISqlExpression leftExpression = mFilterExpressions.pop();
      ISqlExpression orExpression = sSqlFactory.createOrExpression(leftExpression, rightExpression);
      mFilterExpressions.push(orExpression);

      if (mOpenParenthesisCount != 0) {
         decreaseOpenParenthesisCount();
      }
   }

   @Override
   public void visit(Between between)
   {
      throw new UnsupportedSqlExpressionException("BETWEEN"); //$NON-NLS-1$
   }

   @Override
   public void visit(EqualsTo equalsTo)
   {
      mFilterExpressions.push(visitBinaryExpression(equalsTo));
   }

   @Override
   public void visit(GreaterThan greaterThan)
   {
      mFilterExpressions.push(visitBinaryExpression(greaterThan));
   }

   @Override
   public void visit(GreaterThanEquals greaterThanEquals)
   {
      mFilterExpressions.push(visitBinaryExpression(greaterThanEquals));
   }

   @Override
   public void visit(MinorThan minorThan)
   {
      mFilterExpressions.push(visitBinaryExpression(minorThan));
   }

   @Override
   public void visit(MinorThanEquals minorThanEquals)
   {
      mFilterExpressions.push(visitBinaryExpression(minorThanEquals));
   }

   @Override
   public void visit(NotEqualsTo notEqualsTo)
   {
      mFilterExpressions.push(visitBinaryExpression(notEqualsTo));
   }

   @Override
   public void visit(IsNullExpression isNullExpression)
   {
      isNullExpression.getLeftExpression().accept(this);
      ISqlExpression parameter = getExpression();
      
      boolean isNotNull = isNullExpression.isNot();
      if (isNotNull) {
         mFilterExpressions.push(sSqlFactory.createIsNotNullExpression(parameter));
      }
      else {
         mFilterExpressions.push(sSqlFactory.createIsNullExpression(parameter));
      }
   }

   @Override
   public void visit(InExpression inExpression)
   {
      throw new UnsupportedSqlExpressionException("IN"); //$NON-NLS-1$
   }

   @Override
   public void visit(LikeExpression likeExpression)
   {
      throw new UnsupportedSqlExpressionException("LIKE"); //$NON-NLS-1$
   }

   @Override
   public void visit(net.sf.jsqlparser.schema.Column tableColumn)
   {
      try {
         String tableName = tableColumn.getTable().getFullyQualifiedName();
         String columnName = tableColumn.getColumnName();
         mExpression = mTableList.getColumn(tableName, columnName);
      }
      catch (SqlMappingParserException e) {
         throw new SqlException(e);
      }
   }

   /*
    * Get the Table object given its full name.
    */
   private ITable findTableFromMetadata(String tableName)
   {
      return mMetadata.getTable(tableName);
   }

   @Override
   public void visit(CaseExpression caseExpression)
   {
      throw new UnsupportedSqlExpressionException("CASE"); //$NON-NLS-1$
   }

   @Override
   public void visit(WhenClause whenClause)
   {
      throw new UnsupportedSqlExpressionException("WHEN"); //$NON-NLS-1$
   }

   @Override
   public void visit(ExistsExpression existsExpression)
   {
      throw new UnsupportedSqlExpressionException("EXISTS"); //$NON-NLS-1$
   }

   @Override
   public void visit(AllComparisonExpression allComparisonExpression)
   {
      throw new UnsupportedSqlExpressionException("NESTED SELECT"); //$NON-NLS-1$
   }

   @Override
   public void visit(AnyComparisonExpression anyComparisonExpression)
   {
      throw new UnsupportedSqlExpressionException("NESTED SELECT"); //$NON-NLS-1$
   }

   @Override
   public void visit(Concat concat)
   {
      throw new UnsupportedSqlExpressionException("CONCAT"); //$NON-NLS-1$
   }

   @Override
   public void visit(Matches matches)
   {
      throw new UnsupportedSqlExpressionException("MATCHES"); //$NON-NLS-1$
   }

   @Override
   public void visit(BitwiseAnd bitwiseAnd)
   {
      throw new UnsupportedSqlExpressionException("BITWISE AND"); //$NON-NLS-1$
   }

   @Override
   public void visit(BitwiseOr bitwiseOr)
   {
      throw new UnsupportedSqlExpressionException("BITWISE OR"); //$NON-NLS-1$
   }

   @Override
   public void visit(BitwiseXor bitwiseXor)
   {
      throw new UnsupportedSqlExpressionException("BITWISE XOR"); //$NON-NLS-1$
   }

   @Override
   public void visit(SignedExpression signed)
   {
      throw new UnsupportedSqlExpressionException("SIGNED"); //$NON-NLS-1$
   }

   @Override
   public void visit(JdbcNamedParameter parameter)
   {
      throw new UnsupportedSqlExpressionException("JDBC NAMED PARAMETER"); //$NON-NLS-1$
   }

   @Override
   public void visit(CastExpression cast)
   {
      throw new UnsupportedSqlExpressionException("CAST"); //$NON-NLS-1$
   }

   @Override
   public void visit(Modulo module)
   {
      throw new UnsupportedSqlExpressionException("MODULO"); //$NON-NLS-1$
   }

   @Override
   public void visit(AnalyticExpression analytic)
   {
      throw new UnsupportedSqlExpressionException("ANALYTIC"); //$NON-NLS-1$
   }

   @Override
   public void visit(ExtractExpression extract)
   {
      throw new UnsupportedSqlExpressionException("EXTRACT"); //$NON-NLS-1$
   }

   @Override
   public void visit(IntervalExpression interval)
   {
      throw new UnsupportedSqlExpressionException("INTERVAL"); //$NON-NLS-1$
   }

   @Override
   public void visit(OracleHierarchicalExpression oracleHierarchical)
   {
      throw new UnsupportedSqlExpressionException("ORACLE HIERARCHICAL"); //$NON-NLS-1$
   }

   @Override
   public void visit(RegExpMatchOperator regExpMatch)
   {
      throw new UnsupportedSqlExpressionException("REGEXP MATCH"); //$NON-NLS-1$
   }

   @Override
   public void visit(MultiExpressionList multiExpressionList)
   {
      throw new UnsupportedSqlExpressionException("MULTI EXPRESSION LIST"); //$NON-NLS-1$
   }

   @Override
   public void visit(LateralSubSelect lateralSubSelect)
   {
      throw new UnsupportedSqlExpressionException("LATERAL SUB-SELECT"); //$NON-NLS-1$
   }

   @Override
   public void visit(ValuesList valuesList)
   {
      throw new UnsupportedSqlExpressionException("VALUES LIST"); //$NON-NLS-1$
   }

   @Override
   public void visit(SetOperationList setOperationList)
   {
      throw new UnsupportedSqlExpressionException("SET OPERATION LIST"); //$NON-NLS-1$
   }

   @Override
   public void visit(WithItem withItem)
   {
      throw new UnsupportedSqlExpressionException("WITH ITEM"); //$NON-NLS-1$
   }

   protected ISqlExpression visitBinaryExpression(BinaryExpression binaryExpression)
   {
      binaryExpression.getLeftExpression().accept(this);
      ISqlExpression leftParameter = getExpression();

      binaryExpression.getRightExpression().accept(this);
      ISqlExpression rightParameter = getExpression();

      if (binaryExpression instanceof EqualsTo) {
         return sSqlFactory.createEqualsToExpression(leftParameter, rightParameter);
      }
      else if (binaryExpression instanceof NotEqualsTo) {
         return sSqlFactory.createNotEqualsToExpression(leftParameter, rightParameter);
      }
      else if (binaryExpression instanceof GreaterThan) {
         return sSqlFactory.createGreaterThanExpression(leftParameter, rightParameter);
      }
      else if (binaryExpression instanceof GreaterThanEquals) {
         return sSqlFactory.createGreaterThanEqualsExpression(leftParameter, rightParameter);
      }
      else if (binaryExpression instanceof MinorThan) {
         return sSqlFactory.createLessThanExpression(leftParameter, rightParameter);
      }
      else if (binaryExpression instanceof MinorThanEquals) {
         return sSqlFactory.createLessThanEqualsExpression(leftParameter, rightParameter);
      }
      throw new UnsupportedSqlExpressionException(binaryExpression.toString());
   }

   private ISqlExpression getExpression()
   {
      return (ISqlExpression) Serializer.copy(mExpression);
   }

   private void increaseOpenParenthesisCount()
   {
      mOpenParenthesisCount++;
   }

   private void decreaseOpenParenthesisCount()
   {
      mOpenParenthesisCount--;
   }
}
