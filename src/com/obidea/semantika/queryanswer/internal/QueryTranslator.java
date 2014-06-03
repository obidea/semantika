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

import java.sql.SQLException;

import com.obidea.semantika.database.sql.base.SqlSelectItem;
import com.obidea.semantika.database.sql.deparser.SqlDeparser;
import com.obidea.semantika.exception.SemantikaException;
import com.obidea.semantika.expression.base.IQueryExt;
import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.expression.base.QuerySet;
import com.obidea.semantika.mapping.base.sql.SqlQuery;
import com.obidea.semantika.queryanswer.AbstractQueryEngine;
import com.obidea.semantika.queryanswer.parser.SparqlFactory;
import com.obidea.semantika.queryanswer.parser.SparqlParserException;
import com.obidea.semantika.queryanswer.processor.QueryOptimizationException;
import com.obidea.semantika.queryanswer.processor.QueryReformulationException;
import com.obidea.semantika.queryanswer.processor.QueryUnfoldingException;
import com.obidea.semantika.queryanswer.result.IQueryResult;

public class QueryTranslator extends QueryResultLoader implements IQueryTranslator
{
   private String mQueryString;
   private String mSqlString;

   private String[] mReturnLabels;
   private String[] mReturnTypes;

   public QueryTranslator(String queryString, AbstractQueryEngine queryEngine) throws QueryTranslatorException
   {
      super(queryEngine);
      try {
         /*
          * Parse the SPARQL string into a set of query objects.
          */
         QuerySet<IQueryExt> parsedQuery = SparqlFactory.create(queryString);
         
         /*
          * Process the input query set by expanding it using a query reformulator.
          */
         QuerySet<IQueryExt> reformulatedQuery = applyQueryReformulation(parsedQuery);
         
         /*
          * Process the "expanded" query set by unfolding each query to a proper SQL query.
          */
         QuerySet<SqlQuery> unfoldedQuery = applyQueryUnfolding(reformulatedQuery);
         
         if (!unfoldedQuery.isEmpty()) {
            /*
             * Optimize the "unfolded" SQL query to increase the query performance.
             */
            unfoldedQuery = applyQueryOptimization(unfoldedQuery);
   
            /*
             * Translate the "unfolded" <code>QueryExt</code> objects into SQL string.
             */
            renderSql(unfoldedQuery);
            
            collectReturnLabels(unfoldedQuery.get(0)); // collect from one sample query
            collectReturnTypes(unfoldedQuery.get(0));
         }
         else {
            throw new QueryTranslatorException("No SQL was produced for input query:\n" + queryString); //$NON-NLS-1$
         }
      }
      catch (SparqlParserException e) {
         throw new QueryTranslatorException("Exception while parsing input query:\n" + queryString, e); //$NON-NLS-1$
      }
      catch (QueryReformulationException e) {
         throw new QueryTranslatorException("Exception while performing query reformulation:\n" + queryString, e); //$NON-NLS-1$
      }
      catch (QueryUnfoldingException e) {
         throw new QueryTranslatorException("Exception while performing query unfolding:\n" + queryString, e); //$NON-NLS-1$
      }
      catch (QueryOptimizationException e) {
         throw new QueryTranslatorException("Exception while performing query optimization:\n" + queryString, e); //$NON-NLS-1$
      }
   }

   private QuerySet<IQueryExt> applyQueryReformulation(QuerySet<IQueryExt> querySet) throws QueryReformulationException
   {
      return mQueryEngine.getQueryReformulator().reformulate(querySet);
   }
   
   private QuerySet<SqlQuery> applyQueryUnfolding(QuerySet<IQueryExt> querySet) throws QueryUnfoldingException
   {
      return mQueryEngine.getQueryUnfolder().unfold(querySet);
   }

   private QuerySet<SqlQuery> applyQueryOptimization(QuerySet<SqlQuery> querySet) throws QueryOptimizationException
   {
      return mQueryEngine.getQueryOptimizers().optimize(querySet);
   }

   private void renderSql(QuerySet<SqlQuery> inputQuery)
   {
      SqlDeparser deparser = new SqlDeparser(mQueryEngine.getSettings().getDialect());
      mSqlString = deparser.deparse(inputQuery);
   }

   private void collectReturnLabels(SqlQuery sqlQuery)
   {
      int returnSize = sqlQuery.getSelectItems().size();
      
      mReturnLabels = new String[returnSize];
      for (int i = 0; i < returnSize; i++) {
         SqlSelectItem selectItem = sqlQuery.getSelectItems().get(i);
         mReturnLabels[i] = selectItem.getLabelName();
      }
   }

   private void collectReturnTypes(SqlQuery sqlQuery)
   {
      int returnSize = sqlQuery.getDistTerms().size();
      
      mReturnTypes = new String[returnSize];
      for (int i = 0; i < returnSize; i++) {
         ITerm selectTerm = sqlQuery.getDistTerms().get(i);
         mReturnTypes[i] = selectTerm.getDatatype();
      }
   }

   @Override
   public String[] getReturnLabels()
   {
      return mReturnLabels;
   }

   @Override
   public String[] getReturnTypes()
   {
      return mReturnTypes;
   }

   @Override
   public String getQueryString()
   {
      return mQueryString;
   }

   @Override
   public String getSqlString()
   {
      return mSqlString;
   }

   @Override
   public IQueryResult evaluate(QueryParameters queryParameters) throws QueryTranslatorException
   {
      try {
         return super.evaluate(queryParameters);
      }
      catch (SQLException e) {
         throw new QueryTranslatorException(e);
      }
      catch (SemantikaException e) {
         throw new QueryTranslatorException(e);
      }
   }
}
