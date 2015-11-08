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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.obidea.semantika.database.sql.base.ISqlExpression;

/* package */class SqlInternal
{
   private SqlQueryWalker mSqlWalker;

   /**
    * A map to store the select item expressions and its label name.
    */
   private Map<String, ISqlExpression> mSelectItemExpressionMap = new HashMap<String, ISqlExpression>();

   /**
    * A map to store the table object and its string name.
    */
   private Map<String, SqlTable> mTableMap = new HashMap<String, SqlTable>();

   /**
    * A list of column objects used in the <code>SqlQuery</code> object.
    */
   private List<SqlColumn> mColumnList = new ArrayList<SqlColumn>();

   /**
    * A sole constructor.
    */
   public SqlInternal(SqlQuery sqlQuery)
   {
      mSqlWalker = new SqlQueryWalker(sqlQuery);
   }

   public void addTable(String tableName, SqlTable table)
   {
      mTableMap.put(tableName, table);
   }

   public void addColumn(SqlColumn column)
   {
      mColumnList.add(column);
   }

   public void addSelectItem(String selectItemLabel, ISqlExpression selectItemExpression)
   {
      mSelectItemExpressionMap.put(selectItemLabel, selectItemExpression);
   }

   public List<SqlColumn> getColumns()
   {
      findAllColumns();
      return mColumnList;
   }

   public List<SqlTable> getTables()
   {
      findAllTables();
      return new ArrayList<SqlTable>(mTableMap.values());
   }

   public SqlTable getTable(String tableName)
   {
      findTable(tableName);
      return mTableMap.get(tableName);
   }

   public ISqlExpression findSelectItemExpression(String selectItemLabel)
   {
      findItemLabel(selectItemLabel);
      return mSelectItemExpressionMap.get(selectItemLabel);
   }

   public void changeColumnNamespace(String targetColumnName, String oldNamespace, String newNamespace)
   {
      findAllColumns();
      for (SqlColumn column : mColumnList) {
         try {
            validateColumnNamespace(column, oldNamespace);
            validateColumnName(column, targetColumnName);
            column.setViewName(newNamespace);
         }
         catch (Exception e) {
            continue;
         }
      }
   }

   private static void validateColumnNamespace(SqlColumn column, String namespace) throws Exception
   {
      /*
       * Check the namespace as column's table name
       */
      String columnTable = column.getTableOrigin();
      if (columnTable.equals(namespace)) {
         return;
      }
      /*
       * Check the namespace as column's view name
       */
      String columnView = column.getViewName();
      if (columnView.equals(namespace)) {
         return;
      }
      throw new Exception(); // give up
   }

   private static void validateColumnName(SqlColumn column, String targetColumnName) throws Exception
   {
      if (targetColumnName.equals("*")) { // * = ignore column name
         return;
      }
      String columnName = column.getColumnName();
      if (targetColumnName.equals(columnName)) {
         return;
      }
      throw new Exception();
   }

   public void clearAllColumns()
   {
      mColumnList.clear();
   }

   /*
    * Private utility methods
    */

   private void findAllTables()
   {
      if (!mTableMap.isEmpty()) {
         return;
      }
      mSqlWalker.doUpdate(this); // lazy walk
   }

   private void findAllColumns()
   {
      if (!mColumnList.isEmpty()) {
         return;
      }
      mSqlWalker.doUpdate(this); // lazy walk
   }

   private void findTable(String tableName)
   {
      if (mTableMap.containsKey(tableName)) {
         return;
      }
      mSqlWalker.doUpdate(this); // lazy walk
   }

   private void findItemLabel(String selectItemLabel)
   {
      if (mSelectItemExpressionMap.containsKey(selectItemLabel)) {
         return;
      }
      mSqlWalker.doUpdate(this); // lazy walk
   }
}
