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
package com.obidea.semantika.database.internal;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import com.obidea.semantika.database.QualifiedName;
import com.obidea.semantika.database.base.Column;
import com.obidea.semantika.database.base.ForeignKey;
import com.obidea.semantika.database.base.IColumn;
import com.obidea.semantika.database.base.IForeignKey;
import com.obidea.semantika.database.base.IPrimaryKey;
import com.obidea.semantika.database.base.ISchema;
import com.obidea.semantika.database.base.ITable;
import com.obidea.semantika.database.base.PrimaryKey;
import com.obidea.semantika.database.base.Schema;
import com.obidea.semantika.database.base.Table;
import com.obidea.semantika.database.exception.AmbiguousNameFoundException;
import com.obidea.semantika.database.exception.TableNotFoundException;
import com.obidea.semantika.exception.IllegalOperationException;
import com.obidea.semantika.util.StringUtils;

public class InternalDatabase extends AbstractInternalDatabase
{
   private DatabaseMetaData mDbMetadata;

   public InternalDatabase(DatabaseMetaData metadata) throws SQLException
   {
      mDbMetadata = metadata;
   }

   @Override
   public TemporaryTable findTable(String anyTableName) throws SQLException
   {
      List<TemporaryTable> possibleTables = findCandidateTables(anyTableName);
      if (possibleTables.size() == 0) {
         throw new TableNotFoundException(anyTableName);
      }
      if (possibleTables.size() > 1) {
         throw new AmbiguousNameFoundException(anyTableName, possibleTables);
      }
      return possibleTables.get(0);
   }

   @Override
   public void retrieveColumns(final ITable table) throws SQLException
   {
      final String schemaName = table.getSchemaName();
      final String tableName = table.getLocalName();
      
      ResultSet rs = null;
      try {
         rs = mDbMetadata.getColumns(null, schemaName, tableName, null);
         while (rs.next()) {
            final String columnName = rs.getString("COLUMN_NAME"); //$NON-NLS-1$
            final int datatype = rs.getInt("DATA_TYPE"); //$NON-NLS-1$
            IColumn column = new Column(table, columnName, datatype);
            table.addColumn(column); // update the table
         }
      }
      finally {
         if (rs != null && !rs.isClosed()) {
            rs.close();
         }
      }
   }

   @Override
   public Set<IPrimaryKey> retrievePrimaryKeys(final ITable table) throws SQLException
   {
      final String schemaName = table.getSchemaName();
      final String tableName = table.getLocalName();
      
      Set<IPrimaryKey> toReturn = new HashSet<IPrimaryKey>();
      ResultSet rs = null;
      try {
         WeakHashMap<String, IPrimaryKey> pkCache = new WeakHashMap<String, IPrimaryKey>();
         rs = mDbMetadata.getPrimaryKeys(null, schemaName, tableName);
         while (rs.next()) {
            int keySequence = rs.getInt("KEY_SEQ"); //$NON-NLS-1$
            String pkName = rs.getString("PK_NAME"); //$NON-NLS-1$
            String pkColumnName = rs.getString("COLUMN_NAME"); //$NON-NLS-1$
            
            IPrimaryKey pk = pkCache.get(pkName);
            if (pk == null) {
               pk = new PrimaryKey(table, pkName);
               pkCache.put(pkName, pk);
               toReturn.add(pk); // add to the returned set
            }
            IColumn column = table.getColumn(pkColumnName);
            column.setPrimaryKey(true);
            pk.addKey(keySequence-1, column); // the key sequence starts at 1 instead of 0
         }
      }
      finally {
         if (rs != null && !rs.isClosed()) {
            rs.close();
         }
      }
      return toReturn;
   }

   @Override
   public Set<IForeignKey> retrieveForeignKeys(final ITable table) throws SQLException
   {
      final String schemaName = table.getSchemaName();
      final String tableName = table.getLocalName();
      
      Set<IForeignKey> toReturn = new HashSet<IForeignKey>();
      ResultSet rs = null;
      try {
         WeakHashMap<String, IForeignKey> fkCache = new WeakHashMap<String, IForeignKey>();
         rs = mDbMetadata.getImportedKeys(null, schemaName, tableName);
         
         while (rs.next()) {
            int keySequence = rs.getInt("KEY_SEQ"); //$NON-NLS-1$
            String fkName = rs.getString("FK_NAME"); //$NON-NLS-1$
            String pkSchemaName = rs.getString("PKTABLE_SCHEM"); //$NON-NLS-1$
            if (StringUtils.isEmpty(pkSchemaName)) {
               /*
                * Connector/J for MySQL uses table catalog as its table schema. Others,
                * like Oracle uses TABLE_SCHEM as table schema
                */
               pkSchemaName = rs.getString("PKTABLE_CAT"); //$NON-NLS-1$
            }
            String pkTableName = rs.getString("PKTABLE_NAME"); //$NON-NLS-1$
            String pkColumnName = rs.getString("PKCOLUMN_NAME"); //$NON-NLS-1$
            String fkColumnName = rs.getString("FKCOLUMN_NAME"); //$NON-NLS-1$
            
            IForeignKey fk = fkCache.get(fkName);
            if (fk == null) {
               fk = new ForeignKey(table, fkName);
               fkCache.put(fkName, fk);
               toReturn.add(fk); // add to the returned set
            }
            IColumn pkColumn = getColumnFromTable(pkSchemaName, pkTableName, pkColumnName);
            IColumn fkColumn = table.getColumn(fkColumnName);
            fk.addReference(keySequence-1, pkColumn, fkColumn); // the key sequence starts at 1 instead of 0
         }
      }
      finally {
         if (rs != null && !rs.isClosed()) {
            rs.close();
         }
      }
      return toReturn;
   }

   /*
    * Private helper methods
    */

   private List<TemporaryTable> findCandidateTables(String anyTableName) throws SQLException
   {
      if (StringUtils.isEmpty(anyTableName)) {
         throw new IllegalOperationException("The table name must not be empty."); //$NON-NLS-1$
      }
      QualifiedName qname = QualifiedName.create(anyTableName);
      String schemaPattern = qname.getIdentifier(1);
      String tableNamePattern = qname.getIdentifier(0);
      
      ResultSet rs = null;
      List<TemporaryTable> toReturn = new ArrayList<TemporaryTable>();
      try {
         rs = mDbMetadata.getTables(null, schemaPattern, tableNamePattern, null);
         while (rs.next()) {
            String schemaName = rs.getString("TABLE_SCHEM"); //$NON-NLS-1$
            if (StringUtils.isEmpty(schemaName)) {
               /*
                * Connector/J for MySQL uses table catalog as its table schema. Others,
                * like Oracle uses TABLE_SCHEM as table schema
                */
               schemaName = rs.getString("TABLE_CAT"); //$NON-NLS-1$
            }
            String tableName = rs.getString("TABLE_NAME"); //$NON-NLS-1$
            TemporaryTable table = new TemporaryTable(schemaName, tableName);
            toReturn.add(table);
         }
      }
      finally {
         if (rs != null && !rs.isClosed()) {
            rs.close();
         }
      }
      return toReturn;
   }

   private IColumn getColumnFromTable(String schemaName, String tableName, String columnName) throws SQLException
   {
      ITable table = new Table(getSchema(schemaName), tableName);
      retrieveColumns(table);
      return table.getColumn(columnName);
   }

   private static ISchema getSchema(String name)
   {
      return new Schema(name);
   }
}
