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

import javax.xml.bind.DatatypeConverter;

import com.obidea.semantika.datatype.AbstractDerivedDecimalType;
import com.obidea.semantika.datatype.DataType;
import com.obidea.semantika.datatype.DataTypeConstants;
import com.obidea.semantika.datatype.exception.InvalidLexicalFormException;

public class XsdUnsignedShort extends AbstractDerivedDecimalType
{
   private static final XsdUnsignedShort mInstance;
   private static final int MAX_VALUE;

   static {
      mInstance = new XsdUnsignedShort();
      MAX_VALUE = 65535; // http://www.w3.org/TR/xmlschema-2/#unsignedShort
   }

   /**
    * Private constructor forces use of {@link #getInstance()}
    */
   private XsdUnsignedShort()
   {
      super(DataType.UNSIGNED_SHORT);
   }

   public static XsdUnsignedShort getInstance()
   {
      return mInstance;
   }

   @Override
   protected Number parseLexicalForm(String lexicalForm) throws InvalidLexicalFormException
   {
      try {
         final int i = DatatypeConverter.parseInt(lexicalForm);
         if (0 > i) {
            throw new InvalidLexicalFormException(getName(), lexicalForm);
         }
         if (MAX_VALUE < i) {
            throw new InvalidLexicalFormException(getName(), lexicalForm);
         }
         return i;
      }
      catch (NumberFormatException e) {
         throw new InvalidLexicalFormException(getName(), lexicalForm, e);
      }
   }

   @Override
   public int getType()
   {
      return DataTypeConstants.UNSIGNED_SHORT;
   }

   @Override
   public String toString()
   {
      return "xsd:unsignedShort"; //$NON-NLS-1$
   }
}
