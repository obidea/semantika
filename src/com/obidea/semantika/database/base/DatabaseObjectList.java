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
package com.obidea.semantika.database.base;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.obidea.semantika.database.NamingUtils;

public class DatabaseObjectList<E extends IDatabaseObject> implements Serializable
{
   private static final long serialVersionUID = 629451L;

   private final Map<String, E> mObjects;

   public DatabaseObjectList()
   {
      mObjects = new LinkedHashMap<String, E>();
   }

   public void add(final E object)
   {
      if (object == null) {
         throw new IllegalArgumentException("Cannot add null object to the list."); //$NON-NLS-1$
      }
      final String key = object.getFullName();
      mObjects.put(key, object);
   }

   public E get(final String namespace, final String localName)
   {
      final String identifier = NamingUtils.constructDatabaseObjectIdentifier(namespace, localName);
      return get(identifier);
   }

   public E get(String identifier)
   {
      return mObjects.get(identifier);
   }

   public List<E> values()
   {
      return new LinkedList<E>(mObjects.values());
   }

   public boolean isEmpty()
   {
      return mObjects.isEmpty();
   }
}
