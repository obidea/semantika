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
package com.obidea.semantika.mapping.base.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.obidea.semantika.database.sql.base.ISqlExpression;
import com.obidea.semantika.expression.base.AbstractFunction;
import com.obidea.semantika.expression.base.IConstant;
import com.obidea.semantika.expression.base.IFunction;
import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.expression.base.ITermVisitor;
import com.obidea.semantika.expression.base.IVariable;
import com.obidea.semantika.expression.base.Term;
import com.obidea.semantika.knowledgebase.TermSubstitutionBinding;
import com.obidea.semantika.mapping.base.IMappingTerm;
import com.obidea.semantika.mapping.base.TermType;

public abstract class FunctionMediator extends AbstractFunction implements IMappingTerm
{
   private static final long serialVersionUID = 629451L;

   private int mTermType = TermType.LITERAL_TYPE; // by default

   public FunctionMediator(String name, String returnType, ISqlExpression... expressions)
   {
      this(name, returnType, Arrays.asList(expressions));
   }

   public FunctionMediator(String name, String returnType, List<ISqlExpression> parameters)
   {
      super(name, returnType, getFunctionTerms(parameters));
   }

   private static List<? extends Term> getFunctionTerms(List<ISqlExpression> expressions)
   {
      List<Term> toReturn = new ArrayList<Term>();
      for (ISqlExpression expression : expressions) {
         toReturn.add((Term) expression);
      }
      return toReturn;
   }

   @Override
   public void setTermType(int type)
   {
      mTermType = type;
   }

   @Override
   public int getTermType()
   {
      return mTermType;
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
   public IConstant execute(List<? extends IConstant> arguments)
   {
      return null; // Override by subclasses
   }

   @Override
   public void accept(ITermVisitor visitor)
   {
      visitor.visit(this);
   }
}
