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
package com.obidea.semantika.datatype.primitive;

import com.obidea.semantika.datatype.AbstractTimelineType;
import com.obidea.semantika.datatype.DataType;
import com.obidea.semantika.datatype.DataTypeConstants;
import com.obidea.semantika.datatype.IDatatype;

public class XsdGMonth extends AbstractTimelineType
{
   private static final XsdGMonth mInstance;

   static {
      mInstance = new XsdGMonth();
   }

   /**
    * Private constructor forces use of {@link #getInstance()}
    */
   private XsdGMonth()
   {
      super(DataType.G_MONTH, javax.xml.datatype.DatatypeConstants.GMONTH);
   }

   public static XsdGMonth getInstance()
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
      return DataTypeConstants.G_MONTH;
   }

   @Override
   public String toString()
   {
      return "xsd:gMonth"; //$NON-NLS-1$
   }
}
