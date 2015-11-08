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
package com.obidea.semantika.mapping.base.sql;

import java.util.ArrayList;
import java.util.List;

import com.obidea.semantika.database.sql.base.ISqlExpression;
import com.obidea.semantika.database.sql.base.ISqlUnaryFunction;

public abstract class SqlUnaryFunction extends SqlFunction implements ISqlUnaryFunction
{
   private static final long serialVersionUID = 629451L;

   private String mName;

   private ISqlExpression mParameter;

   public SqlUnaryFunction(String name, String returnType, ISqlExpression parameter)
   {
      super(name, returnType, parameter);
      mName = name;
      mParameter = parameter;
   }

   @Override
   public String getName()
   {
      return mName;
   }

   @Override
   public ISqlExpression getParameterExpression()
   {
      return mParameter;
   }

   @Override
   public List<ISqlExpression> getParameterExpressions()
   {
      List<ISqlExpression> parameters = new ArrayList<ISqlExpression>();
      parameters.add(getParameterExpression());
      return parameters;
   }
}
