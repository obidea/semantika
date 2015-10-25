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
package com.obidea.semantika.util;

import java.io.Serializable;

import org.apache.commons.collections4.map.AbstractReferenceMap.ReferenceStrength;
import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.collections4.map.ReferenceMap;

/**
 * Cache following a "Most Recently Used" (MRU) algorithm for maintaining a bounded in-memory size;
 * the "Least Recently Used" (LRU) entry is the first available for removal from the cache.
 * 
 * The class is based on <code>SoftLimitMRUCache</code> implementation from Hibernate Utility API.
 */
public class SoftMruCache implements Serializable
{
   private static final long serialVersionUID = 629451L;

   public static final int DEFAULT_CACHE_SIZE = 128;

   private int mCacheSize;

   private transient ReferenceMap<Object, Object> mActualCache = new ReferenceMap<Object, Object>(ReferenceStrength.SOFT, ReferenceStrength.SOFT);

   private transient LRUMap<Object, Object> mSafeguardCache;

   public SoftMruCache()
   {
      this(DEFAULT_CACHE_SIZE);
   }

   public SoftMruCache(int cacheSize)
   {
      this.mCacheSize = cacheSize;
      init();
   }

   private void init()
   {
      mSafeguardCache = new LRUMap<Object, Object>(mCacheSize);
   }

   public synchronized Object get(Object key)
   {
      Object result = mActualCache.get(key);
      if (result != null) {
         mSafeguardCache.put(key, result);
      }
      return result;
   }

   public synchronized Object put(Object key, Object value)
   {
      mActualCache.put(key, value);
      return mSafeguardCache.put(key, value);
   }

   public synchronized int size()
   {
      return mSafeguardCache.size();
   }

   public synchronized int softSize()
   {
      return mActualCache.size();
   }

   public synchronized void clear()
   {
      mSafeguardCache.clear();
      mActualCache.clear();
   }
}
