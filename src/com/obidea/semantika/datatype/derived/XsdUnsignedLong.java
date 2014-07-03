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
package com.obidea.semantika.datatype.derived;

import java.math.BigInteger;

import javax.xml.bind.DatatypeConverter;

import com.obidea.semantika.datatype.AbstractDerivedDecimalType;
import com.obidea.semantika.datatype.DataType;
import com.obidea.semantika.datatype.DataTypeConstants;
import com.obidea.semantika.datatype.exception.InvalidLexicalFormException;

public class XsdUnsignedLong extends AbstractDerivedDecimalType
{
   private static final XsdUnsignedLong mInstance;
   private static final BigInteger MAX_VALUE;

   static {
      mInstance = new XsdUnsignedLong();
      MAX_VALUE = new BigInteger("18446744073709551615"); // http://www.w3.org/TR/xmlschema-2/#unsignedLong
   }

   /**
    * Private constructor forces use of {@link #getInstance()}
    */
   private XsdUnsignedLong()
   {
      super(DataType.UNSIGNED_LONG);
   }

   public static XsdUnsignedLong getInstance()
   {
      return mInstance;
   }

   @Override
   protected BigInteger parseLexicalForm(String lexicalForm) throws InvalidLexicalFormException
   {
      try {
         final BigInteger n = DatatypeConverter.parseInteger(lexicalForm);
         if (BigInteger.ZERO.compareTo(n) > 0) {
            throw new InvalidLexicalFormException(getName(), lexicalForm);
         }
         if (MAX_VALUE.compareTo(n) < 0) {
            throw new InvalidLexicalFormException(getName(), lexicalForm);
         }
         return n;
      }
      catch (NumberFormatException e) {
         throw new InvalidLexicalFormException(getName(), lexicalForm, e);
      }
   }

   @Override
   public int getType()
   {
      return DataTypeConstants.UNSIGNED_LONG;
   }

   @Override
   public String toString()
   {
      return "xsd:unsignedLong"; //$NON-NLS-1$
   }
}
