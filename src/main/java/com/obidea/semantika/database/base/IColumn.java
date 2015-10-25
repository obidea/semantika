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
package com.obidea.semantika.database.base;

public interface IColumn extends IDatabaseObject
{
   /**
    * Returns the table object associated to this column.
    *
    * @return the <code>ITable</code> object.
    */
   ITable getTableOrigin();

   /**
    * Returns the schema name associated to this column.
    * 
    * @return the schema name.
    */
   String getSchemaName();

   /**
    * Returns the table name associated to this column.
    * 
    * @return the table name.
    */
   String getTableName();

   /**
    * Returns the datatype of this column. The return value follows the
    * constants in <code>java.sql.Types</code>.
    * 
    * @return the JDBC type of this column, or 0 if the type is undefined.
    */
   int getSqlType();

   /**
    * Sets <code>true</code> if this column is part of the primary key.
    */
   void setPrimaryKey(boolean isPrimaryKey);

   /**
    * Checks if this column is the primary key.
    * 
    * @return Returns <code>true</code> if this column is part of the primary
    *         key, or <code>false</code> otherwise.
    */
   boolean isPrimaryKey();
}
