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

import java.util.List;
import java.util.Set;

public interface ISqlQuery extends ISqlObject
{
   /**
    * Sets <code>true</code> if the query should return distinct results, or
    * <code>false</code> otherwise.
    */
   void setDistinct(boolean isDistinct);

   /**
    * Checks if the query requires distinct results.
    * 
    * @return returns <code>true</code> if the query requires distinct results,
    *         or <code>false</code> otherwise.
    */
   boolean isDistinct();

   /**
    * Adds select item to this query object.
    */
   void addSelectItem(SqlSelectItem selectItem);

   /**
    * Returns select item expressions that are in the SELECT clause.
    */
   List<SqlSelectItem> getSelectItems();

   /**
    * Adds from expression to this query object.
    */
   void setFromExpression(ISqlExpression expression);

   /**
    * Returns table expressions that are in the FROM clause.
    */
   ISqlExpression getFromExpression();

   /**
    * Adds where expressions to this query object.
    */
   void addWhereExpression(ISqlExpression expression);

   /**
    * Returns filter expressions that are in the WHERE clause.
    */
   Set<ISqlExpression> getWhereExpression();

   /**
    * Check if the query contains where expression for filtering.
    * 
    * @return returns <code>true</code> if the query contains filters,
    *         or <code>false</code> otherwise.
    */
   boolean hasWhereExpression();
}
