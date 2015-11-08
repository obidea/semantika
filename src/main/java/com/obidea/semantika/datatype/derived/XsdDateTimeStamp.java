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

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import com.obidea.semantika.datatype.AbstractTimelineType;
import com.obidea.semantika.datatype.DataType;
import com.obidea.semantika.datatype.DataTypeConstants;
import com.obidea.semantika.datatype.IDatatype;
import com.obidea.semantika.datatype.exception.InvalidLexicalFormException;
import com.obidea.semantika.datatype.primitive.XsdDateTime;

/**
 * Singleton implementation of <code>xsd:dateTimeStamp</code> datatype.
 */
public class XsdDateTimeStamp extends AbstractTimelineType
{
   private static final XsdDateTimeStamp mInstance;

   static {
      mInstance = new XsdDateTimeStamp();
   }
   
   /**
    * Private constructor forces use of {@link #getInstance()}
    */
   private XsdDateTimeStamp()
   {
      super(DataType.DATE_TIME_STAMP, javax.xml.datatype.DatatypeConstants.DATETIME);
   }

   public static XsdDateTimeStamp getInstance()
   {
      return mInstance;
   }

   @Override
   public XMLGregorianCalendar getValue(String lexicalForm) throws InvalidLexicalFormException
   {
      final XMLGregorianCalendar c = super.getValue(lexicalForm);
      if (c.getTimezone() == DatatypeConstants.FIELD_UNDEFINED) {
         throw new InvalidLexicalFormException(getName(), lexicalForm);
      }
      return c;
   }

   @Override
   public IDatatype<?> getPrimitiveDatatype()
   {
      return XsdDateTime.getInstance();
   }

   @Override
   public boolean isPrimitive()
   {
      return false;
   }

   @Override
   public int getType()
   {
      return DataTypeConstants.DATE_TIME_STAMP;
   }

   @Override
   public String toString()
   {
      return "xsd:dateTimeStamp"; //$NON-NLS-1$
   }
}
