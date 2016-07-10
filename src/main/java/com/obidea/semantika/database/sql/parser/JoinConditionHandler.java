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

import java.util.HashSet;
import java.util.Set;

import com.obidea.semantika.database.sql.base.SqlJoinCondition;
import com.obidea.semantika.mapping.base.sql.SqlColumn;
import com.obidea.semantika.util.Serializer;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;

/* package */ class JoinConditionHandler extends ExpressionAdapter
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

   private SqlColumn copy(SqlColumn column)
   {
      return (SqlColumn) Serializer.copy(column);
   }

   @Override
   protected void handleDefault(Expression expr)
   {
      throw new UnsupportedSqlExpressionException(expr);
   }
}
