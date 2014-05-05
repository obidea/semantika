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
package com.obidea.semantika.mapping;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.obidea.semantika.mapping.base.IMapping;
import com.obidea.semantika.util.MultiMap;

class MapPointer<K, V extends IMapping> implements IInternalMapping.Pointer<K, V>, Serializable
{
   private static final long serialVersionUID = 629451L;

   private MultiMap<K, V> mMap;

   public MapPointer()
   {
      mMap = new MultiMap<K, V>(true);
   }

   @Override
   public Set<K> keySet()
   {
      return mMap.keySet();
   }

   @Override
   public Set<V> values(K key)
   {
      return new HashSet<V>(mMap.get(key));
   }

   @Override
   public Set<V> getAllValues()
   {
      return mMap.getAllValues();
   }

   @Override
   public boolean containsKey(K key)
   {
      return mMap.containsKey(key);
   }

   @Override
   public boolean containsValue(V value)
   {
      return mMap.containsValue(value);
   }

   @Override
   public boolean put(K key, V value)
   {
      return mMap.put(key, value);
   }

   @Override
   public boolean remove(K key, V value)
   {
      return mMap.remove(key, value);
   }

   @Override
   public int size()
   {
      return mMap.size();
   }
}
