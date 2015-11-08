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
package com.obidea.semantika.datatype.derived;

import javax.xml.bind.DatatypeConverter;

import com.obidea.semantika.datatype.AbstractDerivedDecimalType;
import com.obidea.semantika.datatype.DataType;
import com.obidea.semantika.datatype.DataTypeConstants;
import com.obidea.semantika.datatype.exception.InvalidLexicalFormException;

public class XsdUnsignedInt extends AbstractDerivedDecimalType
{
   private static final XsdUnsignedInt mInstance;
   private static final long MAX_VALUE;

   static {
      mInstance = new XsdUnsignedInt();
      MAX_VALUE = 4294967295l; // http://www.w3.org/TR/xmlschema-2/#unsignedInt
   }

   /**
    * Private constructor forces use of {@link #getInstance()}
    */
   private XsdUnsignedInt()
   {
      super(DataType.UNSIGNED_INT);
   }

   public static XsdUnsignedInt getInstance()
   {
      return mInstance;
   }

   @Override
   protected Long parseLexicalForm(String lexicalForm) throws InvalidLexicalFormException
   {
      try {
         final long l = DatatypeConverter.parseLong(lexicalForm);
         if (0 > l) {
            throw new InvalidLexicalFormException(getName(), lexicalForm);
         }
         if (MAX_VALUE < l) {
            throw new InvalidLexicalFormException(getName(), lexicalForm);
         }
         return l;
      }
      catch (NumberFormatException e) {
         throw new InvalidLexicalFormException(getName(), lexicalForm, e);
      }
   }

   @Override
   public int getType()
   {
      return DataTypeConstants.UNSIGNED_INT;
   }

   @Override
   public String toString()
   {
      return "xsd:unsignedInt"; //$NON-NLS-1$
   }
}
