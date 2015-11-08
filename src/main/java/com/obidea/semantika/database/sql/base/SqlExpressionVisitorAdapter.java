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
package com.obidea.semantika.database.sql.base;

public abstract class SqlExpressionVisitorAdapter implements ISqlExpressionVisitor
{
   @Override
   public void visit(ISqlTable tableExpression)
   {
      // NO-OP: To be implemented by subclasses
   }

   @Override
   public void visit(ISqlColumn columnExpression)
   {
      // NO-OP: To be implemented by subclasses
   }

   @Override
   public void visit(ISqlFunction filterExpression)
   {
      // NO-OP: To be implemented by subclasses
   }

   @Override
   public void visit(ISqlValue valueExpression)
   {
      // NO-OP: To be implemented by subclasses
   }

   @Override
   public void visit(ISqlJoin joinExpression)
   {
      // NO-OP: To be implemented by subclasses
   }

   @Override
   public void visit(ISqlSubQuery subQueryExpression)
   {
      // NO-OP: To be implemented by subclasses
   }
}
