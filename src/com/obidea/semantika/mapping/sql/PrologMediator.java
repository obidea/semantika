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

import java.util.Collections;
import java.util.Set;

import com.obidea.semantika.database.sql.base.ISqlExpression;
import com.obidea.semantika.expression.base.AbstractProlog;
import com.obidea.semantika.expression.base.BuiltInPredicate;
import com.obidea.semantika.expression.base.IAtom;
import com.obidea.semantika.expression.base.IFunction;
import com.obidea.semantika.expression.base.IQueryExt;
import com.obidea.semantika.expression.base.IQueryExtVisitor;
import com.obidea.semantika.expression.base.ITerm;

public abstract class PrologMediator extends AbstractProlog implements IQueryExt
{
   private static final long serialVersionUID = 629451L;

   public PrologMediator()
   {
      super(BuiltInPredicate.Answer);
   }

   protected void notifySelectItemChanged(ISqlExpression expression)
   {
      ITerm term = (ITerm) expression;
      super.addDistTerm(term);
   }

   protected void notifyFromExpressionChanged(ISqlExpression expression)
   {
      IAtom atom = (IAtom) expression;
      super.addAtom(atom);
   }

   protected void notifyWhereExpressionChanged(ISqlExpression expression)
   {
      IFunction filter = (IFunction) expression;
      mConstraints.add(filter);
   }

   protected void notifyQueryConstraintAllRemoved()
   {
      mConstraints.clear();
   }

   @Override
   public void setFilter(IFunction filter)
   {
      if (filter != null) {
         mConstraints.add(filter);
      }
   }

   @Override
   public Set<IFunction> getFilters()
   {
      return Collections.unmodifiableSet(mConstraints);
   }

   @Override
   public boolean hasFilter()
   {
      return (getFilters().size() > 0) ? true : false;
   }

   @Override
   public void accept(IQueryExtVisitor visitor)
   {
      visitor.visit(this);
   }
}
