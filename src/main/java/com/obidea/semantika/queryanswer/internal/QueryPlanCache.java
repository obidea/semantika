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

import com.obidea.semantika.queryanswer.SparqlQueryEngine;
import com.obidea.semantika.util.SoftMruCache;

public class QueryPlanCache
{
   private SparqlQueryEngine mQueryEngine;

   private final SoftMruCache mQueryPlanCache = new SoftMruCache();

   public QueryPlanCache(SparqlQueryEngine queryEngine)
   {
      mQueryEngine = queryEngine;
   }

   public QueryPlan getQueryPlan(String queryString) throws QueryTranslationException
   {
      QueryPlan plan = (QueryPlan) mQueryPlanCache.get(queryString);
      if (plan == null) {
         plan = new QueryPlan(queryString, mQueryEngine);
      }
      mQueryPlanCache.put(queryString, plan);
      return plan;
   }
}
