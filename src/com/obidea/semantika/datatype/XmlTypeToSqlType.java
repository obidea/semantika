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
package com.obidea.semantika.datatype;

import java.sql.Types;
import java.util.HashMap;

import com.obidea.semantika.datatype.exception.UnsupportedDataTypeException;

public final class XmlTypeToSqlType
{
   private static HashMap<String, Integer> mTypeMapping;
   
   static {
      mTypeMapping = new HashMap<String, Integer>();
      mTypeMapping.put(DataType.STRING, Types.VARCHAR);
      mTypeMapping.put(DataType.BOOLEAN, Types.BOOLEAN);
      mTypeMapping.put(DataType.DECIMAL, Types.DECIMAL);
      mTypeMapping.put(DataType.LONG, Types.BIGINT);
      mTypeMapping.put(DataType.INTEGER, Types.INTEGER);
      mTypeMapping.put(DataType.INT, Types.INTEGER);
      mTypeMapping.put(DataType.SHORT, Types.SMALLINT);
      mTypeMapping.put(DataType.BYTE, Types.TINYINT);
      mTypeMapping.put(DataType.NON_NEGATIVE_INTEGER, Types.BIGINT);
      mTypeMapping.put(DataType.NON_POSITIVE_INTEGER, Types.BIGINT);
      mTypeMapping.put(DataType.NEGATIVE_INTEGER, Types.BIGINT);
      mTypeMapping.put(DataType.POSITIVE_INTEGER, Types.BIGINT);
      mTypeMapping.put(DataType.UNSIGNED_LONG, Types.BIGINT);
      mTypeMapping.put(DataType.UNSIGNED_INT, Types.BIGINT);
      mTypeMapping.put(DataType.UNSIGNED_SHORT, Types.INTEGER);
      mTypeMapping.put(DataType.UNSIGNED_BYTE, Types.SMALLINT);
      mTypeMapping.put(DataType.DOUBLE, Types.DOUBLE);
      mTypeMapping.put(DataType.FLOAT, Types.FLOAT);
      mTypeMapping.put(DataType.DATE_TIME, Types.TIMESTAMP);
      mTypeMapping.put(DataType.DATE_TIME_STAMP, Types.TIMESTAMP);
      mTypeMapping.put(DataType.DATE, Types.DATE);
      mTypeMapping.put(DataType.TIME, Types.TIME);
      mTypeMapping.put(DataType.G_YEAR_MONTH, Types.VARCHAR);
      mTypeMapping.put(DataType.G_MONTH_DAY, Types.VARCHAR);
      mTypeMapping.put(DataType.G_YEAR, Types.VARCHAR);
      mTypeMapping.put(DataType.G_MONTH,Types.VARCHAR);
      mTypeMapping.put(DataType.G_DAY, Types.VARCHAR);
   }

   /**
    * Return the corresponding SQL type given the XML type.
    * 
    * @param xmlType
    *           The XML XSD Types {@link http://www.w3.org/TR/xmlschema-2/}
    * @return an integer representing the SQL type.
    * @throws UnsupportedDataTypeException
    *            if the data type has no corresponding SQL type.
    */
   public static int get(String xmlType)
   {
      Integer toReturn = mTypeMapping.get(xmlType);
      if (toReturn == null) {
         throw new UnsupportedDataTypeException(xmlType);
      }
      return toReturn;
   }
}
