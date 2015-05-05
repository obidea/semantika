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
package com.obidea.semantika.database.datatype;

import java.util.HashMap;
import java.util.Map;

import com.obidea.semantika.datatype.DataType;
import com.obidea.semantika.datatype.exception.UnsupportedDataTypeException;

public final class SqlDataTypeProfile
{
   private static final Map<String, AbstractSqlType<?>> coreDatatypes;
   
   static {
      coreDatatypes = new HashMap<String, AbstractSqlType<?>>();
      {
         coreDatatypes.put(DataType.PLAIN_LITERAL, SqlVarChar.getInstance());
         coreDatatypes.put(DataType.STRING, SqlVarChar.getInstance());
         
      // TODO: Implement later
      // coreDatatypes.put(DataType.NORMALIZED_STRING, ...);
      // coreDatatypes.put(DataType.TOKEN, ...);
      // coreDatatypes.put(DataType.LANGUAGE, ...);
      // coreDatatypes.put(DataType.NMToken, ...);
      // coreDatatypes.put(DataType.NAME, ...);
      // coreDatatypes.put(DataType.NCName, ...);
         
         coreDatatypes.put(DataType.BOOLEAN, SqlBoolean.getInstance());
         
         coreDatatypes.put(DataType.DECIMAL, SqlDecimal.getInstance());
         coreDatatypes.put(DataType.INTEGER, SqlInteger.getInstance());
         coreDatatypes.put(DataType.LONG, SqlBigInt.getInstance());
         coreDatatypes.put(DataType.INT, SqlInteger.getInstance());
         coreDatatypes.put(DataType.SHORT, SqlSmallInt.getInstance());
         coreDatatypes.put(DataType.BYTE, SqlTinyInt.getInstance());
         
      // TODO: Implement later
      // coreDatatypes.put(DataType.NON_NEGATIVE_INTEGER, ...);
      // coreDatatypes.put(DataType.NON_POSITIVE_INTEGER, ...);
      // coreDatatypes.put(DataType.NEGATIVE_INTEGER, ...);
      // coreDatatypes.put(DataType.POSITIVE_INTEGER, ...);
      // coreDatatypes.put(DataType.UNSIGNED_LONG, ...);
      // coreDatatypes.put(DataType.UNSIGNED_INT, ...);
      // coreDatatypes.put(DataType.UNSIGNED_SHORT, ...);
      // coreDatatypes.put(DataType.UNSIGNED_BYTE, ...);
         
         coreDatatypes.put(DataType.DOUBLE, SqlDouble.getInstance());
         
         coreDatatypes.put(DataType.FLOAT, SqlFloat.getInstance());
         
         coreDatatypes.put(DataType.DATE_TIME, SqlTimestamp.getInstance());
      // coreDatatypes.put(DataType.DATE_TIME_STAMP, ...);
         
         coreDatatypes.put(DataType.DATE, SqlDate.getInstance());
      // coreDatatypes.put(DataType.G_YEAR_MONTH, ...);
      // coreDatatypes.put(DataType.G_MONTH_DAY, ...);
      // coreDatatypes.put(DataType.G_YEAR, ...);
      // coreDatatypes.put(DataType.G_MONTH, ...);
      // coreDatatypes.put(DataType.G_DAY, ...);
         coreDatatypes.put(DataType.TIME, SqlTime.getInstance());
         
      // coreDatatypes.put(DataType.DURATION, ...);
         
      // coreDatatypes.put(DataType.ANY_URI, ...);
      }
   }

   public static AbstractSqlType<?> getSqlDatatype(String datatypeUri)
   {
      AbstractSqlType<?> dt = coreDatatypes.get(datatypeUri);
      if (dt == null) {
         throw new UnsupportedDataTypeException(datatypeUri);
      }
      return dt;
   }
}
