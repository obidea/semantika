/*
 * Copyright (c) 2013-2015 Obidea Technology
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

import java.io.Serializable;

public interface IColumnReference extends Serializable
{
   /**
    * Gets the foreign key column / referencing column.
    * 
    * @return foreign key column
    */
   IColumn getForeignKeyColumn();

   /**
    * Gets the primary key column / referenced column.
    * 
    * @return primary key column
    */
   IColumn getPrimaryKeyColumn();
}
