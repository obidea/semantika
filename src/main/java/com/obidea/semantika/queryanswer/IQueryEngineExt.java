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
package com.obidea.semantika.queryanswer;

import com.obidea.semantika.queryanswer.exception.QueryAnswerException;
import com.obidea.semantika.queryanswer.internal.QueryModifiers;
import com.obidea.semantika.queryanswer.internal.UserStatementSettings;
import com.obidea.semantika.queryanswer.result.IQueryResult;

/**
 * This interface extends the simple query engine that allows input query manipulation (i.e., by
 * assigning query modifiers and user query transaction settings)
 */
public interface IQueryEngineExt extends IQueryEngine
{
   /**
    * Evaluates the given input SPARQL query and returns the answer result. In addition, users can
    * insert some query modifiers or transaction settings.
    * 
    * @param sparql
    *           The input query in SPARQL language.
    * @param modifiers
    *           The query modifiers (e.g., limit, offset).
    * @param settings
    *           User settings for query transaction behavior (e.g., timeout, fetch size).
    * @return Returns the answer result.
    */
   IQueryResult evaluate(String sparql, QueryModifiers modifiers, UserStatementSettings settings)
         throws QueryAnswerException;
}
