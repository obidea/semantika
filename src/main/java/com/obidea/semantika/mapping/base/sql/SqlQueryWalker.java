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
package com.obidea.semantika.mapping.base.sql;

import com.obidea.semantika.database.sql.base.ISqlColumn;
import com.obidea.semantika.database.sql.base.ISqlExpression;
import com.obidea.semantika.database.sql.base.ISqlFunction;
import com.obidea.semantika.database.sql.base.ISqlJoin;
import com.obidea.semantika.database.sql.base.ISqlQuery;
import com.obidea.semantika.database.sql.base.ISqlSubQuery;
import com.obidea.semantika.database.sql.base.ISqlTable;
import com.obidea.semantika.database.sql.base.SqlExpressionVisitorAdapter;
import com.obidea.semantika.database.sql.base.SqlJoinCondition;
import com.obidea.semantika.database.sql.base.SqlSelectItem;

/* package */class SqlQueryWalker extends SqlExpressionVisitorAdapter
{
   private SqlQuery mSelectQuery;

   private SqlInternal mInternalQuery;

   public SqlQueryWalker(SqlQuery selectQuery)
   {
      mSelectQuery = selectQuery;
   }

   public void doUpdate(SqlInternal internalQuery)
   {
      mInternalQuery = internalQuery;
      visitQuery(mSelectQuery);
   }

   private void visitQuery(ISqlQuery sqlQuery)
   {
      if (sqlQuery instanceof SqlQuery) {
         sqlQuery.getFromExpression().accept(this);
         for (SqlSelectItem selectItem : sqlQuery.getSelectItems()) {
            visitSelectItem(selectItem);
            selectItem.getExpression().accept(this);
         }
         for (ISqlExpression whereExpression : sqlQuery.getWhereExpression()) {
            whereExpression.accept(this);
         }
      }
   }

   private void visitSelectItem(SqlSelectItem selectItem)
   {
      String selectItemLabel = selectItem.getLabelName();
      mInternalQuery.addSelectItem(selectItemLabel, selectItem.getExpression());
   }

   @Override
   public void visit(ISqlColumn columnExpression)
   {
      if (columnExpression instanceof SqlColumn) {
         mInternalQuery.addColumn((SqlColumn) columnExpression);
      }
   }

   @Override
   public void visit(ISqlJoin joinExpression)
   {
      if (joinExpression instanceof SqlJoin) {
         joinExpression.getLeftExpression().accept(this);
         joinExpression.getRightExpression().accept(this);
         for (SqlJoinCondition condition : joinExpression.getJoinConditions()) {
            condition.getLeftColumn().accept(this);
            condition.getRightColumn().accept(this);
         }
         for (ISqlExpression filter : joinExpression.getFilters()) {
            filter.accept(this);
         }
      }
   }

   @Override
   public void visit(ISqlSubQuery subQueryExpression)
   {
      if (subQueryExpression instanceof SqlSubQuery) {
         visitQuery(subQueryExpression.getQuery());
      }
   }

   @Override
   public void visit(ISqlTable tableExpression)
   {
      if (tableExpression instanceof SqlTable) {
         String tableName = tableExpression.getTableName();
         mInternalQuery.addTable(tableName, (SqlTable) tableExpression);
      }
   }

   @Override
   public void visit(ISqlFunction filterExpression)
   {
      if (filterExpression instanceof SqlFunction) {
         for (ISqlExpression parameter : filterExpression.getParameterExpressions()) {
            parameter.accept(this);
         }
      }
   }
}
