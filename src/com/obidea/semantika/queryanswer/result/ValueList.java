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

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ValueList implements IValueList
{
   private static final long serialVersionUID = 629451L;

   private List<String> mSelectNames;
   private List<IValue> mValues;

   public ValueList(List<String> selectNames, IValue... values)
   {
      this(selectNames, Arrays.asList(values));
   }

   public ValueList(List<String> selectNames, List<IValue> values)
   {
      assert selectNames.size() == values.size() : "Number of select names and values are not equal"; //$NON-NLS-1$
      mSelectNames = selectNames;
      mValues = values;
   }

   @Override
   public List<String> getSelectNames()
   {
      return Collections.unmodifiableList(mSelectNames);
   }

   @Override
   public IValue get(int position)
   {
      return mValues.get(position);
   }

   @Override
   public IValue get(String selectName)
   {
      int idx = mSelectNames.indexOf(selectName);
      if (idx != -1) {
         return mValues.get(idx);
      }
      return null;
   }

   @Override
   public boolean contains(String selectName)
   {
      return (get(selectName) != null) ? true : false;
   }

   @Override
   public int size()
   {
      return mValues.size();
   }

   @Override
   public Iterator<IValue> iterator()
   {
      return mValues.iterator();
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + mSelectNames.hashCode();
      result = prime * result + mValues.hashCode();
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
      final ValueList other = (ValueList) obj;
      return mSelectNames.equals(other.mSelectNames) && mValues == other.mValues;
   }

   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();
      
      sb.append("["); //$NON-NLS-1$
      boolean needSeparator = false;
      for (String name : mSelectNames) {
         if (needSeparator) {
            sb.append("; "); //$NON-NLS-1$
         }
         sb.append(name);
         sb.append("="); //$NON-NLS-1$
         sb.append(get(name));
         needSeparator = true;
      }
      sb.append("]"); //$NON-NLS-1$
      return sb.toString();
   }
}
