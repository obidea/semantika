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

/**
 * Singleton implementation of <code>xsd:long</code> datatype.
 */
public class XsdLong extends AbstractDerivedDecimalType
{
   private static final XsdLong mInstance;

   static {
      mInstance = new XsdLong();
   }

   /**
    * Private constructor forces use of {@link #getInstance()}
    */
   private XsdLong()
   {
      super(DataType.LONG);
   }

   public static XsdLong getInstance()
   {
      return mInstance;
   }

   @Override
   protected Long parseLexicalForm(String lexicalForm) throws InvalidLexicalFormException
   {
      try {
         long l = DatatypeConverter.parseLong(lexicalForm);
         if (l < Long.MIN_VALUE || l > Long.MAX_VALUE) {
            throw new InvalidLexicalFormException(getName(), lexicalForm);
         }
         return Long.valueOf(l);
      }
      catch (NumberFormatException e) {
         throw new InvalidLexicalFormException(getName(), lexicalForm, e);
      }
   }

   @Override
   public int getType()
   {
      return DataTypeConstants.LONG;
   }

   @Override
   public String toString()
   {
      return "xsd:long"; //$NON-NLS-1$
   }
}
