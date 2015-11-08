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

import com.obidea.semantika.knowledgebase.TermSubstitutionBinding;

/**
 * Represents the construction of Prolog query object as in declarative
 * logic language. Prolog is a general purpose logic programming with variables,
 * predicates, constants and function symbols as the terms.
 */
public interface IProlog extends IClause
{
   /**
    * Adds a term to this prolog object.
    *
    * @param term
    *          The term to add
    */
   void addDistTerm(ITerm term);

   /**
    * Return all the distinguished terms used in the prolog clause.
    * 
    * @return A set of terms
    */
   List<? extends ITerm> getDistTerms();

   /**
    * Add an atom to the prolog body.
    * 
    * @param atom
    *           the atom to add.
    */
   void addAtom(IAtom atom);

   /**
    * Remove an atom from the prolog body.
    * 
    * @param atom
    *           the atom to remove.
    */
   void removeAtom(IAtom atom);

   /**
    * Applies the substitution unifier <code>V/T</code> to this prolog object by
    * replacing each free variable of <code>V</code> in the atom by term <code>T</code>.
    * 
    * @param binding
    *           the substitution unifier (i.e., the most general unifier).
    */
   void apply(TermSubstitutionBinding binding);
}
