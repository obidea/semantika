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

import java.util.Set;

import com.obidea.semantika.database.base.IDatabaseObject;
import com.obidea.semantika.database.base.IForeignKey;
import com.obidea.semantika.database.base.IPrimaryKey;
import com.obidea.semantika.database.base.ITable;

interface IInternalDatabase
{
   /**
    * A utility interface for storing key-value map entries.
    */
   public interface MapPointer<K, V> { 
      void put(K key, V value);
      Set<V> values();
      V get(K key);
   }

   /**
    * A utility interface for storing value set entries.
    */
   public interface SetPointer<E> { 
      void add(E element);
      Set<E> values();
   }

   MapPointer<String, ? extends ITable> getTableReferences();

   SetPointer<? extends IPrimaryKey> getPrimaryKeyReferences();

   SetPointer<? extends IForeignKey> getForeignKeyReferences();

   <K, V extends IDatabaseObject> void add(MapPointer<K, V> pointer, K key, V value);

   <K, V extends IDatabaseObject> Set<V> getAllValues(MapPointer<K, V> pointer);

   <K, V extends IDatabaseObject> V getValue(MapPointer<K, V> pointer, K key);
   
   <E extends IDatabaseObject> void add(SetPointer<E> pointer, E value);
   
   <E extends IDatabaseObject> Set<E> getAllValues(SetPointer<E> pointer);
}
