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
package com.obidea.semantika.mapping.base;

import java.net.URI;
import java.util.List;
import java.util.Set;

import com.obidea.semantika.expression.base.IAtom;
import com.obidea.semantika.expression.base.IFunction;
import com.obidea.semantika.expression.base.IPredicate;
import com.obidea.semantika.expression.base.Predicate;

/**
 * Provides a skeletal implementation of the <code>IMapping</code> interface.
 */
public abstract class AbstractMapping implements IMapping
{
   private static final long serialVersionUID = 629451L;

   protected URI mSignature;

   public AbstractMapping(URI signature)
   {
      mSignature = signature;
   }

   @Override
   public URI getSignature()
   {
      return mSignature;
   }

   @Override
   public IPredicate getHeadSymbol()
   {
      return new Predicate(mSignature);
   }

   @Override
   public TripleAtom getHead()
   {
      return getTargetAtom();
   }

   @Override
   public List<IAtom> getBody()
   {
      return getSourceQuery().getBody();
   }

   public Set<IFunction> getFilters()
   {
      return getSourceQuery().getFilters();
   }

   @Override
   public boolean isGround()
   {
      return false; // Mapping assertion should not be ground
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + getTargetAtom().hashCode();
      result = prime * result + getSourceQuery().hashCode();
      return result;
   }
}
