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
package com.obidea.semantika.database;

import com.obidea.semantika.util.StringUtils;

public class NamingUtils
{
   /**
    * Separator character for database object
    */
   private static final String DBO_SEPARATOR = "."; //$NON-NLS-1$

   /**
    * Separator character for expression object
    */
   private static final String EXO_SEPARATOR = "__"; //$NON-NLS-1$

   /**
    * Constructs namespace string for a database object, given the name components.
    * Each name component is separated by a period.
    * 
    * @param names
    *           the name components for constructing the namespace.
    * @return namespace string.
    */
   public static String constructDatabaseObjectNamespace(final String... names)
   {
      StringBuilder sb = new StringBuilder();
      boolean needSeparator = false;
      for (String name : names) {
         if (StringUtils.isEmpty(name)) {
            continue;
         }
         if (needSeparator) {
            sb.append(DBO_SEPARATOR);
         }
         sb.append(name);
         needSeparator = true;
      }
      return sb.toString();
   }

   /**
    * Constructs identifier string for a database object, given the namespace and
    * the local name. Both are separated by a period.
    * 
    * @param namespace
    *           the namespace string.
    * @param localName
    *           the local name.
    * @return identifier name.
    */
   public static String constructDatabaseObjectIdentifier(final String namespace, final String localName)
   {
      StringBuilder sb = new StringBuilder();
      if (!StringUtils.isEmpty(namespace)) {
         sb.append(namespace).append(DBO_SEPARATOR);
      }
      if (!StringUtils.isEmpty(localName)) {
         sb.append(localName);
      }
      return sb.toString();
   }

   /**
    * Constructs label string for any expression object, given the name
    * components. Each name component is separated by a double underscore. In
    * addition, the name construction is regulated as following:
    * <p>
    * <ul>
    * <li>Replace whitespace with an underscore,</li>
    * <li>Convert the name string to upper case</li>
    * </ul>
    * <p>
    * An example for calling this method:
    * <pre>
    * ...
    * NamingUtils.label("DB 12ACT", "Lab.Species", "species_name");  // returns "DB_12ACT__LAB.SPECIES__SPECIES_NAME"
    * ...
    * </pre>
    *
    * @param names
    *          the name components for constructing the label name.
    * @return label string.
    */
   public static String constructExpressionObjectLabel(final String... names)
   {
      StringBuilder sb = new StringBuilder();
      boolean needSeparator = false;
      for (String name : names) {
         if (StringUtils.isEmpty(name)) {
            continue;
         }
         if (needSeparator) {
            sb.append(EXO_SEPARATOR);
         }
         String label = StringUtils.toUpperCase(StringUtils.useUnderscore(name));
         sb.append(label);
         needSeparator = true;
      }
      return sb.toString();
   }
}
