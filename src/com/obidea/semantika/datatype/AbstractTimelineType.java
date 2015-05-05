/*
 * Copyright (c) 2013-2015 Josef Hardi <josef.hardi@gmail.com>
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
package com.obidea.semantika.datatype;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import com.obidea.semantika.datatype.exception.InvalidLexicalFormException;
import com.obidea.semantika.exception.SemantikaRuntimeException;

public abstract class AbstractTimelineType extends AbstractXmlType<XMLGregorianCalendar>
{
   protected final QName mSchemaType;

   private final static DatatypeFactory mDtFactory;

   static {
      try {
         mDtFactory = DatatypeFactory.newInstance();
      }
      catch (DatatypeConfigurationException e) {
         throw new SemantikaRuntimeException("Failure initializing restricted timeline datatype support.", e); //$NON-NLS-1$
      }
   }

   public AbstractTimelineType(String name, QName schemaType)
   {
      super(name);
      mSchemaType = schemaType;
   }

   @Override
   public XMLGregorianCalendar getValue(String lexicalForm)
   {
      try {
         XMLGregorianCalendar c = mDtFactory.newXMLGregorianCalendar(lexicalForm);
         if (!mSchemaType.equals(c.getXMLSchemaType())) {
            throw new InvalidLexicalFormException(getName(), lexicalForm);
         }
         return c;
      }
      catch (IllegalArgumentException e) {
         /*
          * An IllegalArgumentException is thrown by newXMLGregorianCalendar()
          * if the lexical form is not one of the XML Schema datetime types
          */
         throw new InvalidLexicalFormException(getName(), lexicalForm);
      }
      catch (IllegalStateException e) {
         /*
          * An IllegalStateException is thrown by getXMLSchemaType() if the
          * combination of fields set in the calendar object doesn't match one
          * of the XML Schema datetime types
          */
         throw new InvalidLexicalFormException(getName(), lexicalForm);
      }
   }

   @Override
   public boolean isPrimitive()
   {
      return true;
   }
}
