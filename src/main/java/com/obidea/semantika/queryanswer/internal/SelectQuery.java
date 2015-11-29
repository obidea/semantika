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

import java.util.List;

import com.obidea.semantika.queryanswer.SparqlQueryEngine;
import com.obidea.semantika.queryanswer.exception.QueryAnswerException;
import com.obidea.semantika.queryanswer.exception.QueryParserException;
import com.obidea.semantika.queryanswer.result.IQueryResult;
import com.obidea.semantika.queryanswer.result.IQueryResultHandler;
import com.obidea.semantika.queryanswer.result.ListResultHandler;
import com.obidea.semantika.queryanswer.result.QueryResultHandlerException;

public class SelectQuery extends QueryBase
{
   private QueryMetadata mQueryMetadata;

   public SelectQuery(String sparqlString, final SparqlQueryEngine engine, final QueryMetadata metadata)
         throws QueryParserException
   {
      super(sparqlString, engine);
      mQueryMetadata = metadata;
   }

   public QueryMetadata getProjection()
   {
      return mQueryMetadata;
   }

   public SelectQuery setMaxResults(int limit)
   {
      mQueryModifiers.setLimit(limit);
      return this;
   }

   public SelectQuery setFirstResult(int offset)
   {
      mQueryModifiers.setOffset(offset);
      return this;
   }

   public SelectQuery setAscendingOrder(String column)
   {
      mQueryModifiers.setAscendingOrder(column);
      return this;
   }

   public SelectQuery setDescendingOrder(String column)
   {
      mQueryModifiers.setDescendingOrder(column);
      return this;
   }

   public void setFetchSize(int fetchSize)
   {
      mUserStatementSettings.setFetchSize(fetchSize);
   }

   public void setTimeout(int timeout)
   {
      mUserStatementSettings.setQueryTimeout(timeout);
   }

   public void setMaxRows(int maxRows)
   {
      mUserStatementSettings.setMaxRows(maxRows);
   }

   @Override
   public IQueryResult evaluate() throws QueryAnswerException
   {
      return mQueryEngine.evaluate(getQueryString(), getModifiers(), getTransactionSettings());
   }

   public void evaluate(IQueryResultHandler handler) throws QueryAnswerException
   {
      try {
         IQueryResult result = evaluate();
         handler.start(result.getSelectNames());
         while (result.next()) {
            handler.handleResultFragment(result.getValueArray());
         }
         handler.stop();
      }
      catch (QueryResultHandlerException e) {
         throw new QueryEvaluationException("Exception occured when handling query results", e); //$NON-NLS-1$
      }
   }

   public List<Object[]> list() throws QueryAnswerException
   {
      ListResultHandler handler = new ListResultHandler();
      evaluate(handler);
      return handler.getListResult();
   }
}
