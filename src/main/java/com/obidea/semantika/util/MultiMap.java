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
package com.obidea.semantika.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

public class MultiMap<K, V> implements Serializable
{
   private static final long serialVersionUID = 629451L;

   private final Map<K, Collection<V>> mMap;
   private int mSize = 0;
   private boolean mUseSets = true;

   private static Logger LOG = LogUtils.createLogger("semantika.utility"); //$NON-NLS-1$

   public MultiMap()
   {
      this(false);
   }

   public MultiMap(boolean useSets)
   {
      mMap = new HashMap<K, Collection<V>>();
      mUseSets = useSets;
   }

   public boolean put(K key, V value)
   {
      Collection<V> set = mMap.get(key);
      if (set == null) {
         set = createCollection();
         mMap.put(key, set);
      }
      boolean toReturn = set.add(value);
      if (toReturn) {
         mSize = -1; // a flag that the map was updated
      }
      return toReturn;
   }

   private Collection<V> createCollection()
   {
      Collection<V> toReturn;
      if (mUseSets) {
         toReturn = new HashSet<V>();
      }
      else {
         toReturn = new ArrayList<V>();
      }
      return toReturn;
   }

   public void setEntry(K key, Collection<V> values)
   {
      mMap.put(key, values);
      this.mSize = -1;
   }

   /**
    * Returns a mutable set of values connected to the key; if no value is
    * connected, returns an immutable empty set
    * 
    * @param key
    *           The map key.
    * @return The collection of values connected with the key
    */
   public Collection<V> get(K key)
   {
      final Collection<V> collection = mMap.get(key);
      if (collection != null) {
         return collection;
      }
      return Collections.emptyList();
   }

   /**
    * @return The key set.
    */
   public Set<K> keySet()
   {
      return mMap.keySet();
   }

   /**
    * @return All values in the map as a set.
    */
   public Set<V> getAllValues()
   {
      Set<V> toReturn = new HashSet<V>();
      for (Collection<V> collection : mMap.values()) {
         for (V v : collection) {
            boolean succeed = toReturn.add(v);
            if (!succeed) {
               LOG.warn("The set already contained the value: {}", v.toString()); //$NON-NLS-1$
            }
         }
      }
      return toReturn;
   }

   /**
    * Removes the collection of values connected to the key
    * 
    * @param key
    *           The map key.
    */
   public boolean remove(K key)
   {
      if (mMap.remove(key) != null) {
         mSize = -1;
         return true;
      }
      return false;
   }

   /**
    * Removes the value connected to the key; if there is more than one value
    * connected to the key, only one is removed
    * 
    * @param key
    *           The map key.
    * @param value
    *           The value to remove in the corresponded collection.
    */
   public boolean remove(K key, V value)
   {
      Collection<V> c = mMap.get(key);
      if (c != null) {
         boolean toReturn = c.remove(value);
         // if false, no change was actually made - skip the rest
         if (!toReturn) {
            return false;
         }
         mSize = -1;
         if (c.isEmpty()) {
            mMap.remove(key);
         }
         return true;
      }
      return false;
   }

   /**
    * @return The size of the multimap (sum of all the sizes of the sets)
    */
   public int size()
   {
      if (mSize < 0) {
         mSize = getAllValues().size();
      }
      return mSize;
   }

   /**
    * @return Returns true if the pairing <key, value> is in the map.
    */
   public boolean contains(K key, V value)
   {
      final Collection<V> collection = mMap.get(key);
      if (collection == null) {
         return false;
      }
      return collection.contains(value);
   }

   /**
    * @return Returns true if the map contains the key.
    */
   public boolean containsKey(K key)
   {
      return mMap.containsKey(key);
   }

   /**
    * @return Returns true if the map contains the value.
    */
   public boolean containsValue(V value)
   {
      for (Collection<V> c : mMap.values()) {
         if (c.contains(value)) {
            return true;
         }
      }
      return false;
   }

   public void clear()
   {
      mMap.clear();
      mSize = 0;
   }

   @Override
   public String toString()
   {
      return "MultiMap " + size() + "\n" + mMap.toString(); //$NON-NLS-1$ //$NON-NLS-2$
   }

   public void putAll(MultiMap<K, V> otherMap)
   {
      for (K k : otherMap.keySet()) {
         putAll(k, otherMap.get(k));
      }
   }

   public void putAll(K key, Collection<V> values)
   {
      Collection<V> set = mMap.get(key);
      if (set == null) {
         set = createCollection();
         setEntry(key, set);
      }
      set.addAll(values);
      mSize = -1;
   }

   public boolean isValueSetsEqual()
   {
      if (mMap.size() < 2) {
         return true;
      }
      List<Collection<V>> list = new ArrayList<Collection<V>>(mMap.values());
      for (int i = 1; i < list.size(); i++) {
         if (!list.get(0).equals(list.get(i))) {
            return false;
         }
      }
      return true;
   }

   public boolean isEmpty()
   {
      return (size() == 0) ? true : false;
   }
}
