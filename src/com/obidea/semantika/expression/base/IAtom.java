/*
 * Copyright (c) 2013-2015 Josef Hardi <josef.hardi@gmail.com>
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

import com.obidea.semantika.knowledgebase.TermSubstitutionBinding;

/**
 * A predicate atom that consists of predicate symbols with their arguments, each
 * argument being a term.
 */
public interface IAtom extends ISignature, IExpressionObject
{
   /**
    * Returns the atom predicate.
    * 
    * @return predicate of the atom.
    */
   IPredicate getPredicate();

   /**
    * Returns the atom terms.
    * 
    * @return a list of terms of the atom.
    */
   List<? extends ITerm> getTerms();

   /**
    * Returns the term according to its position.
    * 
    * @param position
    *          the term position, starting from 0, 1, 2, ...
    * @return the term on the given position.
    */
   ITerm getTerm(int position);

   /**
    * Returns the number of arguments that the atom takes.
    * 
    * @return the atom arity.
    */
   int getArity();

   /**
    * Checks if the atom is ground, i.e., the arguments are not variables but
    * instead all of them are constants.
    * 
    * @return true if all arguments are constants, or false otherwise.
    */
   boolean isGround();

   /**
    * Applies the substitution unifier <code>V/T</code> to this atom by replacing 
    * each free variable of <code>V</code> in the atom by term <code>T</code>.
    * 
    * @param binding
    *           the substitution unifier (i.e., the most general unifier).
    */
   void apply(TermSubstitutionBinding binding);

   /**
    * Accept a visitor to collect the internal content of this atom.
    * 
    * @param visitor
    *           a visitor object.
    */
   void accept(IAtomVisitor visitor);
}
