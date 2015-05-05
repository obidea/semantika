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
package com.obidea.semantika.database.internal;

import java.sql.SQLException;

import org.slf4j.Logger;

import com.obidea.semantika.database.base.IForeignKey;
import com.obidea.semantika.database.base.IPrimaryKey;
import com.obidea.semantika.database.base.ITable;
import com.obidea.semantika.database.base.Table;
import com.obidea.semantika.exception.SemantikaRuntimeException;
import com.obidea.semantika.util.LogUtils;

public class TableMapPointer extends LazyMapPointer<String, ITable>
{
   private InternalDatabase mInternal;

   private static final Logger LOG = LogUtils.createLogger("semantika.database.internal"); //$NON-NLS-1$

   public TableMapPointer(AbstractInternalDatabase internal)
   {
      mInternal = (InternalDatabase) internal;
   }

   @Override
   public void find(String tableName)
   {
      try {
         /*
          * Check if the given table name already exists
          */
         if (mMap.containsKey(tableName)) {
            return;
         }
         else {
            /*
             * Use its canonical name (i.e., full-qualified name) to check. It is possible
             * that the same table is already registered using its qualified name.
             */
            final TemporaryTable temporaryTable = mInternal.findTable(tableName);
            String canonicalName = temporaryTable.getFullName();
            if (mMap.containsKey(canonicalName)) {
               return;
            }
            else {
               /*
                * Insert this new table using the given table name as keyword.
                */
               insertNewTable(tableName, temporaryTable);
            }
         }
      }
      catch (SQLException e) {
         LOG.error("Failed to construct the internal database"); //$NON-NLS-1$
         LOG.error("Detailed cause: {}", e.getMessage()); //$NON-NLS-1$
         throw new SemantikaRuntimeException(e);
      }
   }

   private void insertNewTable(String keyword, TemporaryTable temporaryTable)
   {
      try {
         ITable table = new Table(temporaryTable);
         mInternal.retrieveColumns(table);
         
         /*
          * With the find() method above, the map keyword will always be unique
          * across the data sources. The keyword can be table's local name or
          * qualified name.
          */
         mMap.put(keyword, table);
         
         for (IPrimaryKey pk : mInternal.retrievePrimaryKeys(table)) {
            mInternal.add(mInternal.getPrimaryKeyReferences(), pk);
         }
         for (IForeignKey fk : mInternal.retrieveForeignKeys(table)) {
            mInternal.add(mInternal.getForeignKeyReferences(), fk);
         }
      }
      catch (SQLException e) {
         LOG.error("Failed to construct the internal database"); //$NON-NLS-1$
         LOG.error("Detailed cause: {}", e.getMessage()); //$NON-NLS-1$
         throw new SemantikaRuntimeException(e);
      }
   }
}
