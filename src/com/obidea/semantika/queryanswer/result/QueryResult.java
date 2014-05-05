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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class QueryResult implements IQueryResult
{
   private static final long serialVersionUID = 629451L;

   private List<String> mSelectNames;
   private Iterator<? extends IValueList> mValueListsIter;

   private IValueList mValueList;

   public QueryResult(List<String> selectNames, Iterable<? extends IValueList> valueLists)
   {
      this(selectNames, valueLists.iterator());
   }
   
   public QueryResult(List<String> selectNames, Iterator<? extends IValueList> valueListsIter)
   {
      mSelectNames = selectNames;
      mValueListsIter = valueListsIter;
   }

   @Override
   public List<String> getSelectNames()
   {
      return Collections.unmodifiableList(mSelectNames);
   }

   @Override
   public IValueList getValueList()
   {
      return mValueList;
   }

   @Override
   public boolean next()
   {
      boolean hasNext = mValueListsIter.hasNext();
      if (hasNext) {
         mValueList = mValueListsIter.next();
      }
      return hasNext;
   }
}
