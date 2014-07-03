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

import com.obidea.semantika.datatype.AbstractTimelineType;
import com.obidea.semantika.datatype.DataType;
import com.obidea.semantika.datatype.DataTypeConstants;
import com.obidea.semantika.datatype.IDatatype;

/**
 * Singleton implementation of <code>xsd:date</code> datatype.
 */
public class XsdDate extends AbstractTimelineType
{
   private static final XsdDate mInstance;
   
   static {
      mInstance = new XsdDate();
   }
   
   /**
    * Private constructor forces use of {@link #getInstance()}
    */
   private XsdDate() 
   {
      super(DataType.DATE, javax.xml.datatype.DatatypeConstants.DATE);
   }
   
   public static XsdDate getInstance() 
   {
      return mInstance;
   }

   @Override
   public IDatatype<?> getPrimitiveDatatype()
   {
      return this;
   }

   @Override
   public int getType()
   {
      return DataTypeConstants.DATE;
   }

   @Override
   public String toString()
   {
      return "xsd:date"; //$NON-NLS-1$
   }
}
