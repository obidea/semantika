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
package com.obidea.semantika.mapping.base.sql;

import com.obidea.semantika.database.sql.base.ISqlExpression;
import com.obidea.semantika.database.sql.base.ISqlExpressionVisitor;
import com.obidea.semantika.datatype.DataType;

public class SqlNotEqualsTo extends SqlBinaryFunction
{
   private static final long serialVersionUID = 629451L;

   public SqlNotEqualsTo(ISqlExpression leftExpression, ISqlExpression rightExpression)
   {
      super("NE", DataType.BOOLEAN, leftExpression, rightExpression); //$NON-NLS-1$
   }

   @Override
   public String getStringExpression()
   {
      return "<>"; //$NON-NLS-1$
   }

   @Override
   public void accept(ISqlExpressionVisitor visitor)
   {
      visitor.visit(this);
   }
}
