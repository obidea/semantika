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
package com.obidea.semantika.datatype.primitive;

import java.net.IDN;
import java.net.URI;
import java.net.URISyntaxException;

import com.obidea.semantika.datatype.AbstractXmlType;
import com.obidea.semantika.datatype.DataType;
import com.obidea.semantika.datatype.DataTypeConstants;
import com.obidea.semantika.datatype.IDatatype;
import com.obidea.semantika.datatype.exception.InvalidLexicalFormException;

/**
 * Singleton implementation of <code>xsd:anyURI</code> datatype.
 */
public class XsdAnyUri extends AbstractXmlType<URI>
{
   private static final XsdAnyUri mInstance;

   static {
      mInstance = new XsdAnyUri();
   }

   /**
    * Private constructor forces use of {@link #getInstance()}
    */
   private XsdAnyUri()
   {
      super(DataType.ANY_URI);
   }

   public static XsdAnyUri getInstance()
   {
      return mInstance;
   }

   @Override
   public IDatatype<?> getPrimitiveDatatype()
   {
      return this;
   }

   @Override
   public URI getValue(String lexicalForm)
   {
      try {
         return new URI(encode(lexicalForm.trim())).normalize();
      }
      catch (URISyntaxException e) {
         throw new InvalidLexicalFormException(getName(), lexicalForm);
      }
   }

   public static String encode(String hostname)
   {
      return IDN.toASCII(hostname);
   }

   @Override
   public boolean isPrimitive()
   {
      return true;
   }

   @Override
   public int getType()
   {
      return DataTypeConstants.ANY_URI;
   }

   @Override
   public String toString()
   {
      return "xsd:anyURI"; //$NON-NLS-1$
   }
}
