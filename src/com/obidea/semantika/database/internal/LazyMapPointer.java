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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.obidea.semantika.database.base.IDatabaseObject;

public abstract class LazyMapPointer<K, V extends IDatabaseObject> implements IInternalDatabase.MapPointer<K, V>
{
   protected Map<K, V> mMap;

   public LazyMapPointer()
   {
      mMap = new HashMap<K, V>();
   }

   public abstract void find(K key);

   @Override
   public void put(K key, V value)
   {
      mMap.put(key, value);
   }

   @Override
   public V get(K key)
   {
      return mMap.get(key);
   }

   /**
    * @return a copy values of this map pointer
    */
   @Override
   public Set<V> values()
   {
      return new HashSet<V>(mMap.values());
   }
}
