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

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.algebra.Slice;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.QueryParser;
import org.openrdf.query.parser.QueryParserUtil;

import com.obidea.semantika.queryanswer.SparqlQueryEngine;
import com.obidea.semantika.queryanswer.exception.QueryParserException;

/**
 * @author Josef Hardi <josef.hardi@gmail.com>
 * @since 1.8
 */
public abstract class QueryBase implements IQuery
{
   private static QueryParser sQueryValidator = QueryParserUtil.createParser(QueryLanguage.SPARQL);

   protected QueryModifiers mQueryModifiers = new QueryModifiers();
   protected UserStatementSettings mUserStatementSettings = new UserStatementSettings();

   protected String mSparqlString;
   protected SparqlQueryEngine mQueryEngine;

   public QueryBase(String sparqlString, final SparqlQueryEngine engine) throws QueryParserException
   {
      validateQuery(sparqlString);
      mSparqlString = sparqlString;
      mQueryEngine = engine;
   }

   private void validateQuery(String sparqlString) throws QueryParserException
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
         throw new QueryParserException(e.getMessage());
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

   public String getQueryString()
   {
      return mSparqlString;
   }

   public QueryModifiers getModifiers()
   {
      return mQueryModifiers;
   }

   public UserStatementSettings getTransactionSettings()
   {
      return mUserStatementSettings;
   }
}
