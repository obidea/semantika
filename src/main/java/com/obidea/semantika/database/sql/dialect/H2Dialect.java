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
package com.obidea.semantika.database.sql.dialect;

import java.sql.Types;
import java.util.List;

import com.obidea.semantika.database.sql.deparser.SqlDeparserException;
import com.obidea.semantika.exception.SemantikaRuntimeException;

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
      throw new SqlDeparserException("Unable to produce SQL lang expression. " +
            "H2 doesn't have the sufficient built-in expressions"); //$NON-NLS-1$
   }

   @Override
   public String cast(String column, int datatype)
   {
      return "CAST(" + column + " AS " + getTypeName(datatype) + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
   }

   private String getTypeName(int datatype)
   {
      switch (datatype) {
         case Types.VARCHAR:
         case Types.NVARCHAR:
         case Types.NCHAR:
         case Types.LONGVARCHAR:
         case Types.LONGNVARCHAR: return "CHAR"; //$NON-NLS-1$
         case Types.NUMERIC:
         case Types.DECIMAL:
         case Types.BIGINT:
         case Types.INTEGER:
         case Types.SMALLINT:
         case Types.TINYINT:
         case Types.REAL:
         case Types.FLOAT:
         case Types.DOUBLE: return "DECIMAL"; //$NON-NLS-1$
         case Types.DATE: return "DATE"; //$NON-NLS-1$
         case Types.TIME: return "TIME"; //$NON-NLS-1$
         case Types.TIMESTAMP: return "DATETIME"; //$NON-NLS-1$
         case Types.OTHER: return "CHAR"; //$NON-NLS-1$
      }
      throw new SemantikaRuntimeException("Failed to construct CAST (datatype: " + datatype + ")");
   }
}
