/*
 * Copyright (c) 2013-2015 Obidea Technology
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

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.obidea.semantika.database.IDatabaseMetadata;
import com.obidea.semantika.database.base.ITable;
import com.obidea.semantika.database.sql.base.ISqlExpression;
import com.obidea.semantika.database.sql.base.SqlJoinCondition;
import com.obidea.semantika.database.sql.base.SqlSelectItem;
import com.obidea.semantika.mapping.base.sql.SqlJoin;
import com.obidea.semantika.mapping.base.sql.SqlMappingFactory;
import com.obidea.semantika.mapping.base.sql.SqlQuery;
import com.obidea.semantika.mapping.base.sql.SqlSelectQuery;
import com.obidea.semantika.mapping.base.sql.SqlTable;
import com.obidea.semantika.util.Serializer;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
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
import net.sf.jsqlparser.statement.select.TableFunction;
import net.sf.jsqlparser.statement.select.ValuesList;
import net.sf.jsqlparser.statement.select.WithItem;

public class SelectStatementHandler extends ExpressionAdapter implements SelectVisitor, FromItemVisitor
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
            visitJoinExpression(join, new JoinConditionHandler(this));
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

   protected void visitJoinExpression(Join join, JoinConditionHandler handler)
   {
      if (join.isInner()) {
         ISqlExpression leftExpression = mFromExpression;
         join.getRightItem().accept(this);
         ISqlExpression rightExpression = getExpression();
         join.getOnExpression().accept(handler);
         createJoinExpression(leftExpression, rightExpression, handler.getJoinConditions());
      }
      else if (join.isCross()) {
         ISqlExpression leftExpression = mFromExpression;
         join.getRightItem().accept(this);
         ISqlExpression rightExpression = getExpression();
         join.getOnExpression().accept(handler);
         createJoinExpression(leftExpression, rightExpression, handler.getJoinConditions());
      }
      else {
         throw new UnsupportedSqlExpressionException(join);
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
   public void visit(Parenthesis parenthesis)
   {
      increaseOpenParenthesisCount();
      parenthesis.getExpression().accept(this);
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
   public void visit(net.sf.jsqlparser.schema.Column tableColumn)
   {
      String tableName = tableColumn.getTable().getFullyQualifiedName();
      String columnName = tableColumn.getColumnName();
      mExpression = mTableList.getColumn(tableName, columnName);
   }

   /*
    * Get the Table object given its full name.
    */
   private ITable findTableFromMetadata(String tableName)
   {
      return mMetadata.getTable(tableName);
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
      throw new UnsupportedSqlExpressionException(binaryExpression);
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

   @Override
   public void visit(TableFunction tableFunction)
   {
      throw new UnsupportedSqlExpressionException(tableFunction);
   }

   @Override
   public void visit(SubJoin subjoin)
   {
      throw new UnsupportedSqlExpressionException(subjoin);
   }

   @Override
   public void visit(LateralSubSelect lateralSubSelect)
   {
      throw new UnsupportedSqlExpressionException(lateralSubSelect);
   }

   @Override
   public void visit(ValuesList valuesList)
   {
      throw new UnsupportedSqlExpressionException(valuesList);
   }

   @Override
   public void visit(SetOperationList setOpList)
   {
      throw new UnsupportedSqlExpressionException(setOpList);
   }

   @Override
   public void visit(WithItem withItem)
   {
      throw new UnsupportedSqlExpressionException(withItem);
   }

   @Override
   protected void handleDefault(Expression expr)
   {
      throw new UnsupportedSqlExpressionException(expr);
   }
}
