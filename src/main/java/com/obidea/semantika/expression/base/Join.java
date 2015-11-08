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

import com.obidea.semantika.knowledgebase.TermSubstitutionBinding;

public class Join extends SyntacticSugar
{
   private static final long serialVersionUID = 629451L;

   private boolean mIsInnerJoin = false;
   private boolean mIsLeftJoin = false;

   private IAtom mLeftExpression;
   private IAtom mRightExpression;

   private IFunction mFilter;

   public Join()
   {
      super();
   }

   @Override
   public String getName()
   {
      if (isInnerJoin()) {
         return "JOIN"; //$NON-NLS-1$
      }
      else if (isLeftJoin()) {
         return "LEFTJOIN"; //$NON-NLS-1$
      }
      else {
         return "<undefined>"; //$NON-NLS-1$
      }
   }

   public void setInnerJoin(boolean value)
   {
      mIsInnerJoin = value;
   }

   public boolean isInnerJoin()
   {
      return mIsInnerJoin;
   }

   public void setLeftJoin(boolean value)
   {
      mIsLeftJoin = value;
   }

   public boolean isLeftJoin()
   {
      return mIsLeftJoin;
   }

   public void setLeftExpression(IAtom atom)
   {
      mLeftExpression = atom;
   }

   public IAtom getLeftExpression()
   {
      return mLeftExpression;
   }

   public void setRightExpression(IAtom expr)
   {
      mRightExpression = expr;
   }

   public IAtom getRightExpression()
   {
      return mRightExpression;
   }

   public void setFilter(IFunction filter)
   {
      mFilter = filter;
   }

   public IFunction getFilter()
   {
      return mFilter;
   }

   public boolean hasFilter()
   {
      return (mFilter == null) ? false : true;
   }

   @Override
   public void apply(TermSubstitutionBinding binding)
   {
      mLeftExpression.apply(binding);
      mRightExpression.apply(binding);
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
      final Join other = (Join) obj;
      return getLeftExpression().equals(other.getLeftExpression())
            && getRightExpression().equals(other.getRightExpression())
            && hasFilter() == other.hasFilter();
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + getLeftExpression().hashCode();
      result = prime * result + getRightExpression().hashCode();
      result = prime * result + (hasFilter() ? getFilter().hashCode() : 0);
      return result;
   }

   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append(getName());
      sb.append("("); //$NON-NLS-1$
      sb.append(mLeftExpression);
      sb.append(", "); //$NON-NLS-1$
      sb.append(mRightExpression);
      if (hasFilter()) {
         sb.append(", "); //$NON-NLS-1$
         sb.append(getFilter());
      }
      sb.append(")"); //$NON-NLS-1$
      return sb.toString();
   }
}
