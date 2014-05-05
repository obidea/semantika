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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Syntactic sugar is a special atom with an empty predicate and empty terms. It is used to make
 * easy query writing on complex expression, such as join sub-queries and unions.
 */
public abstract class SyntacticSugar extends AbstractAtom
{
   private static final long serialVersionUID = 629451L;

   protected SyntacticSugar()
   {
      super(new Predicate(""), new ArrayList<Term>()); //$NON-NLS-1$
   }

   public abstract String getName();

   @Override
   public final Predicate getPredicate()
   {
      throw new UnsupportedOperationException("Syntactic sugar has no predicate"); //$NON-NLS-1$
   }

   @Override
   public final List<Term> getTerms()
   {
      throw new UnsupportedOperationException("Syntactic sugar has no terms"); //$NON-NLS-1$
   }

   @Override
   public final int getArity()
   {
      throw new UnsupportedOperationException("Syntactic sugar has no arity"); //$NON-NLS-1$
   }

   @Override
   public final boolean isGround()
   {
      throw new UnsupportedOperationException("Syntactic sugar cannot be determined ground or not"); //$NON-NLS-1$
   }

   @Override
   public Set<AbstractFunction> getConstraints()
   {
      throw new UnsupportedOperationException("Syntactic sugar cannot be determined ground or not"); //$NON-NLS-1$
   }

   /**
    * Accept a visitor to collect the internal content of this atom.
    * 
    * @param visitor
    *           a visitor object.
    */
   @Override
   public void accept(IAtomVisitor visitor)
   {
      visitor.visit(this);
   }
}
