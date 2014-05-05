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
package com.obidea.semantika.database.sql.dialect;

public class PostgreSqlDialect extends Sql99Dialect
{
   public PostgreSqlDialect()
   {
      super();
   }

   @Override
   public String regex(String column, String pattern, String flag)
   {
      if (flag.equalsIgnoreCase("i")) { //$NON-NLS-1$ // XPath flag: http://www.w3.org/TR/xpath-functions/#regex-syntax
         return column + " ~* " + pattern; //$NON-NLS-1$
      }
      else {
         return column + " ~ " + pattern; //$NON-NLS-1$
      }
   }

   @Override
   public String lang(String textExpr)
   {
      return String.format("SUBSTR(SUBSTRING(%s from '@[A-z0-9_-]*$'), 2)", textExpr); //$NON-NLS-1$
   }
}
