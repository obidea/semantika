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

import java.util.List;

import com.obidea.semantika.knowledgebase.TermSubstitutionBinding;

public interface IDatalog extends IClause
{
   /**
    * Adds a distinguished variable to this datalog.
    *
    * @param var
    *          The distinguished variable.
    */
   void addDistVar(IVariable var);

   /**
    * Return all the distinguished variables used in the clause.
    * 
    * @return A set of variables
    */
   List<? extends IVariable> getDistVars();

   /**
    * Return all undistinguished variables appeared in the clause.
    * 
    * @return A set of variables
    */
   List<? extends IVariable> getUndistVars();

   /**
    * Return all the variables used in the clause.
    * 
    * @return A set of variables
    */
   List<? extends IVariable> getVars();

   /**
    * Return constant literals used in the clause.
    * 
    * @return A set of constants
    */
   List<? extends IConstant> getConstants();

   /**
    * Add an atom to the datalog body.
    * 
    * @param atom
    *           the atom to add.
    */
   void addAtom(IAtom atom);

   /**
    * Remove an atom from the datalog body.
    * 
    * @param atom
    *           the atom to remove.
    */
   void removeAtom(IAtom atom);

   /**
    * Applies the substitution unifier <code>V/T</code> to this datalog object by
    * replacing each free variable of <code>V</code> in the atom by term <code>T</code>.
    * 
    * @param binding
    *           the substitution unifier (i.e., the most general unifier).
    */
   void apply(TermSubstitutionBinding binding);
}
