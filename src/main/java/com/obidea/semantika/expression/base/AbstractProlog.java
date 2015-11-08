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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.obidea.semantika.knowledgebase.TermSubstitutionBinding;

public abstract class AbstractProlog implements IProlog
{
   private static final long serialVersionUID = 629451L;

   protected IPredicate mHeadSymbol;

   protected List<ITerm> mDistTerm = new ArrayList<ITerm>();

   protected List<IAtom> mBody = new ArrayList<IAtom>();

   protected boolean mGround = false;

   public AbstractProlog(final Predicate headSymbol)
   {
      mHeadSymbol = headSymbol;
   }

   @Override
   public IAtom getHead()
   {
      return new Atom(getHeadSymbol(), getDistTerms());
   }

   @Override
   public IPredicate getHeadSymbol()
   {
      return mHeadSymbol;
   }

   @Override
   public void addDistTerm(ITerm term)
   {
      mDistTerm.add(term);
   }

   @Override
   public void addAtom(IAtom atom)
   {
      mBody.add(atom);
      mGround = mGround && atom.isGround();
   }

   @Override
   public void removeAtom(IAtom atom)
   {
      mBody.remove(atom);
      
      boolean ground = true;
      for (final IAtom a : getBody()) {
         ground = ground && a.isGround();
      }
      mGround = ground; // update if the clause is still ground
   }

   @Override
   public List<ITerm> getDistTerms()
   {
      return Collections.unmodifiableList(mDistTerm);
   }

   @Override
   public List<IAtom> getBody()
   {
      return Collections.unmodifiableList(mBody);
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
      result = prime * result + getHead().hashCode();
      result = prime * result + getBody().hashCode();
      return result;
   }

   /**
    * Internal use only for debugging
    */
   @Override
   public String toString()
   {
      final StringBuilder sb = new StringBuilder();
      
      sb.append("\nHEAD:"); //$NON-NLS-1$
      sb.append("\n   "); //$NON-NLS-1$
      sb.append(getHeadSymbol());
      sb.append("("); //$NON-NLS-1$
      boolean needComma = false;
      for (final ITerm term : getDistTerms()) {
         if (needComma) {
            sb.append(", "); //$NON-NLS-1$
         }
         sb.append(TermUtils.toString(term));
         needComma = true;
      }
      sb.append(")"); //$NON-NLS-1$
      sb.append("\n"); //$NON-NLS-1$
      
      sb.append("BODY:");
      if (getBody().size() > 0) {
         needComma = false;
         for (final IAtom atom : getBody()) {
            if (needComma) {
               sb.append(","); //$NON-NLS-1$
            }
            sb.append("\n   "); //$NON-NLS-1$
            sb.append(atom);
            needComma = true;
         }
      }
      sb.append("\n"); //$NON-NLS-1$
      return sb.toString();
   }
}
