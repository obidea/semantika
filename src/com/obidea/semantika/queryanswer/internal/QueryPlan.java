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

import com.obidea.semantika.exception.SemantikaException;
import com.obidea.semantika.queryanswer.AbstractQueryEngine;
import com.obidea.semantika.queryanswer.result.IQueryResult;

public class QueryPlan
{
   private String mQueryString;

   private IQueryTranslator mTranslator;
   private QueryReturnMetadata mReturnMetadata;

   public QueryPlan(String queryString, AbstractQueryEngine queryEngine) throws SemantikaException
   {
      mQueryString = queryString;
      mTranslator = new QueryTranslator(queryString, queryEngine);
      mReturnMetadata = new QueryReturnMetadata(mTranslator.getReturnLabels(), mTranslator.getReturnTypes());
   }

   public QueryReturnMetadata getReturnMetadata()
   {
      return mReturnMetadata;
   }

   public String getQueryString()
   {
      return mQueryString;
   }

   public String getSqlString()
   {
      return mTranslator.getSqlString();
   }

   public IQueryResult evaluateQuery(QueryParameters queryParameters) throws SemantikaException
   {
      return mTranslator.evaluate(queryParameters);
   }
}
