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
package com.obidea.semantika.mapping.base;

import java.util.Arrays;

import com.obidea.semantika.expression.base.AbstractAtom;
import com.obidea.semantika.expression.base.IAtomVisitor;
import com.obidea.semantika.expression.base.IFunction;
import com.obidea.semantika.expression.base.IPredicate;
import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.expression.base.IVariable;
import com.obidea.semantika.expression.base.Predicate;
import com.obidea.semantika.knowledgebase.TermSubstitutionBinding;

/**
 * A TripleAtom construction. The atom consists of a fix predicate called
 * "Triple" and three terms, namely <code>subject</code>, <code>predicate</code>
 * and <code>object</code>. This data structure follows the RDF data model for
 * making a statement about resources.
 */
public class TripleAtom extends AbstractAtom
{
   private static final long serialVersionUID = 629451L;

   public static final IPredicate TRIPLE_PREDICATE = new Predicate("Triple"); //$NON-NLS-1$

   /**
    * Constructs a triple atom with the specified <code>subject</code>,
    * <code>predicate</code> and <code>object</code>. The subject and
    * object denote the resource, and the predicate denotes the relationship
    * between the subject and the object.
    * 
    * @param subject
    *           the <code>subject</code> term.
    * @param predicate
    *           the <code>predicate</code> term.
    * @param object
    *           the <code>object</code> term.
    */
   public TripleAtom(ITerm subject, ITerm predicate, ITerm object)
   {
      super(TRIPLE_PREDICATE, Arrays.asList(new ITerm[] {subject, predicate, object}));
   }

   public static ITerm getSubject(AbstractAtom atom)
   {
      checkArity(atom);
      return atom.getTerm(0);
   }

   public static ITerm getPredicate(AbstractAtom atom)
   {
      checkArity(atom);
      return atom.getTerm(1);
   }

   public static ITerm getObject(AbstractAtom atom)
   {
      checkArity(atom);
      return atom.getTerm(2);
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final TripleAtom other = (TripleAtom) obj;
      return getPredicate().equals(other.getPredicate()) && getTerms().equals(other.getTerms());
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
      for (int i = 0; i < mTerms.size(); i++) {
         ITerm term = mTerms.get(i);
         if (term instanceof IFunction) {
            IFunction function = (IFunction) term;
            function.apply(binding);
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

   protected static void checkArity(AbstractAtom atom)
   {
      int arity = atom.getArity();
      if (arity != 3) {
         throw invalidInputAtomArityException(atom, arity);
      }
   }

   protected static IllegalArgumentException invalidInputAtomArityException(AbstractAtom atom, int arity)
   {
      String message = String.format("Input atom must have arity of three (Found: %s)", arity); //$NON-NLS-1$
      return new IllegalArgumentException(message);
   }
}
