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

public interface IPrimaryKey extends IDatabaseObject
{
   /**
    * Returns the name of the table that contains this primary key object.
    * 
    * @return The table object of this primary key, or <code>null</code> if the
    *         table is undefined.
    */
   String getSourceTable();

   /**
    * Adds a column which forms this primary key. The key may consist of 2 or
    * more column that will create a compound key.
    * 
    * @param keySequence
    *           number within primary key (a value of 0 represents the first
    *           column of the primary key, a value of 1 represent the second
    *           column within the primary key).
    * @param pkColumn
    *           The attribute which forms this PK.
    */
   void addKey(int keySequence, IColumn pkColumn);

   /**
    * Returns the primary key components.
    * 
    * @return A list of columns that forms the PK.
    */
   List<? extends IColumn> getKeys();

   /**
    * Checks whether the key consists of 2 or more attributes.
    * 
    * @return Returns <code>true</code> if this PK consists of 2 or more
    *         columns.
    */
   boolean isCompound();
}
