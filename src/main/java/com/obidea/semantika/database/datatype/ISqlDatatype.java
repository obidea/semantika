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


public interface ISqlDatatype<T>
{
   /**
    * Get the datatype identifier.
    * 
    * @return the URI identifier of this datatype
    */
   public String getName();

   /**
    * Get the Java object representation of the given a lexical form of a string.
    * 
    * @param lexicalForm
    *           a lexical form of a string.
    * @return the Java object representation of the given a lexical form of a string.
    */
   public T getValue(String lexicalForm);

   /**
    * Get the JDBC type constant (See {@link java.sql.Types})
    * 
    * @return
    *       the SQL type constant value.
    */
   public int getType();
}
