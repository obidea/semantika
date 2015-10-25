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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollectionUtils
{
   public static <E extends Object> List<E> createEmptyList(Class<E> clazz)
   {
      return new ArrayList<E>();
   }

   public static <E extends Object> Set<E> createEmptySet(Class<E> clazz)
   {
      return new HashSet<E>();
   }

   public static <E extends Object> List<E> union(List<E> list1, List<E> list2)
   {
      List<E> toReturn = new ArrayList<E>();
      toReturn.addAll(list1);
      toReturn.addAll(list2);
      return toReturn;
   }

   public static <E extends Object> void copy(List<E> source, List<E> target)
   {
      target.addAll(source);
   }

   public static <E extends Object> void copy(Set<E> source, Set<E> target)
   {
      target.addAll(source);
   }

   public static <E extends Object> void move(List<E> source, List<E> target)
   {
      copy(source, target);
      source.clear();
   }

   public static <E extends Object> void move(Set<E> source, Set<E> target)
   {
      copy(source, target);
      source.clear();
   }

   public static <E extends Object> void removeNullFromList(List<E> list)
   {
      list.removeAll(Collections.singleton(null));
   }

   public static <E extends Object> List<E> toList(Set<E> set)
   {
      return new ArrayList<E>(set);
   }

   @SuppressWarnings("unchecked")
   public static <E extends Object> E[] toArray(List<E> list, Class<E> clazz)
   {
      return list.toArray((E[]) Array.newInstance(clazz, list.size()));
   }

   @SuppressWarnings("unchecked")
   public static <E extends Object> E[] toArray(Set<E> set, Class<E> clazz)
   {
      return set.toArray((E[]) Array.newInstance(clazz, set.size()));
   }

   @SuppressWarnings("unchecked")
   public static <E extends Object> List<E> asList(E... values)
   {
      return Arrays.asList(values);
   }
}
