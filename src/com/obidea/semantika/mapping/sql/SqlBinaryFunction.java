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
package com.obidea.semantika.mapping.sql;

import java.util.ArrayList;
import java.util.List;

import com.obidea.semantika.database.sql.base.ISqlBinaryFunction;
import com.obidea.semantika.database.sql.base.ISqlExpression;

public abstract class SqlBinaryFunction extends SqlFunction implements ISqlBinaryFunction
{
   private static final long serialVersionUID = 629451L;

   private String mName;

   protected ISqlExpression mLeftParameter;
   protected ISqlExpression mRightParameter;

   public SqlBinaryFunction(String name, String returnType, ISqlExpression leftParameter, ISqlExpression rightParameter)
   {
      super(name, returnType, leftParameter, rightParameter);
      mName = name;
      mLeftParameter = leftParameter;
      mRightParameter = rightParameter;
   }

   @Override
   public String getName()
   {
      return mName;
   }

   @Override
   public ISqlExpression getLeftParameterExpression()
   {
      return mLeftParameter;
   }

   @Override
   public ISqlExpression getRightParameterExpression()
   {
      return mRightParameter;
   }

   @Override
   public List<ISqlExpression> getParameterExpressions()
   {
      List<ISqlExpression> parameters = new ArrayList<ISqlExpression>();
      parameters.add(getLeftParameterExpression());
      parameters.add(getRightParameterExpression());
      return parameters;
   }
}
