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

import java.io.Serializable;
import java.net.URI;
import java.util.Collections;
import java.util.Set;

import com.obidea.semantika.mapping.base.IMapping;

public abstract class AbstractInternalMapping implements IInternalMapping, Serializable
{
   private static final long serialVersionUID = 629451L;

   protected Pointer<URI, IMapping> getMappingBySignature()
   {
      return mMappingBySignature;
   }

   protected Pointer<MappingType<?>, IMapping> getMappingByType()
   {
      return mMappingByType;
   }

   public <K, V> boolean add(Pointer<K, V> pointer, K key, V value)
   {
      return pointer.put(key, value);
   }

   public <K, V> boolean remove(Pointer<K, V> pointer, K key, V value)
   {
      return pointer.remove(key, value);
   }

   public <K, V> Set<K> getKeyset(Pointer<K, V> pointer)
   {
      return Collections.unmodifiableSet(pointer.keySet());
   }

   public <K, V> Set<V> getAllValues(Pointer<K, V> pointer)
   {
      return Collections.unmodifiableSet(pointer.getAllValues());
   }

   public <K, V> Set<V> getValues(Pointer<K, V> pointer, K key)
   {
      return Collections.unmodifiableSet(pointer.values(key));
   }

   public <K, V> boolean containsKey(Pointer<K, V> pointer, K key)
   {
      return pointer.containsKey(key);
   }

   private final MapPointer<URI, IMapping> mMappingBySignature = build();
   private final MapPointer<MappingType<?>, IMapping> mMappingByType = build();

   private <K, V extends IMapping> MapPointer<K, V> build()
   {
      return new MapPointer<K, V>();
   }
}
