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

import java.util.List;

/**
 * Represents the construction of Horn clause for logic programming.
 * The object is commonly written in the form of an implication:
 * <pre>
 * u :- p, q, ..., t </pre>
 * such that atom <code>u</code> is known as the head (or goal) and the
 * remaining atoms <code>p</code>, <code>q</code> and <code>t</code>
 * collectively are called the body.
 */
public interface IClause extends IExpressionObject
{
   /**
    * Gets the head predicate of the clause
    *
    * @return head predicate
    */
   public IPredicate getHeadSymbol();

   /**
    * Gets the head atom of the clause.
    * 
    * @return head atom
    */
   public IAtom getHead();

   /**
    * Gets the body of the clause
    * 
    * @return a list of atoms that constructs the clause's body.
    */
   public List<IAtom> getBody();

   /**
    * Checks whether the clause is ground.
    * 
    * @return true iff the query is ground
    */
   public boolean isGround();
}
