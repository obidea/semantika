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
 * Singleton implementation of <code>xsd:dateTime</code> datatype.
 */
public class XsdDateTime extends AbstractTimelineType
{
   private static final XsdDateTime instance;

   static {
      instance = new XsdDateTime();
   }

   /**
    * Private constructor forces use of {@link #getInstance()}
    */
   private XsdDateTime()
   {
      super(DataType.DATE_TIME, javax.xml.datatype.DatatypeConstants.DATETIME);
   }

   public static XsdDateTime getInstance()
   {
      return instance;
   }

   @Override
   public IDatatype<?> getPrimitiveDatatype()
   {
      return this;
   }

   @Override
   public int getType()
   {
      return DataTypeConstants.DATE_TIME;
   }
}
