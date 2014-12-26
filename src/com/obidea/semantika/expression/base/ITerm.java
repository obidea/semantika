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
package com.obidea.semantika.expression.base;

/**
 * Represent the term symbol in predicate logic. A first-order term is recursively
 * constructed from constant symbols, variables and function symbols.
 */
public interface ITerm extends IExpressionObject
{
   /**
    * Get the name or symbol of this term.
    * 
    * @return the string name.
    */
   public String getName();

   /**
    * Get the datatype of this term.
    * 
    * @return the datatype of this term.
    */
   public String getDatatype();

   /**
    * Determine if the term contains data typing definition.
    * 
    * @return true if it is a typed term
    */
   public boolean isTyped();

   /**
    * Get a hash code value of this term.
    * 
    * @return the hash code of this term.
    */
   public int hashCode();

   /**
    * Check equality of this term against any Java object. Note that for two
    * terms to be equal.
    * 
    * @param obj
    *           the object to check for equality.
    * @return true if term equals obj.
    */
   public boolean equals(Object obj);

   /**
    * Get a string representation of this term. Used for debugging purpose
    * only.
    * 
    * @return a string representation of this term.
    */
   public String toString();

   /**
    * Accept a visitor to collect the internal properties of this term.
    * 
    * @param visitor
    *           a visitor object.
    */
   public void accept(ITermVisitor visitor);
}
