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

import java.net.URI;

import com.obidea.semantika.datatype.DataType;
import com.obidea.semantika.datatype.XmlDataTypeProfile;

public class Literal implements ILiteral
{
   private static final long serialVersionUID = 629451L;

   private String mValue;
   private String mLang;
   private String mDatatype;

   public Literal(String value, URI datatype)
   {
      mValue = value;
      mLang = ""; //$NON-NLS-1$
      mDatatype = datatype.toString();
   }

   public Literal(String value, String lang)
   {
      mValue = value;
      mLang = lang;
      mDatatype = DataType.PLAIN_LITERAL;
   }

   @Override
   public int getType()
   {
      return IValue.LITERAL;
   }

   @Override
   public String stringValue()
   {
      return mValue;
   }

   /**
    * Returns the XML datatype string.
    */
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

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + stringValue().hashCode();
      result = prime * result + getLanguage().hashCode();
      result = prime * result + getDatatype().hashCode();
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final Literal other = (Literal) obj;
      
      return stringValue().equals(other.stringValue()) && getLanguage().equals(other.getLanguage())
            && getDatatype().equals(other.getDatatype());
   }

   /*
    * Internal use only for debugging.
    */

   @Override
   public String toString()
   {
      final StringBuilder sb = new StringBuilder();
      if (mDatatype.equals(DataType.PLAIN_LITERAL)) {
         sb.append("\""); //$NON-NLS-1$
         sb.append(stringValue()).append("@").append(getLanguage()); //$NON-NLS-1$
         sb.append("\""); //$NON-NLS-1$
      }
      else if (mDatatype.equals(DataType.STRING)) {
         sb.append("\"").append(stringValue()).append("\""); //$NON-NLS-1$ //$NON-NLS-2$
      }
      else {
         sb.append("\"").append(stringValue()).append("\""); //$NON-NLS-1$ //$NON-NLS-2$
         sb.append("^^").append(XmlDataTypeProfile.getXmlDatatype(mDatatype)); //$NON-NLS-1$
      }
      return sb.toString();
   }
}
