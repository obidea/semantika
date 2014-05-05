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

import java.util.ArrayList;
import java.util.List;

public class QueryResultBuilder implements IQueryResultHandler
{
   private List<String> mSelectNames;
   private List<IValueList> mValueLists = new ArrayList<IValueList>();

   @Override
   public void start(List<String> selectNames)
   {
      mSelectNames = selectNames;
   }

   @Override
   public void handleResultFragment(IValueList valueList)
   {
      mValueLists.add(valueList);
   }

   @Override
   public void stop()
   {
      // NO-OP
   }

   public QueryResult getQueryResult()
   {
      return new QueryResult(mSelectNames, mValueLists);
   }
}