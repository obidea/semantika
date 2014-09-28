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
import java.util.Collections;
import java.util.List;

import com.obidea.semantika.knowledgebase.TermSubstitutionBinding;

public abstract class AbstractAtom implements IAtom
{
   private static final long serialVersionUID = 629451L;

   protected final IPredicate mPredicate;

   protected final List<ITerm> mTerms = new ArrayList<ITerm>();

   protected boolean mGround;

   public AbstractAtom(final IPredicate predicate, final List<? extends ITerm> terms)
   {
      if (predicate == null) {
         throw new RuntimeException("Predicate cannot be null"); //$NON-NLS-1$
      }
      mPredicate = predicate;
      mTerms.addAll(terms);
      
      mGround = true;
      for (ITerm t : terms) {
         if (t instanceof IVariable) {
            mGround = false;
            break;
         }
         if (t instanceof IFunction) {
            mGround = false;
            break;
         }
      }
   }

   @Override
   public IPredicate getPredicate()
   {
      return mPredicate;
   }

   @Override
   public List<? extends ITerm> getTerms()
   {
      return Collections.unmodifiableList(mTerms);
   }

   @Override
   public ITerm getTerm(int index)
   {
      return mTerms.get(index);
   }

   @Override
   public int getArity()
   {
      return mTerms.size();
   }

   @Override
   public boolean isGround()
   {
      return mGround;
   }

   @Override
   public void apply(TermSubstitutionBinding binding)
   {
      // NO-OP: To be implemented by subclasses
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + getPredicate().hashCode();
      result = prime * result + getTerms().hashCode();
      return result;
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
      if (obj instanceof AbstractAtom) {
         final AbstractAtom other = (AbstractAtom) obj;
         return getPredicate().equals(other.getPredicate()) && getTerms().equals(other.getTerms());
      }
      return false;
   }

   @Override
   public boolean isEquivalent(Object obj)
   {
      if (obj instanceof AbstractAtom) {
         AbstractAtom other = (AbstractAtom) obj;
         return getPredicate().equals(other.getPredicate()) && getArity() == other.getArity();
      }
      return false;
   }

   /**
    * Use only for debugging purposes.
    */
   @Override
   public String toString()
   {
      final StringBuilder sb = new StringBuilder();
      boolean needComma = false;
      for (ITerm term : mTerms) {
         if (needComma) {
            sb.append(", "); //$NON-NLS-1$
         }
         sb.append(TermUtils.toString(term));
         needComma = true;
      }
      return mPredicate + "(" + sb.toString() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
   }
}
