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
package com.obidea.semantika.database.sql.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.obidea.semantika.database.base.IColumn;
import com.obidea.semantika.database.base.ITable;
import com.obidea.semantika.mapping.base.sql.SqlColumn;
import com.obidea.semantika.mapping.base.sql.SqlTable;
import com.obidea.semantika.mapping.base.sql.parser.SqlMappingParserException;
import com.obidea.semantika.util.StringUtils;

/**
 * A utility class to keep a list of tables in the FROM clause.
 */
/* package */class FromTablesList implements Iterator<SqlTable>, Iterable<SqlTable>
{
   private List<ITable> mSelectionList = new ArrayList<ITable>();

   private int mSize = 0;
   private int mCurrent = 0;

   public void add(ITable table)
   {
      mSelectionList.add(table);
      mSize++; // increment the size each time new table is added
   }

   /**
    * Gets the column given its name and the table where it is located. If the
    * table name is <code>null</code> or empty, the method will search the
    * column in all tables listed in this list. It will throw an exception if
    * there are more than one column found.
    * 
    * @param anyTableName
    *           table name; can be local name or full name
    * @param columnName
    *           column name
    * @return a column object.
    * @throws UnknownColumnNameException
    *            if the column name does not exist in any selected tables.
    * @throws AmbiguousColumnNameException
    *            if there are more than one column using the same name.
    */
   public SqlColumn getColumn(String anyTableName, String columnName) throws SqlMappingParserException
   {
      IColumn c = (StringUtils.isEmpty(anyTableName)) ?
            findColumnByNameOnly(columnName) :
            findColumnByTableReference(anyTableName, columnName);
      return new SqlColumn(c);
   }

   /**
    * Gets all the column from a table. The table name must come from table
    * selection.
    * 
    * @param anyTableName
    *           table name; can be local name or full name
    * @return a list of columns.
    */
   public List<SqlColumn> getColumns(String anyTableName) throws SqlMappingParserException
   {
      List<SqlColumn> toReturn = new ArrayList<SqlColumn>();
      ITable sourceTable = findTableInSelectionList(anyTableName);
      for (IColumn c : sourceTable.getColumns()) {
         toReturn.add(new SqlColumn(c));
      }
      return toReturn;
   }

   /**
    * Gets all columns from all selected table in the FROM clause.
    *
    * @return a list of columns.
    */
   public List<SqlColumn> getAllColumns()
   {
      List<SqlColumn> toReturn = new ArrayList<SqlColumn>();
      for (ITable sourceTable : mSelectionList) {
         for (IColumn c : sourceTable.getColumns()) {
            toReturn.add(new SqlColumn(c));
         }
      }
      return toReturn;
   }

   /*
    * Method implementation for the Iterator and Iterable interfaces.
    */

   @Override
   public boolean hasNext()
   {
      return mCurrent < mSize;
   }

   @Override
   public SqlTable next()
   {
      if (hasNext()) {
         final ITable nextTable = mSelectionList.get(mCurrent++);
         return new SqlTable(nextTable);
      }
      throw new NoSuchElementException();
   }

   @Override
   public void remove()
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public Iterator<SqlTable> iterator()
   {
      mCurrent = 0; // clear the current count every time client code asks for an iterator
      return this;
   }

   /*
    * Private helper methods
    */

   private ITable findTableInSelectionList(String tableName) throws SqlMappingParserException
   {
      for (ITable table : mSelectionList) {
         // Check based its local name first
         if (tableName.equals(table.getLocalName())) {
            return table;
         }
         // If failed, check based on its full name
         if (tableName.equals(table.getFullName())) {
            return table;
         }
      }
      // Give up
      throw new UnknownTableNameException(tableName);
   }

   private IColumn findColumnByTableReference(String tableName, String columnName) throws SqlMappingParserException
   {
      final ITable table = findTableInSelectionList(tableName);
      IColumn column = table.getColumn(columnName);
      if (column != null) {
         return column;
      }
      throw new UnknownColumnNameException(columnName);
   }

   private IColumn findColumnByNameOnly(String columnName) throws SqlMappingParserException
   {
      List<IColumn> possibleColumns = new ArrayList<IColumn>();
      for (ITable table : mSelectionList) {
         IColumn column = table.getColumn(columnName);
         if (column != null) {
            possibleColumns.add(column);
         }
      }
      if (possibleColumns.size() == 0) {
         throw new UnknownColumnNameException(columnName);
      }
      if (possibleColumns.size() > 1) {
         throw new AmbiguousColumnNameException(columnName, possibleColumns);
      }
      return possibleColumns.get(0);
   }
}
