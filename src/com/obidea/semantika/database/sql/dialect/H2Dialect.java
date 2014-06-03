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

import java.util.List;

import com.obidea.semantika.database.sql.parser.SqlException;

public class H2Dialect extends Sql99Dialect
{
   public H2Dialect()
   {
      super();
   }

   @Override
   public String concat(List<String> parameters)
   {
      StringBuilder sb = new StringBuilder();
      sb.append("CONCAT"); //$NON-NLS-1$
      sb.append("("); //$NON-NLS-1$
      boolean needConcat = false;
      for (String parameter : parameters) {
         if (needConcat) {
            sb.append(","); //$NON-NLS-1$
         }
         sb.append(parameter);
         needConcat = true;
      }
      sb.append(")"); //$NON-NLS-1$
      return sb.toString();
   }

   @Override
   public String regex(String column, String pattern, String flag)
   {
      return column + " REGEXP " + pattern; //$NON-NLS-1$
   }

   @Override
   public String lang(String text)
   {
      throw new SqlException("Unable to produce SQL string for LANG expression:" +
            "H2 doesn't have the sufficient built-in expressions"); //$NON-NLS-1$
   }
}
