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

public interface ITermVisitor
{
   void visit(IVariable variable);

   void visit(IConstant literal);

   void visit(ILiteral literal);

   /**
    * @deprecated since 1.8. Use {@link ITermVisitor#visit(IIriReference)} instead.
    */
   @Deprecated
   default void visit(IUriReference uriReference)
   {
      visit((IIriReference) uriReference);
   }

   void visit(IIriReference iriReference);

   void visit(IFunction function);
}
