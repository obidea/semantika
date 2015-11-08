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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.obidea.semantika.database.sql.SqlPrinter;
import com.obidea.semantika.database.sql.base.ISqlQuery;
import com.obidea.semantika.expression.QueryPrinter;
import com.obidea.semantika.expression.base.IQueryExt;
import com.obidea.semantika.knowledgebase.IPrefixManager;
import com.obidea.semantika.mapping.IMappingSet;
import com.obidea.semantika.mapping.MappingPrinter;
import com.obidea.semantika.mapping.base.IMapping;

public class PrinterUtils
{
   /**
    * Prints out query object in Prolog-like syntax.
    * 
    * @param query
    *           The query object
    * @return a string representation of the query object.
    */
   public static String print(IQueryExt query)
   { 
      return new QueryPrinter().print(query);
   }

   /**
    * Prints out SQL select query object in Prolog-like syntax.
    * 
    * @param query
    *           The SQL query object
    * @return a string representation of the SQL query object.
    */
   public static String print(ISqlQuery query)
   {
      return new SqlPrinter().print(query);
   }

   /**
    * Prints out the mapping set in Prolog-like syntax.
    * 
    * @param mappingSet
    *           The mapping set object.
    * @return a string representation of the mapping set.
    */
   public static String print(IMappingSet mappingSet)
   {
      return print(mappingSet, null);
   }

   /**
    * Prints out the mapping set in Prolog-like syntax.
    * 
    * @param mappingSet
    *           The mapping set object.
    * @param prefixManager
    *           A prefix manager to shorten any URI name into qualified name.
    * @return a string representation of the mapping set.
    */
   public static String print(IMappingSet mappingSet, IPrefixManager prefixManager)
   {
      final StringBuilder sb = new StringBuilder();
      boolean needNewline = false;
      for (IMapping mapping : sortMappingByPredicate(mappingSet)) {
         if (needNewline) {
            sb.append("\n"); //$NON-NLS-1$
         }
         sb.append(print(mapping, prefixManager));
         needNewline = true;
      }
      return sb.toString();
   }

   /**
    * Prints out the mapping object in Prolog-like syntax.
    * 
    * @param mapping
    *           The mapping object
    * @return a string representation of the mapping object.
    */
   public static String print(IMapping mapping)
   {
      return print(mapping, null);
   }

   /**
    * Prints out the mapping object in Prolog-like syntax.
    * 
    * @param mapping
    *           The mapping object
    * @param prefixManager
    *           A prefix manager to shorten any URI name into qualified name.
    * @return a string representation of the mapping object.
    */
   public static String print(IMapping mapping, IPrefixManager prefixManager)
   {
      MappingPrinter printer = new MappingPrinter();
      printer.setPrefixManager(prefixManager);
      return printer.print(mapping);
   }

   /*
    * Private utility method
    */

   private static List<IMapping> sortMappingByPredicate(IMappingSet mappingSet)
   {
      List<IMapping> mappingList = new ArrayList<IMapping>(mappingSet.getAll());
      Collections.sort(mappingList, new Comparator<IMapping>() {
         @Override
         public int compare(IMapping o1, IMapping o2)
         {
            String signature1 = o1.getHeadSymbol().getName();
            String signature2 = o2.getHeadSymbol().getName();
            return signature1.compareToIgnoreCase(signature2);
         }
      });
      return mappingList;
   }
}
