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
package com.obidea.semantika.datatype.primitive;

import javax.xml.bind.DatatypeConverter;

import com.obidea.semantika.datatype.AbstractXmlType;
import com.obidea.semantika.datatype.DataType;
import com.obidea.semantika.datatype.DataTypeConstants;
import com.obidea.semantika.datatype.IDatatype;
import com.obidea.semantika.datatype.exception.InvalidLexicalFormException;

/**
 * Singleton implementation of <code>xsd:string</code> datatype.
 */
public class XsdString extends AbstractXmlType<String>
{
   private static final XsdString mInstance;

   static {
      mInstance = new XsdString();
   }

   /**
    * Private constructor forces use of {@link #getInstance()}
    */
   private XsdString()
   {
      super(DataType.STRING);
   }

   public static XsdString getInstance()
   {
      return mInstance;
   }

   @Override
   public IDatatype<?> getPrimitiveDatatype()
   {
      return this;
   }

   @Override
   public String getValue(String lexicalForm) throws InvalidLexicalFormException
   {
      return DatatypeConverter.parseString(lexicalForm);
   }

   @Override
   public boolean isPrimitive()
   {
      return true;
   }

   @Override
   public int getType()
   {
      return DataTypeConstants.STRING;
   }
}
