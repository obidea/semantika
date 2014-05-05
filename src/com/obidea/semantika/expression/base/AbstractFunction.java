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

import com.obidea.semantika.knowledgebase.TermSubstitutionBinding;
import com.obidea.semantika.util.StringUtils;

public abstract class AbstractFunction extends Term implements IFunction
{
   private static final long serialVersionUID = 629451L;

   protected List<ITerm> mParameters;

   public AbstractFunction(String name, String returnType, final List<? extends ITerm> parameters)
   {
      super(name, returnType);
      mParameters = new ArrayList<ITerm>(parameters);
   }

   @Override
   public List<ITerm> getParameters()
   {
      return mParameters;
   }

   @Override
   public int getArity()
   {
      return mParameters.size();
   }

   public boolean isUnary()
   {
      return getArity() == 1;
   }

   public boolean isBinary()
   {
      return getArity() == 2;
   }

   @Override
   public ITerm getParameter(int position)
   {
      return mParameters.get(position);
   }

   @Override
   public void apply(TermSubstitutionBinding binding)
   {
      // NO-OP: To be implemented by subclasses
   }

   @Override
   public IConstant execute(List<? extends IConstant> arguments)
   {
      return new NullValue(); // NO-OP: To be implemented by subclasses
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + getName().hashCode();
      result = prime * result + getParameters().hashCode();
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
      if (obj instanceof AbstractFunction) {
         final AbstractFunction other = (AbstractFunction) obj;
         return getName().equals(other.getName()) && getParameters().equals(other.getParameters());
      }
      return false;
   }

   @Override
   public boolean isEquivalent(Object obj)
   {
      if (obj instanceof AbstractFunction) {
         AbstractFunction other = (AbstractFunction) obj;
         return getName().equals(other.getName()) && getArity() == other.getArity();
      }
      return false;
   }

   /*
    * Internal use only for debugging.
    */

   @Override
   public String toString()
   {
      final StringBuilder sb = new StringBuilder(getName());
      sb.append("("); // $NON-NLS-1$
      boolean needComma = false;
      for (ITerm t : getParameters()) {
         if (needComma) {
            sb.append(", "); //$NON-NLS-1$
         }
         sb.append(t);
         needComma = true;
      }
      sb.append(")"); // $NON-NLS-1$
      if (isTyped()) {
         String type = getDatatype().substring(getDatatype().indexOf("#") + 1); // $NON-NLS-1$
         sb.append(":").append(StringUtils.toUpperCase(type)); //$NON-NLS-1$
      }
      return sb.toString();
   }
}
