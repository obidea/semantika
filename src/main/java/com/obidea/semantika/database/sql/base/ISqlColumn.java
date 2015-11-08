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

public interface ISqlColumn extends ISqlExpression
{
   /**
    * Gets the column's table name in SQL string.
    *
    * @return column's table name or "" if not applicable.
    */
   String getTableOrigin();

   /**
    * Sets the column's view name in SQL string.
    * 
    * @param viewName
    *           Local view name in SQL string.
    */
   void setViewName(String viewName);

   /**
    * Gets the column's view name in SQL string.
    *
    * @return column's view name or "" if not applicable.
    */
   String getViewName();

   /**
    * Checks if the column has been assigned a view name.
    * 
    * @return Returns <code>true</code> if it does, or <code>false
    * </code> otherwise.
    */
   boolean hasViewName();

   /**
    * Gets the column's name in SQL string.
    *
    * @return column's name or "" if not applicable.
    */
   String getColumnName();

   /**
    * Gets the column's full name in string fragments
    *
    * @return column's name fragments.
    */
   String[] getNameFragments();

   /**
    * Gets the column's type in Java SQL Type.
    * {@link http://docs.oracle.com/javase/7/docs/api/java/sql/Types.html}
    *
    * @return column's type or "" if not applicable.
    */
   int getColumnType();
}
