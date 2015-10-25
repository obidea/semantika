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

import java.sql.Types;
import java.util.HashMap;

import com.obidea.semantika.datatype.DataType;

/**
 * A mapping specification from SQL datatypes to XML Schema datatypes.
 * Source: {@link http://www.w3.org/2001/sw/rdb2rdf/wiki/Mapping_SQL_datatypes_to_XML_Schema_datatypes}
 */
public final class SqlTypeToXmlType
{
   private static HashMap<Integer, String> mTypeMapping;
   
   static {
      mTypeMapping = new HashMap<Integer, String>();
      
   // mCoreMapping.put(Types.BINARY, DataType.HEX_BINARY);
   // mCoreMapping.put(Types.JAVA_OBJECT, DataType.HEX_BINARY);
      mTypeMapping.put(Types.NUMERIC, DataType.DECIMAL);
      mTypeMapping.put(Types.DECIMAL, DataType.DECIMAL);
      mTypeMapping.put(Types.BIGINT, DataType.LONG);
      mTypeMapping.put(Types.INTEGER, DataType.INTEGER);
      mTypeMapping.put(Types.SMALLINT, DataType.SHORT);
      mTypeMapping.put(Types.TINYINT, DataType.BYTE);
      mTypeMapping.put(Types.REAL, DataType.FLOAT);
      mTypeMapping.put(Types.FLOAT, DataType.FLOAT);
      mTypeMapping.put(Types.DOUBLE, DataType.DOUBLE);
      mTypeMapping.put(Types.CHAR, DataType.STRING);
      mTypeMapping.put(Types.VARCHAR, DataType.STRING);
      mTypeMapping.put(Types.NCHAR, DataType.STRING);
      mTypeMapping.put(Types.NVARCHAR, DataType.STRING);
      mTypeMapping.put(Types.LONGVARCHAR, DataType.STRING);
      mTypeMapping.put(Types.LONGNVARCHAR, DataType.STRING);
      mTypeMapping.put(Types.DATE, DataType.DATE);
      mTypeMapping.put(Types.TIME, DataType.TIME);
      mTypeMapping.put(Types.TIMESTAMP, DataType.DATE_TIME);
      mTypeMapping.put(Types.BOOLEAN, DataType.BOOLEAN);
      mTypeMapping.put(Types.BIT, DataType.BOOLEAN);
      mTypeMapping.put(Types.OTHER, DataType.STRING);
   }

   /**
    * Return the corresponding XML type given the SQL type.
    * 
    * @param sqlType
    *           The JDBC SQL type (see {@link java.sql.Types}).
    * @return a URI string representing the XML type.
    * @throws UnsupportedSqlDataTypeException
    *            if the data type has no corresponding XML type.
    */
   public static String get(int sqlType)
   {
      String toReturn = mTypeMapping.get(sqlType);
      if (toReturn == null) {
         throw new UnsupportedSqlDataTypeException(sqlType);
      }
      return toReturn;
   }
}
