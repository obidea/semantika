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

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.algebra.Slice;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.QueryParser;
import org.openrdf.query.parser.QueryParserUtil;

import com.obidea.semantika.exception.SemantikaException;
import com.obidea.semantika.queryanswer.QueryEngineException;
import com.obidea.semantika.queryanswer.SparqlQueryEngine;
import com.obidea.semantika.queryanswer.result.IQueryResult;
import com.obidea.semantika.queryanswer.result.IQueryResultHandler;
import com.obidea.semantika.queryanswer.result.ListResultHandler;
import com.obidea.semantika.queryanswer.result.QueryResultHandlerException;

public class SelectQuery implements ISelectQuery
{
   private static QueryParser sQueryValidator = QueryParserUtil.createParser(QueryLanguage.SPARQL);

   private QueryModifiers mQueryModifiers = new QueryModifiers();
   private UserStatementSettings mStatementSettings = new UserStatementSettings();

   private String mSparqlString;
   private SparqlQueryEngine mQueryEngine;
   private QueryMetadata mQueryMetadata;

   public SelectQuery(String sparql, final SparqlQueryEngine engine, final QueryMetadata metadata) throws SemantikaException
   {
      validateQuery(sparql);
      mSparqlString = sparql;
      mQueryEngine = engine;
      mQueryMetadata = metadata;
   }

   private void validateQuery(String sparqlString) throws SemantikaException
   {
      try {
         ParsedQuery query = sQueryValidator.parseQuery(sparqlString, null); // base URI is null
         
         /*
          * If the validation ok, do a quick scan on the query object to collect
          * any query modifers, if any.
          */
         addModifiersIfExist(query);
      }
      catch (MalformedQueryException e) {
         throw new SemantikaException(e.getMessage());
      }
   }

   private void addModifiersIfExist(ParsedQuery query)
   {
      TupleExpr expr = query.getTupleExpr();
      if (expr instanceof Slice) {
         Slice sliceExpr = (Slice) expr;
         mQueryModifiers.setLimit((int) sliceExpr.getLimit());
         mQueryModifiers.setOffset((int) sliceExpr.getOffset());
      }
   }

   public QueryMetadata getProjection()
   {
      return mQueryMetadata;
   }

   @Override
   public String getQueryString()
   {
      return mSparqlString;
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
      return new QueryParameters(getModifiers(), mStatementSettings);
   }

   @Override
   public void evaluate(IQueryResultHandler handler) throws SemantikaException
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
         throw new QueryEngineException("Exception occured when handling query results", e); //$NON-NLS-1$
      }
   }

   @Override
   public List<Object[]> list() throws SemantikaException
   {
      ListResultHandler handler = new ListResultHandler();
      evaluate(handler);
      return handler.getListResult();
   }
}
