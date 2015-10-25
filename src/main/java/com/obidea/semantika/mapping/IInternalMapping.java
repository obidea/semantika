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
package com.obidea.semantika.mapping;

import java.util.Set;

interface IInternalMapping
{
   /**
    * A marker interface for objects that contain maps.
    */
   public interface Pointer<K, V> { 
      Set<K> keySet();
      Set<V> values(K key);
      Set<V> getAllValues();
      boolean containsKey(K key);
      boolean containsValue(V value);
      boolean put(K key, V value);
      boolean remove(K key, V value);
      int size();
   }

   <K, V> Set<K> getKeyset(Pointer<K, V> pointer);

   <K, V> Set<V> getAllValues(Pointer<K, V> pointer);

   <K, V> Set<V> getValues(Pointer<K, V> pointer, K key);

   <K, V> boolean containsKey(Pointer<K, V> pointer, K key);

   <K, V> boolean add(Pointer<K, V> pointer, K key, V value);

   <K, V> boolean remove(Pointer<K, V> pointer, K key, V value);
}
