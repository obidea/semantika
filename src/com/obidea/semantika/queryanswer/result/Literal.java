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
package com.obidea.semantika.queryanswer.result;

import com.obidea.semantika.datatype.XmlDataTypeProfile;

public class Literal implements ILiteral
{
   private static final long serialVersionUID = 629451L;

   private String mValue;
   private String mLang;
   private String mDatatype;

   public Literal(String value, String datatype)
   {
      this(value, "", datatype); //$NON-NLS-1$
   }

   public Literal(String value, String lang, String datatype)
   {
      mValue = value;
      mLang = lang;
      mDatatype = datatype;
   }

   @Override
   public String getLexicalValue()
   {
      return mValue;
   }

   @Override
   public String getDatatype()
   {
      return mDatatype;
   }

   @Override
   public String getLanguage()
   {
      return mLang;
   }

   @Override
   public Object getObject()
   {
      return XmlDataTypeProfile.getXmlDatatype(mDatatype).getValue(mValue);
   }
}
