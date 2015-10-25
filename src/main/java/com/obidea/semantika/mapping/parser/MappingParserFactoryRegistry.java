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
package com.obidea.semantika.mapping.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MappingParserFactoryRegistry
{
   private static MappingParserFactoryRegistry sInstance;

   private List<IMappingParserFactory> mParserFactory;

   public MappingParserFactoryRegistry()
   {
      mParserFactory = new ArrayList<IMappingParserFactory>();
   }

   public static MappingParserFactoryRegistry getInstance()
   {
      if (sInstance == null) {
         sInstance = new MappingParserFactoryRegistry();
      }
      return sInstance;
   }

   /**
    * Registers the mapping parser factory with priority such that the top
    * priority parser will be tried first.
    * 
    * @param priority
    *           The order priority. Priority = <code>0</code> has the highest
    *           priority.
    * @param parserFactory
    *           The factory to create the mapping parser.
    */
   public void register(int priority, IMappingParserFactory parserFactory)
   {
      mParserFactory.add(priority, parserFactory);
   }

   /**
    * Registers the mapping parser factory with subsequent order.
    * 
    * @param parserFactory
    *           The factory to create the mapping parser.
    */
   public void register(IMappingParserFactory parserFactory)
   {
      mParserFactory.add(parserFactory);
   }

   /**
    * Removes the mapping parser factory from this registry.
    * 
    * @param parserFactory
    *           The factory to remove.
    */
   public void unregister(IMappingParserFactory parserFactory)
   {
      mParserFactory.remove(parserFactory);
   }

   public List<IMappingParserFactory> getFactories()
   {
      return Collections.unmodifiableList(mParserFactory);
   }

   public void clearFactories()
   {
      mParserFactory.clear();
   }
}
