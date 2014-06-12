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

import java.math.BigDecimal;

import javax.xml.bind.DatatypeConverter;

import com.obidea.semantika.datatype.AbstractXmlType;
import com.obidea.semantika.datatype.DataType;
import com.obidea.semantika.datatype.DataTypeConstants;
import com.obidea.semantika.datatype.IDatatype;
import com.obidea.semantika.datatype.exception.InvalidLexicalFormException;

/**
 * Singleton implementation of <code>xsd:decimal</code> datatype.
 */
public class XsdDecimal extends AbstractXmlType<BigDecimal>
{
   private static final XsdDecimal mInstance;

   static {
      mInstance = new XsdDecimal();
   }

   /**
    * Private constructor forces use of {@link #getInstance()}
    */
   private XsdDecimal()
   {
      super(DataType.DECIMAL);
   }

   public static XsdDecimal getInstance()
   {
      return mInstance;
   }

   @Override
   public IDatatype<?> getPrimitiveDatatype()
   {
      return this;
   }

   @Override
   public BigDecimal getValue(String lexicalForm) throws InvalidLexicalFormException
   {
      try {
         return DatatypeConverter.parseDecimal(lexicalForm);
      }
      catch (NumberFormatException e) {
         throw new InvalidLexicalFormException(getName(), lexicalForm);
      }
   }

   @Override
   public boolean isPrimitive()
   {
      return true;
   }

   @Override
   public int getType()
   {
      return DataTypeConstants.DECIMAL;
   }

   @Override
   public String toString()
   {
      return "xsd:decimal"; //$NON-NLS-1$
   }
}
