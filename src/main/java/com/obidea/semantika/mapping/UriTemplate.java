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
package com.obidea.semantika.mapping;

import java.util.Arrays;
import java.util.List;

import com.obidea.semantika.datatype.DataType;
import com.obidea.semantika.expression.base.AbstractFunction;
import com.obidea.semantika.expression.base.IConstant;
import com.obidea.semantika.expression.base.IFunction;
import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.expression.base.ITermVisitor;
import com.obidea.semantika.expression.base.IUriReference;
import com.obidea.semantika.expression.base.IVariable;
import com.obidea.semantika.expression.base.TermUtils;
import com.obidea.semantika.knowledgebase.TermSubstitutionBinding;
import com.obidea.semantika.util.StringUtils;

public class UriTemplate extends AbstractFunction implements IUriTemplate
{
   private static final long serialVersionUID = 629451L;

   private String mTemplateString;

   public UriTemplate(String templateString, ITerm... parameters)
   {
      this(templateString, Arrays.asList(parameters));
   }

   public UriTemplate(String templateString, List<? extends ITerm> parameters)
   {
      super("&createUri", DataType.ANY_URI, parameters); //$NON-NLS-1$
      mTemplateString = templateString;
   }

   @Override
   public String getTemplateString()
   {
      return mTemplateString;
   }

   @Override
   public void apply(TermSubstitutionBinding binding)
   {
      if (binding.isEmpty()) {
         return;
      }
      for (int i = 0; i < mParameters.size(); i++) {
         ITerm term = mParameters.get(i);
         if (term instanceof IFunction) {
            IFunction innerFunction = (IFunction) term;
            innerFunction.apply(binding); // recursively unifies function
         }
         else if (term instanceof IVariable) {
            IVariable var = (IVariable) term;
            if (binding.isBound(var)) {
               mParameters.set(i, binding.replace(var));
            }
         }
      }
   }

   @Override
   public boolean isEquivalent(Object obj)
   {
      if (obj instanceof UriTemplate) {
         UriTemplate other = (UriTemplate) obj;
         return getTemplateString().equals(other.getTemplateString()) && getArity() == other.getArity();
      }
      return false;
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
      final UriTemplate other = (UriTemplate) obj;
      return getName().equals(other.getName())
            && getTemplateString().equals(other.getTemplateString())
            && getParameters().equals(other.getParameters());
   }

   @Override
   public void accept(ITermVisitor visitor)
   {
      visitor.visit(this);
   }

   @Override
   public IUriReference execute(List<? extends IConstant> arguments)
   {
      String toReturn = mTemplateString;
      for (int i = 0; i < arguments.size(); i++) {
         String value = arguments.get(i).getLexicalValue();
         String str = StringUtils.useUnderscore(value);
         toReturn = toReturn.replace(holder(i+1), str);
      }
      return TermUtils.makeUriReference(toReturn);
   }

   private String holder(int index)
   {
      return String.format("{%d}", index); //$NON-NLS-1$
   }

   @Override
   public String toString()
   {
      final StringBuilder sb = new StringBuilder(getName());
      sb.append("["); // $NON-NLS-1$
      sb.append("\"").append(getTemplateString()).append("\""); // $NON-NLS-1$ // $NON-NLS-2$
      sb.append("]"); // $NON-NLS-1$
      sb.append("("); // $NON-NLS-1$
      boolean needComma = false;
      for (ITerm parameter : mParameters) {
         if (needComma) {
            sb.append(", "); //$NON-NLS-1$
         }
         sb.append(parameter);
         needComma = true;
      }
      sb.append(")"); // $NON-NLS-1$
      return sb.toString();
   }
}
