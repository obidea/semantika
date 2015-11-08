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

import java.io.StringReader;
import java.util.List;

import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;

import com.obidea.semantika.database.sql.base.SqlSelectItem;
import com.obidea.semantika.mapping.base.sql.SqlColumn;
import com.obidea.semantika.mapping.base.sql.SqlQuery;
import com.obidea.semantika.mapping.base.sql.SqlSelectQuery;
import com.obidea.semantika.mapping.base.sql.SqlUserQuery;
import com.obidea.semantika.util.StringUtils;

public class UserQueryHandler implements SelectItemVisitor
{
   private SelectItemList mSelectItems;

   public SqlQuery parse(String sqlString)
   {
      final StringReader reader = new StringReader(sqlString);
      CCJSqlParser parser = new CCJSqlParser(reader);
      try {
         Statement stmt = parser.Statement();
         if (stmt instanceof Select) {
            Select ss = (Select) stmt;
            PlainSelect plainSelect = (PlainSelect) ss.getSelectBody();
            return createUserQuery(plainSelect, sqlString);
         }
         else {
            throw new UnsupportedSqlExpressionException("Only SELECT statement is valid"); //$NON-NLS-1$
         }
      }
      catch (ParseException e) {
         throw new SqlParserException("SQL syntax error", e); //$NON-NLS-1$
      }
   }

   private SqlQuery createUserQuery(PlainSelect plainSelect, String sqlString)
   {
      SqlQuery toReturn = new SqlSelectQuery();
      SelectItemList selectItems = getSelectItem(plainSelect);
      for (SqlSelectItem selectItem : selectItems) {
         toReturn.addSelectItem(selectItem);
      }
      toReturn.setFromExpression(new SqlUserQuery(sqlString, MockColumn.MOCK_NAMESPACE));
      return toReturn;
   }

   private SelectItemList getSelectItem(PlainSelect plainSelect)
   {
      mSelectItems = new SelectItemList();
      List<SelectItem> selectItemExpressions = plainSelect.getSelectItems();
      for (SelectItem selectItem : selectItemExpressions) {
         selectItem.accept(this);
      }
      return mSelectItems;
   }

   @Override
   public void visit(AllColumns allColumns)
   {
      throw new SqlParserException("User query needs explicit select column statement"); //$NON-NLS-1$
   }

   @Override
   public void visit(AllTableColumns allTableColumns)
   {
      throw new SqlParserException("User query needs explicit select column statement"); //$NON-NLS-1$
   }

   @Override
   public void visit(SelectExpressionItem selectExpressionItem)
   {
      String identifier = getAliasName(selectExpressionItem);
      if (StringUtils.isEmpty(identifier)) {
         Expression expr = selectExpressionItem.getExpression();
         if (expr instanceof Column) {
            Column column = (Column) expr;
            identifier = removeQuotesIfAny(column.getColumnName());
         }
         else {
            throw new SqlParserException("Select item has no identifier: " + expr.toString()); //$NON-NLS-1$
         }
      }
      SqlColumn column = new SqlColumn(new MockColumn(identifier));
      mSelectItems.add(new SqlSelectItem(column));
   }

   private String getAliasName(SelectExpressionItem selectExpressionItem)
   {
      Alias alias = selectExpressionItem.getAlias();
      if (alias != null) {
         return removeQuotesIfAny(alias.getName());
      }
      return ""; //$NON-NLS-1$ // empty alias name
   }

   private static String removeQuotesIfAny(String value)
   {
      return value.replaceAll("^\"|\"$", "").replaceAll("^`|`$", ""); //$NON-NLS-1$ //$NON-NLS-2$
   }
}
