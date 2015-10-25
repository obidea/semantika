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

import java.util.Arrays;
import java.util.List;

import com.obidea.semantika.knowledgebase.TermSubstitutionBinding;

public class Atom extends AbstractAtom
{
   private static final long serialVersionUID = 629451L;

   public Atom(final IPredicate predicate, final ITerm... terms)
   {
      super(predicate, Arrays.asList(terms));
   }

   public Atom(final IPredicate predicate, final List<? extends ITerm> terms)
   {
      super(predicate, terms);
   }

   @Override
   public void apply(TermSubstitutionBinding binding)
   {
      if (binding.isEmpty()) {
         return;
      }
      if (isGround()) {
         return;
      }
      /*
       * Apply the substitution to atom's terms
       */
      for (int i = 0; i < mTerms.size(); i++) {
         ITerm term = mTerms.get(i);
         if (term instanceof IFunction) {
            IFunction function = (IFunction) term;
            function.apply(binding); // recursively unifies function
         }
         else if (term instanceof IVariable) {
            IVariable var = (IVariable) term;
            if (binding.isBound(var)) {
               mTerms.set(i, binding.replace(var));
            }
         }
      }
   }

   @Override
   public void accept(IAtomVisitor visitor)
   {
      visitor.visit(this);
   }
}
