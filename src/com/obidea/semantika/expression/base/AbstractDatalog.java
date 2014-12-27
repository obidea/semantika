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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.obidea.semantika.knowledgebase.TermSubstitutionBinding;

public abstract class AbstractDatalog implements IDatalog
{
   private static final long serialVersionUID = 629451L;

   protected IPredicate mHeadSymbol;

   protected List<IAtom> mBody = new ArrayList<IAtom>();

   protected List<IVariable> mAllVars = new ArrayList<IVariable>();

   protected List<IVariable> mDistVars = new ArrayList<IVariable>();

   protected List<IConstant> mConstants = new ArrayList<IConstant>();

   protected boolean mGround = false;

   public AbstractDatalog(final IPredicate headSymbol)
   {
      mHeadSymbol = headSymbol;
   }

   @Override
   public IAtom getHead()
   {
      return new Atom(getHeadSymbol(), getDistVars());
   }

   @Override
   public IPredicate getHeadSymbol()
   {
      return mHeadSymbol;
   }

   @Override
   public void addAtom(IAtom atom)
   {
      mBody.add(atom);
      
      for (final ITerm term : atom.getTerms()) {
         if (term instanceof IVariable) {
            IVariable var = (IVariable) term;
            mAllVars.add(var);
         }
         else if (term instanceof IConstant) {
            IConstant cons = (IConstant) term;
            mConstants.add(cons);
         }
      }
      mGround = mGround && atom.isGround();
   }

   @Override
   public void removeAtom(IAtom atom)
   {
      if (!mBody.contains(atom)) {
         return;
      }
      mBody.remove(atom);
      
      final Set<ITerm> remainingTerms = new HashSet<ITerm>();
      
      boolean ground = true;
      for (final IAtom a : getBody()) {
         ground = ground && a.isGround();
         remainingTerms.addAll(a.getTerms());
      }
      mGround = ground; // update if the clause is still ground
      
      final Set<ITerm> removeSet = new HashSet<ITerm>(atom.getTerms());
      removeSet.removeAll(remainingTerms); // keep the terms in the remaining set
      
      for (final ITerm term : removeSet) {
         mAllVars.remove(term);
         mDistVars.remove(term);
         mConstants.remove(term);
      }
   }

   @Override
   public List<IAtom> getBody()
   {
      return Collections.unmodifiableList(mBody);
   }

   @Override
   public void addDistVar(IVariable var)
   {
      mDistVars.add(var);
   }
   
   @Override
   public List<IVariable> getVars()
   {
      return Collections.unmodifiableList(mAllVars);
   }

   @Override
   public List<IVariable> getDistVars()
   {
      return Collections.unmodifiableList(mDistVars);
   }

   @Override
   public List<IVariable> getUndistVars()
   {
      final List<IVariable> undistVars = new ArrayList<IVariable>(mAllVars);
      undistVars.removeAll(getDistVars());
      return Collections.unmodifiableList(undistVars);
   }

   @Override
   public List<IConstant> getConstants()
   {
      return Collections.unmodifiableList(mConstants);
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
      
      sb.append("HEAD:"); //$NON-NLS-1$
      sb.append("\n   "); //$NON-NLS-1$
      sb.append(getHeadSymbol());
      sb.append("("); //$NON-NLS-1$
      boolean needComma = false;
      for (final IVariable var : mDistVars) {
         if (needComma) {
            sb.append(", "); //$NON-NLS-1$
         }
         sb.append(TermUtils.toString(var));
         needComma = true;
      }
      sb.append(")"); //$NON-NLS-1$
      sb.append("\n"); //$NON-NLS-1$
      
      sb.append("BODY:");
      if (mBody.size() > 0) {
         needComma = false;
         for (final IAtom atom : mBody) {
            if (needComma) {
               sb.append(","); //$NON-NLS-1$
            }
            sb.append("\n   "); //$NON-NLS-1$
            sb.append(atom);
            needComma = true;
         }
      }
      return sb.toString();
   }
}
