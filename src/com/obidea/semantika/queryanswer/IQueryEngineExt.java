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
package com.obidea.semantika.queryanswer;

import com.obidea.semantika.exception.SemantikaException;
import com.obidea.semantika.queryanswer.internal.IQueryEvaluator;
import com.obidea.semantika.queryanswer.internal.ISelectQuery;
import com.obidea.semantika.queryanswer.internal.QueryParameters;
import com.obidea.semantika.queryanswer.result.IQueryResult;

public interface IQueryEngineExt extends IQueryEngine
{
   IQueryEvaluator getQueryEvaluator();

   ISelectQuery createQuery(String sparql) throws SemantikaException;

   IQueryResult evaluate(String queryString, QueryParameters queryParameters) throws SemantikaException;
}
