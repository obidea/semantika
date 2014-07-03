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

/**
 * Singleton implementation of <code>xsd:integer</code> datatype.
 */
public class XsdInteger extends AbstractDerivedDecimalType
{
   private static final XsdInteger mInstance;

   static {
      mInstance = new XsdInteger();
   }

   /**
    * Private constructor forces use of {@link #getInstance()}
    */
   private XsdInteger()
   {
      super(DataType.INTEGER);
   }

   public static XsdInteger getInstance()
   {
      return mInstance;
   }

   @Override
   protected BigInteger parseLexicalForm(String lexicalForm) throws InvalidLexicalFormException
   {
      try {
         return DatatypeConverter.parseInteger(lexicalForm);
      }
      catch (NumberFormatException e) {
         throw new InvalidLexicalFormException(getName(), lexicalForm, e);
      }
   }

   @Override
   public int getType()
   {
      return DataTypeConstants.INTEGER;
   }

   @Override
   public String toString()
   {
      return "xsd:integer"; //$NON-NLS-1$
   }
}
