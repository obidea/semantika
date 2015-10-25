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
package com.obidea.semantika.queryanswer.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QueryMetadata
{
   private List<String> mSelectNames = new ArrayList<String>();
   private List<Column> mColumnList = new ArrayList<Column>();

   public QueryMetadata(String[] selectNames, String[] selectTypes)
   {
      mSelectNames.addAll(Arrays.asList(selectNames));
      for (int i = 0; i < selectNames.length; i++) {
         mColumnList.add(new Column(selectNames[i], selectTypes[i]));
      }
   }

   public List<String> getSelectNames()
   {
      return Collections.unmodifiableList(mSelectNames);
   }

   /**
    * Retrieves the column meta-info of the designated position in query projection.
    * 
    * @param position
    *           The first index position is 1, the second is 2, etc.
    */
   public Column getColumn(int position)
   {
      return mColumnList.get(position - 1);
   }

   public int size()
   {
      return mSelectNames.size();
   }

   class Column
   {
      private String mLabel;
      private String mDatatype;
      
      public Column(String label, String datatype)
      {
         mLabel = label;
         mDatatype = datatype;
      }
      
      public String getLabel()
      {
         return mLabel;
      }
      
      public String getDatatype()
      {
         return mDatatype;
      }
      
      public boolean isLiteral()
      {
         return (mDatatype != null) ? true : false;
      }
   }
}
