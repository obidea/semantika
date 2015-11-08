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
package com.obidea.semantika.expression;

import java.util.LinkedList;
import java.util.List;

import com.obidea.semantika.exception.IllegalOperationException;
import com.obidea.semantika.expression.base.Atom;
import com.obidea.semantika.expression.base.Predicate;
import com.obidea.semantika.expression.base.Term;

public class AtomBuilder
{
   private Predicate mPredicate;

   private List<Term> mTerms = new LinkedList<Term>();

   private static ExpressionObjectFactory sExpressionFactory = ExpressionObjectFactory.getInstance();

   public AtomBuilder(Predicate predicate)
   {
      mPredicate = predicate;
   }

   public AtomBuilder(String predicateName)
   {
      this(sExpressionFactory.getPredicate(predicateName));
   }

   public AtomBuilder append(Term term)
   {
      mTerms.add(term);
      return this;
   }

   public Atom build()
   {
      if (mPredicate == null) {
         throw new IllegalOperationException("Predicate symbol has not been defined yet!");
      }
      return new Atom(mPredicate, mTerms);
   }
}
