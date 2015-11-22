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
package com.obidea.semantika.mapping.base;

import com.obidea.semantika.expression.base.IAtom;
import com.obidea.semantika.expression.base.IConstant;
import com.obidea.semantika.expression.base.IFunction;
import com.obidea.semantika.expression.base.IIriReference;
import com.obidea.semantika.expression.base.ILiteral;
import com.obidea.semantika.expression.base.IPredicate;
import com.obidea.semantika.expression.base.IVariable;

/**
 * Provides a default implementation of <code>IMappingVisitor</code>. Client
 * code may override methods that need to be re-implemented.
 */
public abstract class MappingVisitorAdapter implements IMappingVisitor
{
   @Override
   public void visit(IClassMapping mapping)
   {
      // NO-OP: To be implemented by subclasses
   }

   @Override
   public void visit(IPropertyMapping mapping)
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

   @Override
   public void visit(IVariable variable)
   {
      // NO-OP: To be implemented by subclasses
   }

   @Override
   public void visit(IConstant literal)
   {
      // NO-OP: To be implemented by subclasses
   }

   @Override
   public void visit(ILiteral literal)
   {
      // NO-OP: To be implemented by subclasses
   }

   @Override
   public void visit(IIriReference iriReference)
   {
      // TODO Auto-generated method stub
   }

   @Override
   public void visit(IFunction function)
   {
      // NO-OP: To be implemented by subclasses
   }
}
