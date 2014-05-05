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

public interface IForeignKey extends IDatabaseObject
{
   /**
    * Adds a referential constraint between two tables. The
    * <code>fkColumn</code> refers to the column in the referencing (or child)
    * table that has a reference to a <code>pkColumn</code> in the referenced
    * (or parent) table.
    * 
    * @param keySequence
    *           sequence number within foreign key (a value of 0 represents the
    *           first column of the foreign key, a value of 1 represent the
    *           second column within the foreign key).
    * @param pkColumn
    *           The referenced column
    * @param fkColumn
    *           The referencing column.
    */
   void addReference(int keySequence, IColumn pkColumn, IColumn fkColumn);

   /**
    * Returns the column references defined by this foreign key.
    * 
    * @return The referential description of this key.
    */
   List<? extends IColumnReference> getReferences();
}
