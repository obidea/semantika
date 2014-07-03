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
import com.obidea.semantika.database.sql.base.ISqlFunction;

public abstract class SqlFunction extends FunctionMediator implements ISqlFunction
{
   private static final long serialVersionUID = 629451L;

   private String mName;

   private List<ISqlExpression> mParameters;

   public SqlFunction(String name, String returnType, List<ISqlExpression> parameters)
   {
      super(name, returnType, parameters);
      mName = name;
      mParameters = parameters;
   }

   public SqlFunction(String name, String returnType, ISqlExpression... parameters)
   {
      this(name, returnType, Arrays.asList(parameters));
   }

   @Override
   public String getName()
   {
      return mName;
   }

   @Override
   public List<ISqlExpression> getParameterExpressions()
   {
      return new ArrayList<ISqlExpression>(mParameters);
   }
}
