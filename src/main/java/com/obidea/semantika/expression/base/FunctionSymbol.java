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

import java.io.Serializable;
import java.util.List;

public class FunctionSymbol implements Serializable
{
   private static final long serialVersionUID = 629451L;

   private String mName;
   private IFunctionOperation mOperation;

   public FunctionSymbol(String name)
   {
      this(name, new DefaultOperation());
   }

   public FunctionSymbol(String name, IFunctionOperation op)
   {
      mName = name;
      mOperation = op;
   }

   public String getName()
   {
      return mName;
   }

   public String getReturnType()
   {
      return mOperation.getReturnType();
   }

   public IConstant execute(List<? extends IConstant> arguments)
   {
      return mOperation.execute(arguments);
   }
}
