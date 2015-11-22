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

import java.net.URI;

public class Predicate implements IPredicate
{
   private static final long serialVersionUID = 629451L;

   private String mName;

   /**
    * Creates a logic predicate.
    * 
    * @param name
    *           The predicate name.
    */
   public Predicate(String name)
   {
      mName = name;
   }

   /**
    * Creates a logic predicate
    * 
    * @param name
    *       The predicate name in URI construct
    * @deprecated since 1.8. Use constructor {@link Predicate(Iri)} instead.
    */
   @Deprecated
   public Predicate(URI name)
   {
      mName = name.toString();
   }

   /**
    * Creates a logic predicate
    * 
    * @param name
    *       The predicate name in IRI construct
    */
   public Predicate(Iri iri)
   {
      mName = iri.toString();
   }

   @Override
   public String getName()
   {
      return mName;
   }

   @Override
   public int hashCode()
   {
      return 31 + mName.hashCode();
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final Predicate other = (Predicate) obj;

      return mName.equals(other.mName);
   }

   @Override
   public void accept(IAtomVisitor visitor)
   {
      visitor.visit(this);
   }

   /*
    * Internal use only for debugging.
    */
   @Override
   public String toString()
   {
      return getName();
   }
}
