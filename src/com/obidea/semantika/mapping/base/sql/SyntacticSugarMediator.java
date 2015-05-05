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

import com.obidea.semantika.expression.base.AbstractAtom;
import com.obidea.semantika.expression.base.IAtomVisitor;
import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.expression.base.Predicate;
import com.obidea.semantika.knowledgebase.TermSubstitutionBinding;
import com.obidea.semantika.util.CollectionUtils;

public class SyntacticSugarMediator extends AbstractAtom
{
   private static final long serialVersionUID = 629451L;

   protected SyntacticSugarMediator()
   {
      super(new Predicate(""), CollectionUtils.createEmptyList(ITerm.class));
   }

   @Override
   public final int getArity()
   {
      return 0; // syntactic sugar has no arity
   }

   @Override
   public final boolean isGround()
   {
      return false;
   }

   @Override
   public void apply(TermSubstitutionBinding binding)
   {
      // NO-OP
   }

   /**
    * Accept a visitor to collect the internal content of this atom.
    * 
    * @param visitor
    *           a visitor object.
    */
   @Override
   public void accept(IAtomVisitor visitor)
   {
      visitor.visit(this);
   }
}
