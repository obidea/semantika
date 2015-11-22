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

import com.obidea.semantika.datatype.DataType;

/**
 * Represents the IRI Reference implementation.
 *
 * @author Josef Hardi <josef.hardi@gmail.com>
 * @since 1.8
 */
public class IriReference extends AbstractConstant implements IIriReference
{
   private static final long serialVersionUID = 629451L;

   /**
    * Constructs a IRI reference which is built from a valid IRI string input.
    * 
    * @param iriString
    *           a valid IRI string.
    */
   public IriReference(String iriString)
   {
      super(iriString, DataType.ANY_URI);
   }

   /**
    * Returns the IRI object given the input <code>term</code>. This utility
    * method only returns value if the input term is inherent
    * {@link IIriReference}. It returns <code>null</code> otherwise.
    */
   public static Iri getIri(ITerm term)
   {
      if (term instanceof IIriReference) {
         IIriReference iriRef = (IIriReference) term;
         return iriRef.toIri();
      }
      return null;
   }

   @Override
   public Iri toIri()
   {
      String value = getLexicalValue();
      return Iri.create(value);
   }

   @Override
   public URI toUri()
   {
      String value = getLexicalValue();
      return URI.create(value);
   }

   @Override
   public boolean isTyped()
   {
      return true;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + toIri().hashCode();
      result = prime * result + getDatatype().hashCode();
      return result;
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
      final IriReference other = (IriReference) obj;
      return getLexicalValue().equals(other.getLexicalValue());
   }

   @Override
   public void accept(ITermVisitor visitor)
   {
      visitor.visit(this);
   }

   /*
    * Internal use only for debugging.
    */
   @Override
   public String toString()
   {
      return toIri().toQuotedString();
   }
}
