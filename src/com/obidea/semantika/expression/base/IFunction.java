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

public interface IFunction extends ITerm, ISignature
{
   /**
    * Returns the function parameters.
    * 
    * @return a list of parameters of the function.
    */
   List<? extends ITerm> getParameters();

   /**
    * Returns the function parameter according to its position.
    * 
    * @param position
    *           The parameter position, starting from 0, 1, 2, ...
    * @return the function parameter on the given position
    */
   ITerm getParameter(int position);

   /**
    * Returns the parameter count in this function.
    * 
    * @return the arity number.
    */
   int getArity();

   /**
    * Applies the substitution unifier <code>V/T</code> to this function by replacing
    * each free variable of <code>V</code> in the atom by term <code>T</code>.
    * 
    * @param binding
    *           the substitution unifier (i.e., the most general unifier).
    */
   void apply(TermSubstitutionBinding binding);

   /**
    * Executes this function using the given <code>arguments</code> and returns result.
    * 
    * @param arguments
    *           A list of arguments for this function.
    * @return the execution result.
    */
   IConstant execute(List<? extends IConstant> arguments);
}
