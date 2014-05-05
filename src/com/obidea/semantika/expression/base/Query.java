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

import java.util.Collections;
import java.util.Set;

import com.obidea.semantika.knowledgebase.TermSubstitutionBinding;

public class Query extends AbstractDatalog implements IQuery
{
   private static final long serialVersionUID = 629451L;

   private boolean mDistinct = false;

   public Query()
   {
      super(BuiltInPredicate.Answer);
   }

   public Query(boolean isDistinct)
   {
      super(BuiltInPredicate.Answer);
      setDistinct(isDistinct);
   }

   @Override
   public void setDistinct(boolean isDistinct)
   {
      mDistinct = isDistinct;
   }

   @Override
   public boolean isDistinct()
   {
      return mDistinct;
   }

   @Override
   public void setFilter(IFunction filter)
   {
      mConstraints.add(filter);
   }

   @Override
   public Set<IFunction> getFilters()
   {
      return Collections.unmodifiableSet(getConstraints());
   }

   @Override
   public boolean hasFilter()
   {
      return (getConstraints().size() > 0) ? true : false;
   }

   @Override
   public void apply(TermSubstitutionBinding binding)
   {
      mAllVars.removeAll(binding.getVariables());
      
      /*
       * Apply the substitution to the distinct variables and all variables
       */
      for (int i = 0; i < mDistVars.size(); i++) {
         IVariable var = mDistVars.get(i);
         if (binding.isBound(var)) {
            ITerm substitutedTerm = binding.replace(var);
            if (substitutedTerm instanceof IVariable) {
               mDistVars.set(i, (IVariable) substitutedTerm);
               mAllVars.add((IVariable) substitutedTerm);
            }
            else if (substitutedTerm instanceof IConstant) {
               mDistVars.remove(i);
               mConstants.add((IConstant) substitutedTerm);
            }
         }
      }
      /*
       * Apply the substitution to atoms in the body
       */
      for (final IAtom atom : mBody) {
         atom.apply(binding);
      }
      /*
       * Apply the substitution to functions in the filter list
       */
      for (final IFunction filter : getFilters()) {
         filter.apply(binding);
      }
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
      final Query other = (Query) obj;
      return getHead().equals(other.getHead())
            && getBody().equals(other.getBody())
            && getFilters().equals(other.getFilters());
   }

   @Override
   public void accept(IQueryVisitor visitor)
   {
      visitor.visit(this);
   }
}