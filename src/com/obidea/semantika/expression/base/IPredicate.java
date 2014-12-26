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
 * Represents the predicate symbol in predicate logic.
 */
public interface IPredicate extends IExpressionObject
{
   /**
    * Returns the symbol given for this predicate.
    * 
    * @return a predicate symbol (or its name).
    */
   String getName();

   /**
    * Accept a visitor to process the internal properties of this predicate.
    * 
    * @param visitor
    *           a visitor object.
    */
   void accept(IAtomVisitor visitor);
}
