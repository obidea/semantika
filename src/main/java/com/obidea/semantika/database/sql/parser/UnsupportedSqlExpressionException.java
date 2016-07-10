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
package com.obidea.semantika.database.sql.parser;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.SelectBody;

public class UnsupportedSqlExpressionException extends SqlParserException
{
   private static final long serialVersionUID = 629451L;

   private String mOperationName;

   public UnsupportedSqlExpressionException(Expression expr)
   {
      super(String.format("The SQL expression '%s' is not supported yet", expr.toString()));
   }

   public UnsupportedSqlExpressionException(SelectBody selectBody)
   {
      super(String.format("The SQL select '%s' is not supported yet", selectBody.toString()));
   }

   public UnsupportedSqlExpressionException(FromItem fromItem)
   {
      super(String.format("The SQL from '%s' is not supported yet", fromItem.toString()));
   }

   public UnsupportedSqlExpressionException(Join join)
   {
      super(String.format("The SQL join '%s' is not supported yet", join.toString()));
   }

   @Deprecated
   public UnsupportedSqlExpressionException(String operation)
   {
      super(String.format("SQL %s expression is not supported yet", operation));
      mOperationName = operation;
   }

   @Deprecated
   public String getUnsupportedOperation()
   {
      return mOperationName;
   }
}
