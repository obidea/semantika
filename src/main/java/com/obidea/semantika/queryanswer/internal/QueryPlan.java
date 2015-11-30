/*
 * Copyright (c) 2013-2015 Obidea Technology
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

import com.obidea.semantika.queryanswer.AbstractQueryEngine;
import com.obidea.semantika.queryanswer.exception.QueryAnswerException;
import com.obidea.semantika.queryanswer.result.IQueryResult;

public class QueryPlan extends QueryResultLoader
{
   private String mQueryString;

   private QueryTranslator mTranslator;

   public QueryPlan(String queryString, AbstractQueryEngine queryEngine) throws QueryTranslationException
   {
      super(queryString, queryEngine);
      mQueryString = queryString;
      mTranslator = new QueryTranslator(queryString, queryEngine);
   }

   public String getQueryString()
   {
      return mQueryString;
   }

   @Override
   public String getSqlString()
   {
      return mTranslator.getSqlString();
   }

   @Override
   public QueryMetadata getQueryMetadata()
   {
      return mTranslator.getQueryMetadata();
   }

   public IQueryResult evaluateQuery(QueryModifiers modifiers, UserStatementSettings userSettings) throws QueryAnswerException
   {
      try {
         return super.evaluate(modifiers, userSettings);
      }
      catch (Exception e) {
         throw new QueryAnswerException(e);
      }
   }
}
