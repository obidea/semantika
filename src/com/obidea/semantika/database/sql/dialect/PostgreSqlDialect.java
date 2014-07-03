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

import java.sql.Types;

import com.obidea.semantika.exception.SemantikaRuntimeException;

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
         case Types.LONGNVARCHAR: return "VARCHAR"; //$NON-NLS-1$
         case Types.NUMERIC:
         case Types.DECIMAL: return "NUMERIC"; //$NON-NLS-1$
         case Types.BIGINT:
         case Types.INTEGER:
         case Types.SMALLINT:
         case Types.TINYINT: return "INTEGER"; //$NON-NLS-1$
         case Types.REAL:
         case Types.FLOAT:
         case Types.DOUBLE: return "DOUBLE PRECISION"; //$NON-NLS-1$
         case Types.DATE: return "DATE"; //$NON-NLS-1$
         case Types.TIME: return "TIME"; //$NON-NLS-1$
         case Types.TIMESTAMP: return "TIMESTAMP"; //$NON-NLS-1$
         case Types.OTHER: return "VARCHAR"; //$NON-NLS-1$
      }
      throw new SemantikaRuntimeException("Failed to construct CAST (datatype: " + datatype + ")");
   }
}
