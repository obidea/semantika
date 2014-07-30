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

import java.util.List;

import com.obidea.semantika.queryanswer.exception.QueryAnswerException;
import com.obidea.semantika.queryanswer.result.IQueryResult;
import com.obidea.semantika.queryanswer.result.IQueryResultHandler;

public interface ISelectQuery
{
   String getQueryString();

   QueryModifiers getModifiers();

   ISelectQuery setMaxResults(int limit);

   ISelectQuery setFirstResult(int offset);

   ISelectQuery setAscendingOrder(String column);

   ISelectQuery setDescendingOrder(String column);

   void setFetchSize(int fetchSize);

   void setTimeout(int timeout);

   void setMaxRows(int maxRows);

   IQueryResult evaluate() throws QueryAnswerException;

   List<? extends Object> list() throws QueryAnswerException;

   void evaluate(IQueryResultHandler handler) throws QueryAnswerException;
}
