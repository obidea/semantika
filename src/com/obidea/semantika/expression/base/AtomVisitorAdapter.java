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

public abstract class AtomVisitorAdapter implements IAtomVisitor
{
   @Override
   public void visit(IVariable variable)
   {
      // NO-OP: To be implemented by subclasses
   }

   @Override
   public void visit(IConstant constant)
   {
      // NO-OP: To be implemented by subclasses
   }

   @Override
   public void visit(ILiteral literal)
   {
      // NO-OP: To be implemented by subclasses
   }

   @Override
   public void visit(IUriReference uriReference)
   {
      // NO-OP: To be implemented by subclasses
   }

   @Override
   public void visit(IFunction function)
   {
      // NO-OP: To be implemented by subclasses
   }

   @Override
   public void visit(IAtom atom)
   {
      // NO-OP: To be implemented by subclasses
   }

   @Override
   public void visit(IPredicate predicate)
   {
      // NO-OP: To be implemented by subclasses
   }
}
