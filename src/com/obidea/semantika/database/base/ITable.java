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
package com.obidea.semantika.database.base;

import java.util.List;

public interface ITable extends IDatabaseObject
{
   /**
    * Returns the schema object associated to this table.
    *
    * @return the <code>ISchema</code> object.
    */
   ISchema getSchemaOrigin();

   /**
    * Returns the schema name of this table.
    */
   String getSchemaName();

   /**
    * Returns a list of columns in ordinal order.
    */
   List<? extends IColumn> getColumns();

   /**
    * Adds a column object to this table.
    */
   void addColumn(IColumn column);

   /**
    * Returns a column object given its name.
    */
   IColumn getColumn(String name);

   /**
    * Adds primary key object to this table.
    */
   void setPrimaryKey(IPrimaryKey primaryKey);

   /**
    * Returns the primary key of this table.
    */
   IPrimaryKey getPrimaryKey();

   /**
    * Adds foreign key object to this table;
    */
   void setForeignKey(IForeignKey foreignKey);

   /**
    * Returns a list of foreign keys of this table.
    */
   List<? extends IForeignKey> getForeignKeys();

   /**
    * Returns a foreign key object given its name.
    */
   IForeignKey getForeignKey(String name);
}
