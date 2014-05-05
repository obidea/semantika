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

import java.net.IDN;
import java.net.URI;
import java.net.URISyntaxException;

import com.obidea.semantika.datatype.DataType;
import com.obidea.semantika.exception.SemantikaRuntimeException;

public class UriReference extends AbstractConstant implements IUriReference
{
   private static final long serialVersionUID = 629451L;

   /**
    * Constructs a URI reference which is built from a valid URI string input.
    * 
    * @param uri
    *           a valid URI string.
    */
   public UriReference(String uri)
   {
      super(uri, DataType.ANY_URI);
   }

   /**
    * Returns the URI object given the input <code>term</code>. This utility
    * method only returns value if the input term is inherent
    * {@link IUriReference}. It returns <code>null</code> otherwise.
    */
   public static URI getUri(ITerm term)
   {
      if (term instanceof IUriReference) {
         IUriReference uriRef = (IUriReference) term;
         return uriRef.toUri();
      }
      return null;
   }

   @Override
   public URI toUri()
   {
      String value = getLexicalValue();
      try {
         final String idn = IDN.toASCII(value);
         return new URI(idn).normalize();
      }
      catch (URISyntaxException e) {
         throw new SemantikaRuntimeException("Failed parsing URI value (" + value + ")", e); //$NON-NLS-1$ //$NON-NLS-2$
      }
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
      result = prime * result + toUri().hashCode();
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
      final UriReference other = (UriReference) obj;
      
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
      return "<" + getLexicalValue() + ">";
   }
}
