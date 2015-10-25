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

public abstract class AbstractXmlType<T> implements IDatatype<T>
{
   private final String mName;

   protected AbstractXmlType(String name)
   {
      if (name == null) {
         throw new NullPointerException();
      }
      mName = name;
   }

   public String getName()
   {
      return mName;
   }

   @Override
   public boolean equals(Object obj)
   {
      // Note that this implementation assumes singleton classes for each datatype
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      return true;
   }
   
   @Override
   public int hashCode()
   {
      return mName.hashCode();
   }

   public abstract int getType();
}
