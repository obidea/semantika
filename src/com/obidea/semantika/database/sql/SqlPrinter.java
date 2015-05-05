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
package com.obidea.semantika.database.sql;

import com.obidea.semantika.database.sql.base.ISqlBinaryFunction;
import com.obidea.semantika.database.sql.base.ISqlColumn;
import com.obidea.semantika.database.sql.base.ISqlExpression;
import com.obidea.semantika.database.sql.base.ISqlExpressionVisitor;
import com.obidea.semantika.database.sql.base.ISqlFunction;
import com.obidea.semantika.database.sql.base.ISqlJoin;
import com.obidea.semantika.database.sql.base.ISqlQuery;
import com.obidea.semantika.database.sql.base.ISqlSubQuery;
import com.obidea.semantika.database.sql.base.ISqlTable;
import com.obidea.semantika.database.sql.base.ISqlUnaryFunction;
import com.obidea.semantika.database.sql.base.ISqlValue;
import com.obidea.semantika.database.sql.base.SqlJoinCondition;
import com.obidea.semantika.database.sql.base.SqlSelectItem;
import com.obidea.semantika.mapping.base.sql.SqlUserQuery;

public class SqlPrinter implements ISqlExpressionVisitor
{
   private static final int SHOW_COLUMN_LIMIT = 4;

   private StringBuilder mStringBuilder;

   public String print(ISqlQuery selectQuery)
   {
      return print(selectQuery, false);
   }

   public String print(ISqlQuery selectQuery, boolean excludeProjection)
   {
      initStringBuilder();
      
      if (!excludeProjection) {
         /*
          * Print the query projection
          */
         mStringBuilder.append("SELECT"); //$NON-NLS-1$
         mStringBuilder.append("("); //$NON-NLS-1$
         boolean needComma = false;
         for (SqlSelectItem selectItem : selectQuery.getSelectItems()) {
            if (needComma) {
               mStringBuilder.append(", "); //$NON-NLS-1$
            }
            selectItem.getExpression().accept(this);
            if (selectItem.hasAliasName()) {
               mStringBuilder.append("[");
               mStringBuilder.append(selectItem.getAliasName());
               mStringBuilder.append("]");
            }
            needComma = true;
         }
         mStringBuilder.append(")"); //$NON-NLS-1$
         mStringBuilder.append(" :- "); //$NON-NLS-1$
      }
      
      /*
       * Print the query body
       */
      selectQuery.getFromExpression().accept(this);
      
      /* 
       * Print the query filters, if any
       */
      for (ISqlExpression filter : selectQuery.getWhereExpression()) {
         mStringBuilder.append(", "); //$NON-NLS-1$
         filter.accept(this);
      }
      return mStringBuilder.toString();
   }

   @Override
   public void visit(ISqlTable table)
   {
      mStringBuilder.append(table.getTableName());
      mStringBuilder.append("("); //$NON-NLS-1$
      int counter = 0;
      boolean needComma = false;
      for (ISqlColumn column : table.getColumns()) {
         if (needComma) {
            mStringBuilder.append(", "); //$NON-NLS-1$
         }
         if (counter == SHOW_COLUMN_LIMIT) {
            int remaining = table.getColumns().size() - SHOW_COLUMN_LIMIT;
            mStringBuilder.append("... (").append(remaining).append(" more)"); //$NON-NLS-1$ //$NON-NLS-2$
            break;
         }
         column.accept(this);
         counter++;
         needComma = true;
      }
      mStringBuilder.append(")"); //$NON-NLS-1$
   }

   @Override
   public void visit(ISqlColumn column)
   {
      mStringBuilder.append(column.getColumnName());
   }

   @Override
   public void visit(ISqlFunction function)
   {
      mStringBuilder.append(function.getName());
      mStringBuilder.append("("); //$NON-NLS-1$
      
      if (function instanceof ISqlUnaryFunction) {
         ISqlUnaryFunction unaryFilter = (ISqlUnaryFunction) function;
         unaryFilter.getParameterExpression().accept(this);
      }
      else if (function instanceof ISqlBinaryFunction) {
         ISqlBinaryFunction binaryFilter = (ISqlBinaryFunction) function;
         binaryFilter.getLeftParameterExpression().accept(this);
         mStringBuilder.append(","); //$NON-NLS-1$
         binaryFilter.getRightParameterExpression().accept(this);
      }
      else {
         boolean needComma = false;
         for (ISqlExpression parameter : function.getParameterExpressions()) {
            if (needComma) {
               mStringBuilder.append(","); //$NON-NLS-1$
            }
            parameter.accept(this);
            needComma = true;
         }
      }
      mStringBuilder.append(")"); //$NON-NLS-1$
   }

   @Override
   public void visit(ISqlValue value)
   {
      mStringBuilder.append("'"); //$NON-NLS-1$
      mStringBuilder.append(value);
      mStringBuilder.append("'"); //$NON-NLS-1$
   }

   @Override
   public void visit(ISqlJoin joinExpression)
   {
      mStringBuilder.append(joinExpression.getName());
      mStringBuilder.append("("); //$NON-NLS-1$
      joinExpression.getLeftExpression().accept(this);
      mStringBuilder.append(", "); //$NON-NLS-1$
      joinExpression.getRightExpression().accept(this);
      mStringBuilder.append(", "); //$NON-NLS-1$
      mStringBuilder.append("{"); //$NON-NLS-1$
      boolean needComma = false;
      for (SqlJoinCondition joinCondition : joinExpression.getJoinConditions()) {
         if (needComma) {
            mStringBuilder.append(","); //$NON-NLS-1$
         }
         mStringBuilder.append("EQ");
         mStringBuilder.append("("); //$NON-NLS-1$
         joinCondition.getLeftColumn().accept(this);
         mStringBuilder.append(","); //$NON-NLS-1$
         joinCondition.getRightColumn().accept(this);
         mStringBuilder.append(")"); //$NON-NLS-1$
         needComma = true;
      }
      mStringBuilder.append("}"); //$NON-NLS-1$
      mStringBuilder.append(")"); //$NON-NLS-1$
   }

   @Override
   public void visit(ISqlSubQuery subQueryExpression)
   {
      if (subQueryExpression instanceof SqlUserQuery) {
         SqlUserQuery userQuery = (SqlUserQuery) subQueryExpression;
         mStringBuilder.append("USERQUERY"); //$NON-NLS-1$
         mStringBuilder.append("("); //$NON-NLS-1$
         mStringBuilder.append(userQuery.getSqlString());
         mStringBuilder.append(")"); //$NON-NLS-1$
      }
      else {
         mStringBuilder.append("SUBSELECT");
         mStringBuilder.append("("); //$NON-NLS-1$
         boolean needComma = false;
         for (SqlSelectItem selectItem : subQueryExpression.getQuery().getSelectItems()) {
            if (needComma) {
               mStringBuilder.append(", "); //$NON-NLS-1$
            }
            selectItem.getExpression().accept(this);
            needComma = true;
         }
         mStringBuilder.append(")"); //$NON-NLS-1$
      }
   }

   private void initStringBuilder()
   {
      mStringBuilder = new StringBuilder();
   }
}
