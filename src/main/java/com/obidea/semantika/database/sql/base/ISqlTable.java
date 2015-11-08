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
package com.obidea.semantika.database.sql.base;

import java.util.List;

public interface ISqlTable extends ISqlExpression
{
   /**
    * Gets the table's name in SQL string.
    *
    * @return table's name or "" if not applicable.
    */
   String getTableName();

   /**
    * Sets an alias name to this SQL select item
    *
    * @param aliasName
    *           an alias name.
    */
   void setAliasName(String alias);

   /**
    * Gets an alias name from this SQL select item.
    *
    * @return an alias name or "" if not applicable.
    */
   String getAliasName();

   /**
    * Gets the table's full name in string fragments
    *
    * @return name fragments
    */
   String[] getNameFragments();

   /**
    * Checks if this SQL select item has been assigned an alias name.
    *
    * @return Returns <code>true</code> if it does, or <code>false
    * </code> otherwise.
    */
   boolean hasAliasName();

   /**
    * Gets the table's columns.
    *
    * @return a list of table's columns.
    */
   List<? extends ISqlColumn> getColumns();
}
