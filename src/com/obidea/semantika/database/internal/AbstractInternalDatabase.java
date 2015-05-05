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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.obidea.semantika.database.base.IDatabaseObject;
import com.obidea.semantika.database.base.IForeignKey;
import com.obidea.semantika.database.base.IPrimaryKey;
import com.obidea.semantika.database.base.ITable;

/* package */abstract class AbstractInternalDatabase implements IInternalDatabase
{
   class SimpleSetPointer<E> implements IInternalDatabase.SetPointer<E>
   {
      private Set<E> mSet = new HashSet<E>();

      /**
       * @return a copy values of this set pointer
       */
      @Override
      public Set<E> values()
      {
         if (mSet.isEmpty()) {
            return Collections.emptySet();
         }
         return new HashSet<E>(mSet);
      }

      @Override
      public void add(E k)
      {
         mSet.add(k);
      }
   }

   @Override
   public TableMapPointer getTableReferences()
   {
      return mTableReferences;
   }

   @Override
   public SetPointer<IPrimaryKey> getPrimaryKeyReferences()
   {
      return mPrimaryKeyReferences;
   }

   @Override
   public SetPointer<IForeignKey> getForeignKeyReferences()
   {
      return mForeignKeyReferences;
   }

   @Override
   public <K, V extends IDatabaseObject> void add(MapPointer<K, V> pointer, K key, V value)
   {
      pointer.put(key, value);
   }

   @Override
   public <K, V extends IDatabaseObject> Set<V> getAllValues(MapPointer<K, V> pointer)
   {
      return Collections.unmodifiableSet(pointer.values());
   }

   @Override
   public <K, V extends IDatabaseObject> V getValue(MapPointer<K, V> pointer, K key)
   {
      final LazyMapPointer<K, V> lazyPointer = (LazyMapPointer<K, V>) pointer;
      lazyPointer.find(key);
      return lazyPointer.get(key);
   }

   @Override
   public <E extends IDatabaseObject> void add(SetPointer<E> pointer, E value)
   {
      pointer.add(value);
   }

   @Override
   public <E extends IDatabaseObject> Set<E> getAllValues(SetPointer<E> pointer)
   {
      return Collections.unmodifiableSet(pointer.values());
   }

   private final TableMapPointer mTableReferences = buildTableReferences();

   private final SimpleSetPointer<IPrimaryKey> mPrimaryKeyReferences = buildSet();
   private final SimpleSetPointer<IForeignKey> mForeignKeyReferences = buildSet();

   private TableMapPointer buildTableReferences()
   {
      return new TableMapPointer(this); // lazy build
   }

   private <E> SimpleSetPointer<E> buildSet()
   {
      return new SimpleSetPointer<E>();
   }

   // ** Helper methods to fetch database meta-data information **

   /**
    * Retrieves a <code>Table</code> object given its name. The method uses
    * database metadata to create the object.
    * 
    * A runtime exception will be thrown if the method finds multiple tables
    * that has the same name (i.e., ambiguous name found).
    * 
    * @param anyTableName
    *           The table name. Can be a simple name or fully-qualified name
    * @return A <code>Table</code> object that represents the input table name
    * @throws SQLException
    *            errors related to database access or other.
    */
   public abstract ITable findTable(String anyTableName) throws SQLException;

   /**
    * Retrieves columns from the database metadata and adds to the given input
    * <code>table</code>.
    * 
    * @param table
    *           the target table
    * @throws SQLException
    *            errors related to database access or other.
    */
   public abstract void retrieveColumns(final ITable table) throws SQLException;

   /**
    * Retrieves primary key constraints from the database metadata for the given
    * input <code>table</code>.
    * 
    * @param table
    *           the target table.
    * @return a set of <code>PrimaryKey</code> objects.
    * @throws SQLException
    *            errors related to database access or other.
    */
   public abstract Set<IPrimaryKey> retrievePrimaryKeys(final ITable table) throws SQLException;

   /**
    * Retrieves foreign key constraints from the database metadata for the given
    * input <code>table</code>.
    * 
    * @param table
    *           the target table.
    * @return a set of <code>ForeignKey</code> objects.
    * @throws SQLException
    *            errors related to database access or other.
    */
   public abstract Set<IForeignKey> retrieveForeignKeys(final ITable table) throws SQLException;
}
