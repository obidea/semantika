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
package com.obidea.semantika.queryanswer.internal;

public class QueryReturnMetadata
{
   private final String[] mReturnLabels;
   private final String[] mReturnTypes;

   public QueryReturnMetadata(String[] returnLabels, String[] returnTypes)
   {
      mReturnLabels = returnLabels;
      mReturnTypes = returnTypes;
   }

   /**
    * Retrieves the label of the designated column position in this projection.
    * 
    * @param position
    *           The first index position is 1, the second is 2, etc.
    * @return the projection label.
    */
   public String getReturnLabel(int position)
   {
      return mReturnLabels[position - 1];
   }

   public String[] getReturnLabels()
   {
      return mReturnLabels;
   }

   /**
    * Retrieves the datatype of the designated column position in this projection.
    * 
    * @param position
    *           The first index position is 1, the second is 2, etc.
    * @return the datatype string according to XML types.
    */
   public String getReturnType(int position)
   {
      return mReturnTypes[position - 1];
   }

   public String[] getReturnTypes()
   {
      return mReturnTypes;
   }

   public int getReturnSize()
   {
      return mReturnLabels.length;
   }
}
