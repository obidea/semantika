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
package com.obidea.semantika.expression.base;

import java.util.Set;

/**
 * Represents the construction of a query language.
 */
public interface IQueryExt extends IProlog
{
   /**
    * Sets <code>true</code> if the query should return distinct results, or
    * <code>false</code> otherwise.
    */
   void setDistinct(boolean isDistinct);

   /**
    * Check if the query requires distinct results.
    * 
    * @return Returns <code>true</code> if the query requires distinct results, or
    *         <code>false</code> otherwise.
    */
   public boolean isDistinct();

   /**
    * Set a filter function that applies to this query object.
    *
    * @param filter
    *           a filter function.
    */
   void setFilter(IFunction filter);

   /**
    * Get the filter functions.
    * 
    * @return A set of filter functions.
    */
   public Set<? extends IFunction> getFilters();

   /**
    * Check if the query has filter functions.
    * 
    * @return Returns <code>true</code> if the query has filter functions, or
    *         <code>false</code> otherwise.
    */
   public boolean hasFilter();

   /**
    * Accept a visitor to collect the internal content of this class.
    * 
    * @param visitor
    *           a visitor object.
    */
   public void accept(IQueryExtVisitor visitor);
}
