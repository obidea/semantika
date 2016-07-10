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

import com.obidea.semantika.database.sql.base.ISqlExpression;
import com.obidea.semantika.database.sql.base.SqlSelectItem;
import com.obidea.semantika.exception.SemantikaRuntimeException;
import com.obidea.semantika.mapping.base.sql.SqlColumn;
import com.obidea.semantika.mapping.base.sql.SqlMappingFactory;
import com.obidea.semantika.mapping.base.sql.SqlTable;
import com.obidea.semantika.util.Serializer;

import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;

/* package */ class SelectItemHandler extends ExpressionAdapter implements SelectItemVisitor
{
   private ISqlExpression mExpression = null;

   private SelectItemList mSelectItems;
   private FromTablesList mFromTables;

   private int mOpenParenthesisCount = 0;

   private static SqlMappingFactory sSqlFactory = SqlMappingFactory.getInstance();

   public SelectItemHandler(SelectStatementHandler handler)
   {
      mSelectItems = handler.getSelectItems();
      mFromTables = handler.getFromTablesList();
   }

   public void parse(List<SelectItem> selectItems)
   {
      for (SelectItem selectItem : selectItems) {
         selectItem.accept(this);
      }
   }

   @Override
   public void visit(AllColumns allColumns)
   {
      for (SqlTable table : mFromTables) {
         for (SqlColumn column : table.getColumns()) {
            mSelectItems.add(new SqlSelectItem(column));
         }
      }
   }

   @Override
   public void visit(AllTableColumns allTableColumns)
   {
      try {
         String tableName = allTableColumns.getTable().getFullyQualifiedName();
         for (SqlColumn column : mFromTables.getColumns(tableName)) {
            mSelectItems.add(new SqlSelectItem(column));
         }
      }
      catch (SqlParserException e) {
         throw new SemantikaRuntimeException(e);
      }
   }

   @Override
   public void visit(SelectExpressionItem selectExpressionItem)
   {
      selectExpressionItem.getExpression().accept(this);
      ISqlExpression expression = getExpression();
      SqlSelectItem selectItem = new SqlSelectItem(expression);
      Alias alias = selectExpressionItem.getAlias();
      if (alias != null) {
         String aliasName = removeQuotesIfAny(alias.getName());
         selectItem.setAliasName(aliasName);
      }
      mSelectItems.add(selectItem);
   }

   @Override
   public void visit(net.sf.jsqlparser.schema.Column tableColumn)
   {
      try {
         String tableName = removeQuotesIfAny(tableColumn.getTable().getFullyQualifiedName());
         String columnName = removeQuotesIfAny(tableColumn.getColumnName());
         mExpression = mFromTables.getColumn(tableName, columnName);
      }
      catch (SqlParserException e) {
         throw new SemantikaRuntimeException(e);
      }
   }

   private static String removeQuotesIfAny(String value)
   {
      return value.replaceAll("^\"|\"$", "").replaceAll("^`|`$", ""); //$NON-NLS-1$ //$NON-NLS-2$
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
   public void visit(Addition addition)
   {
      mExpression = visitBinaryExpression(addition);
      if (mOpenParenthesisCount != 0) {
         decreaseOpenParenthesisCount();
      }
   }

   @Override
   public void visit(Subtraction subtraction)
   {
      mExpression = visitBinaryExpression(subtraction);
      if (mOpenParenthesisCount != 0) {
         decreaseOpenParenthesisCount();
      }
   }

   @Override
   public void visit(Multiplication multiplication)
   {
      mExpression = visitBinaryExpression(multiplication);
      if (mOpenParenthesisCount != 0) {
         decreaseOpenParenthesisCount();
      }
   }

   @Override
   public void visit(Division division)
   {
      mExpression = visitBinaryExpression(division);
      if (mOpenParenthesisCount != 0) {
         decreaseOpenParenthesisCount();
      }
   }

   @Override
   public void visit(Concat concat)
   {
      mExpression = visitBinaryExpression(concat);
      if (mOpenParenthesisCount != 0) {
         decreaseOpenParenthesisCount();
      }
   }

   protected ISqlExpression visitBinaryExpression(BinaryExpression binaryExpression)
   {
      binaryExpression.getLeftExpression().accept(this);
      ISqlExpression leftParameter = getExpression();
      
      binaryExpression.getRightExpression().accept(this);
      ISqlExpression rightParameter = getExpression();
      
      if (binaryExpression instanceof Addition) {
         return sSqlFactory.createAdditionExpression(leftParameter, rightParameter);
      }
      else if (binaryExpression instanceof Subtraction) {
         return sSqlFactory.createSubstractExpression(leftParameter, rightParameter);
      }
      else if (binaryExpression instanceof Multiplication) {
         return sSqlFactory.createMultiplyExpression(leftParameter, rightParameter);
      }
      else if (binaryExpression instanceof Division) {
         return sSqlFactory.createDivideExpression(leftParameter, rightParameter);
      }
      else if (binaryExpression instanceof Concat) {
         return sSqlFactory.createConcatExpression(leftParameter, rightParameter);
      }
      throw new UnsupportedSqlExpressionException(binaryExpression);
   }

   @Override
   protected void handleDefault(Expression expr)
   {
      throw new UnsupportedSqlExpressionException(expr);
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
