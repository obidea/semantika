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

import com.obidea.semantika.exception.SemantikaException;
import com.obidea.semantika.queryanswer.IQueryEngineExt;
import com.obidea.semantika.queryanswer.QueryEngineException;
import com.obidea.semantika.queryanswer.result.IQueryResult;
import com.obidea.semantika.queryanswer.result.IQueryResultHandler;
import com.obidea.semantika.queryanswer.result.ListResultHandler;
import com.obidea.semantika.queryanswer.result.QueryResultHandlerException;

public class SelectQuery implements ISelectQuery
{
   private QueryModifiers mQueryModifiers = new QueryModifiers();
   private StatementSettings mStatementSettings = new StatementSettings();

   private String mQueryString;
   private IQueryEngineExt mQueryEngine;
   private QueryReturnMetadata mQueryReturnMetadata;

   public SelectQuery(String sparql, final IQueryEngineExt queryEngine, final QueryReturnMetadata queryMetadata)
   {
      mQueryString = sparql;
      mQueryEngine = queryEngine;
      mQueryReturnMetadata = queryMetadata;
   }

   @Override
   public String getQueryString()
   {
      return mQueryString;
   }

   @Override
   public QueryReturnMetadata getReturnMetadata()
   {
      return mQueryReturnMetadata;
   }

   @Override
   public QueryModifiers getModifiers()
   {
      return mQueryModifiers;
   }

   @Override
   public SelectQuery setMaxResults(int limit)
   {
      mQueryModifiers.setLimit(limit);
      return this;
   }

   @Override
   public SelectQuery setFirstResult(int offset)
   {
      mQueryModifiers.setOffset(offset);
      return this;
   }

   @Override
   public SelectQuery setAscendingOrder(String column)
   {
      mQueryModifiers.setAscendingOrder(column);
      return this;
   }

   @Override
   public SelectQuery setDescendingOrder(String column)
   {
      mQueryModifiers.setDescendingOrder(column);
      return this;
   }

   @Override
   public void setFetchSize(int fetchSize)
   {
      mStatementSettings.setFetchSize(fetchSize);
   }

   @Override
   public void setTimeout(int timeout)
   {
      mStatementSettings.setQueryTimeout(timeout);
   }

   @Override
   public void setMaxRows(int maxRows)
   {
      mStatementSettings.setMaxRows(maxRows);
   }

   @Override
   public IQueryResult evaluate() throws SemantikaException
   {
      return mQueryEngine.evaluate(getQueryString(), getQueryParameters());
   }

   private QueryParameters getQueryParameters()
   {
      return new QueryParameters(getReturnMetadata(), getModifiers(), mStatementSettings);
   }

   @Override
   public void evaluate(IQueryResultHandler handler) throws SemantikaException
   {
      try {
         IQueryResult result = evaluate();
         handler.start(result.getSelectNames());
         while (result.next()) {
            handler.handleResultFragment(result.getValueList());
         }
         handler.stop();
      }
      catch (QueryResultHandlerException e) {
         throw new QueryEngineException("Exception occured when handling query results", e); //$NON-NLS-1$
      }
   }

   @Override
   public List<Object> list() throws SemantikaException
   {
      ListResultHandler handler = new ListResultHandler();
      evaluate(handler);
      return handler.getListResult();
   }
}
