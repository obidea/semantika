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

public class XsdByte extends AbstractDerivedDecimalType
{
   private static final XsdByte mInstance;

   static {
      mInstance = new XsdByte();
   }

   /**
    * Private constructor forces use of {@link #getInstance()}
    */
   private XsdByte()
   {
      super(DataType.BYTE);
   }

   public static XsdByte getInstance()
   {
      return mInstance;
   }

   @Override
   protected Byte parseLexicalForm(String lexicalForm) throws InvalidLexicalFormException
   {
      try {
         byte b = DatatypeConverter.parseByte(lexicalForm);
         if (b < Byte.MIN_VALUE || b > Byte.MAX_VALUE) {
            throw new InvalidLexicalFormException(getName(), lexicalForm);
         }
         return Byte.valueOf(b);
      }
      catch (NumberFormatException e) {
         throw new InvalidLexicalFormException(getName(), lexicalForm, e);
      }
   }

   @Override
   public int getType()
   {
      return DataTypeConstants.BYTE;
   }

   @Override
   public String toString()
   {
      return "xsd:byte"; //$NON-NLS-1$
   }
}
