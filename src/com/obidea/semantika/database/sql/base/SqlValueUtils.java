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
package com.obidea.semantika.database.sql.base;

import com.obidea.semantika.database.datatype.AbstractSqlType;
import com.obidea.semantika.database.datatype.SqlDataTypeProfile;
import com.obidea.semantika.datatype.exception.InvalidLexicalFormException;
import com.obidea.semantika.datatype.exception.UnsupportedDataTypeException;

public class SqlValueUtils
{
   /**
    * Get the data type given a SQL literal.
    * 
    * @param literal
    *           the <code>literal</code> object.
    * @return the <code>Datatype</code> if the data type in the
    *         <code>literal</code> is recognized, or <code>null</code> otherwise
    * @throws UnsupportedDataTypeException 
    */
   public static AbstractSqlType<?> getDatatype(ISqlValue literal) throws UnsupportedDataTypeException
   {
      String datatypeUri = literal.getDatatype();
      return SqlDataTypeProfile.getSqlDatatype(datatypeUri);
   }

   /**
    * Get the Java object representation of the <code>literal</code> object.
    * E.g., if the literal object represents an integer "2" from a table cell
    * then the returned object is a <code>java.lang.Integer</code>.
    * 
    * @param literal
    *           the <code>literal</code> object.
    * @return Java object representation of <code>literal</code>
    * @throws UnsupportedDataTypeException 
    * @throws InvalidSqlLiteralException 
    */
   public static Object getValue(ISqlValue literal) throws UnsupportedDataTypeException, InvalidLexicalFormException
   {
      AbstractSqlType<?> dt = getDatatype(literal);
      String lexicalForm = literal.getValue();
      return dt.getValue(lexicalForm);
   }
}
