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

import java.util.Arrays;
import java.util.List;

import com.obidea.semantika.knowledgebase.TermSubstitutionBinding;

public class Function extends AbstractFunction
{
   private static final long serialVersionUID = 629451L;

   private FunctionSymbol mFunctionSymbol;

   /**
    * Construct a function given the function symbol and the parameters. The
    * function symbol defines the function operation in which this function can
    * execute.
    * 
    * @param symbol
    *           a function symbol.
    * @param terms
    *           a list of parameters.
    */
   public Function(final FunctionSymbol symbol, final ITerm... parameters)
   {
      super(symbol.getName(), symbol.getReturnType(), Arrays.asList(parameters));
      mFunctionSymbol = symbol;
   }

   /**
    * Construct a function given the function symbol and the parameters. The
    * function symbol defines the function operation in which this function can
    * execute.
    * 
    * @param symbol
    *           a function symbol.
    * @param terms
    *           a list of parameters.
    */
   public Function(final FunctionSymbol symbol, final List<? extends ITerm> parameters)
   {
      super(symbol.getName(), symbol.getReturnType(), parameters);
      mFunctionSymbol = symbol;
   }

   /**
    * Construct a function given the name and the parameters.
    * 
    * @param name
    *          a function name.
    * @param terms
    *          a list of parameters.
    */
   public Function(String name, final ITerm... parameters)
   {
      this(new FunctionSymbol(name), Arrays.asList(parameters));
   }

   /**
    * Construct a function given the name and the parameters.
    * 
    * @param name
    *          a function name.
    * @param terms
    *          a list of parameters.
    */
   public Function(String name, final List<? extends ITerm> parameters)
   {
      this(new FunctionSymbol(name), parameters);
   }

   /* package */static Function createBuiltInFunction(BuiltInFunction buildInFunction, ITerm... parameters)
   {
      return createBuiltInFunction(buildInFunction, Arrays.asList(parameters));
   }

   /* package */static Function createBuiltInFunction(BuiltInFunction buildInFunction, List<? extends ITerm> parameters)
   {
      return new Function(buildInFunction.getFunctionSymbol(), parameters);
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
      return mFunctionSymbol.execute(arguments);
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
      final Function other = (Function) obj;
      return getName().equals(other.getName()) && getParameters().equals(other.getParameters());
   }

   @Override
   public void accept(ITermVisitor visitor)
   {
      visitor.visit(this);
   }
}
