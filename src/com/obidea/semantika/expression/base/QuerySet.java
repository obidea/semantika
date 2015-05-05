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
package com.obidea.semantika.expression.base;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.obidea.semantika.util.CollectionUtils;

public class QuerySet<E> implements Serializable
{
   private static final long serialVersionUID = 629451L;

   private Set<E> mQueries = new HashSet<E>();

   public QuerySet()
   {
      // NO-OP
   }

   public void copy(QuerySet<E> otherSet)
   {
      for (final E query : otherSet.getAll()) {
         add(query);
      }
   }

   public void add(E query)
   {
      if (query != null) {
         mQueries.add(query);
      }
   }

   public void remove(E query)
   {
      if (query != null) {
         mQueries.remove(query);
      }
   }

   public E get(int index)
   {
      return CollectionUtils.toList(mQueries).get(index);
   }

   public Set<E> getAll()
   {
      return Collections.unmodifiableSet(mQueries);
   }

   public int size()
   {
      return mQueries.size();
   }

   public boolean isEmpty()
   {
      return (size() == 0) ? true : false;
   }
}
