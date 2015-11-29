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
package com.obidea.semantika.queryanswer.parser;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.QueryParser;
import org.openrdf.query.parser.QueryParserUtil;

import com.obidea.semantika.expression.base.IQueryExt;

public class SparqlParser extends AbstractSparqlParser
{
   public SparqlParser()
   {
      super("Sesame query model parser"); //$NON-NLS-1$
   }

   @Override
   public IQueryExt parse(String sparqlString) throws SparqlParserException
   {
      try {
         QueryParser parser = QueryParserUtil.createParser(QueryLanguage.SPARQL);
         ParsedQuery query = parser.parseQuery(sparqlString, null); // base URI is null
         SparqlQueryHandler handler = new SparqlQueryHandler();
         TupleExpr expr = query.getTupleExpr();
         expr.visit(handler);
         return handler.getQueryExpression();
      }
      catch (MalformedQueryException e) {
         throw new SparqlParserException("SPARQL syntax error", e);
      }
   }
}
