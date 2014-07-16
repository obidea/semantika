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

import java.io.Serializable;
import java.net.URI;

import com.obidea.semantika.util.XmlUtils;

public class Uri implements IUri, Serializable
{
   private static final long serialVersionUID = 629451L;

   private String mValue;

   public Uri(String value)
   {
      mValue = value;
   }

   @Override
   public int getType()
   {
      return IValue.URI;
   }

   @Override
   public String stringValue()
   {
      return mValue;
   }

   @Override
   public String getNamespace()
   {
      return XmlUtils.getNCNamePrefix(mValue);
   }

   @Override
   public String getLocalName()
   {
      return XmlUtils.getNCNameSuffix(mValue);
   }

   @Override
   public URI getObject()
   {
      return java.net.URI.create(mValue);
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + stringValue().hashCode();
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
      final Uri other = (Uri) obj;
      
      return stringValue().equals(other.stringValue());
   }

   /*
    * Internal use only for debugging.
    */

   @Override
   public String toString()
   {
      return "<" + stringValue() + ">"; //$NON-NLS-1$ //$NON-NLS-2$
   }
}
