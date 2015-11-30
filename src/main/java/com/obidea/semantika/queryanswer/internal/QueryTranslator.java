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

import com.obidea.semantika.database.sql.base.SqlSelectItem;
import com.obidea.semantika.database.sql.deparser.SqlDeparser;
import com.obidea.semantika.database.sql.dialect.IDialect;
import com.obidea.semantika.expression.base.IQueryExt;
import com.obidea.semantika.expression.base.QuerySet;
import com.obidea.semantika.mapping.base.IMappingTerm;
import com.obidea.semantika.mapping.base.TermType;
import com.obidea.semantika.mapping.base.sql.SqlQuery;
import com.obidea.semantika.queryanswer.AbstractQueryEngine;
import com.obidea.semantika.queryanswer.parser.SparqlFactory;
import com.obidea.semantika.queryanswer.parser.SparqlParserException;
import com.obidea.semantika.queryanswer.processor.IOptimizer;
import com.obidea.semantika.queryanswer.processor.IReformulator;
import com.obidea.semantika.queryanswer.processor.IUnfolder;
import com.obidea.semantika.queryanswer.processor.QueryOptimizationException;
import com.obidea.semantika.queryanswer.processor.QueryReformulationException;
import com.obidea.semantika.queryanswer.processor.QueryUnfoldingException;

public class QueryTranslator implements IQueryTranslator
{
   private String mQueryString;
   private String mSqlString;
   private QueryMetadata mQueryMetadata;

   public QueryTranslator(String queryString, AbstractQueryEngine queryEngine) throws QueryTranslationException
   {
      mQueryString = queryString;
      try {
         /*
          * Parse the SPARQL string into a set of query objects.
          */
         IQueryExt parsedQuery = SparqlFactory.create(queryString);
         
         /*
          * Process the input query set by expanding it using a query reformulator.
          */
         QuerySet<IQueryExt> reformulatedQuery = applyQueryReformulation(parsedQuery, queryEngine.getQueryReformulator());
         
         /*
          * Process the "expanded" query set by unfolding each query to a proper SQL query.
          */
         QuerySet<SqlQuery> unfoldedQuery = applyQueryUnfolding(reformulatedQuery, queryEngine.getQueryUnfolder());
         
         if (!unfoldedQuery.isEmpty()) {
            /*
             * Optimize the "unfolded" SQL query to increase the query performance.
             */
            unfoldedQuery = applyQueryOptimization(unfoldedQuery, queryEngine.getQueryOptimizers());
   
            /*
             * Translate the "unfolded" <code>QueryExt</code> objects into SQL string.
             */
            renderSql(unfoldedQuery, queryEngine.getTargetDatabase().getDialect());
            
            /*
             * Construct the query meta-information from taking one query sample
             */
            buildQueryMetadata(unfoldedQuery.get(0));
         }
         else {
            throw new QueryTranslationException("No SQL was produced for input query:\n" + queryString); //$NON-NLS-1$
         }
      }
      catch (SparqlParserException e) {
         throw new QueryTranslationException("Exception while parsing input query:\n" + queryString, e); //$NON-NLS-1$
      }
      catch (QueryReformulationException e) {
         throw new QueryTranslationException("Exception while performing query reformulation:\n" + queryString, e); //$NON-NLS-1$
      }
      catch (QueryUnfoldingException e) {
         throw new QueryTranslationException("Exception while performing query unfolding:\n" + queryString, e); //$NON-NLS-1$
      }
      catch (QueryOptimizationException e) {
         throw new QueryTranslationException("Exception while performing query optimization:\n" + queryString, e); //$NON-NLS-1$
      }
   }

   private QuerySet<IQueryExt> applyQueryReformulation(IQueryExt originalQuery, IReformulator reformulator) throws QueryReformulationException
   {
      return reformulator.reformulate(originalQuery);
   }
   
   private QuerySet<SqlQuery> applyQueryUnfolding(QuerySet<IQueryExt> reformulatedQuery, IUnfolder unfolder) throws QueryUnfoldingException
   {
      return unfolder.unfold(reformulatedQuery);
   }

   private QuerySet<SqlQuery> applyQueryOptimization(QuerySet<SqlQuery> unfoldedQuery, IOptimizer optimizer) throws QueryOptimizationException
   {
      return optimizer.optimize(unfoldedQuery);
   }

   private void renderSql(QuerySet<SqlQuery> unfoldedQuery, IDialect sqlDialect)
   {
      SqlDeparser deparser = new SqlDeparser(sqlDialect);
      mSqlString = deparser.deparse(unfoldedQuery);
   }

   private void buildQueryMetadata(SqlQuery sqlQuery)
   {
      List<SqlSelectItem> selectItems = sqlQuery.getSelectItems();
      int selectSize = selectItems.size();
      
      String[] selectNames = new String[selectSize];
      String[] selectTypes = new String[selectSize];
      
      for (int i = 0; i < selectSize; i++) {
         final SqlSelectItem selectItem = selectItems.get(i);
         
         /*
          * Get the select name from the SQL projection labels.
          */
         selectNames[i] = selectItem.getLabelName();
         
         /*
          * Get the select type from the SQL expression. However, we need to
          * be able to determine if the expression has a semantic as a
          * literal-value or object-value. This can be obtained by casting the
          * object as a mapping term expression.
          */
         IMappingTerm mt = (IMappingTerm) selectItem.getExpression();
         switch (mt.getTermType()) {
            case TermType.LITERAL_TYPE: selectTypes[i] = mt.getDatatype(); break;
            case TermType.IRI_TYPE: selectTypes[i] = null; break;
         }
      }
      mQueryMetadata = new QueryMetadata(selectNames, selectTypes);
   }

   public QueryMetadata getQueryMetadata()
   {
      return mQueryMetadata;
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
}
