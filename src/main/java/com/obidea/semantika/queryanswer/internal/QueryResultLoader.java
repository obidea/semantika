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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.obidea.semantika.queryanswer.AbstractQueryEngine;
import com.obidea.semantika.queryanswer.paging.SqlPaging;
import com.obidea.semantika.queryanswer.paging.SqlPagingStrategy;
import com.obidea.semantika.queryanswer.result.IQueryResult;
import com.obidea.semantika.util.QueryUtils;

public abstract class QueryResultLoader
{
   protected AbstractQueryEngine mQueryEngine;

   private IQueryResultBuilder mResultBuilder = new EmptyResultBuilder();

   public QueryResultLoader(String queryString, final AbstractQueryEngine queryEngine)
   {
      mQueryEngine = queryEngine;
      setQueryResultBuilder(queryString);
   }

   private void setQueryResultBuilder(String queryString)
   {
      if (QueryUtils.isSelectQuery(queryString)) {
         mResultBuilder = new TupleResultBuilder();
      }
      else if (QueryUtils.isConstructQuery(queryString)) {
         mResultBuilder = new GraphResultBuilder();
      }
   }

   protected IQueryResult evaluate(QueryModifiers modifiers, UserStatementSettings userSettings) throws Exception
   {
      String sql = preprocessSql(getSqlString(), modifiers);
      final PreparedStatement ps =  preparedStatement(sql, userSettings);
      final ResultSet resultSet = doQuery(ps);
      
      IQueryResult result = null;
      try {
         result = mResultBuilder.buildQueryResult(resultSet, getQueryMetadata());
      }
      finally {
         mQueryEngine.getQueryEvaluator().closeQueryStatement(ps, resultSet);
      }
      return result;
   }

   protected ResultSet doQuery(PreparedStatement ps) throws Exception
   {
      ResultSet rs = null;
      try {
         rs = mQueryEngine.getQueryEvaluator().getResultSet(ps);
      }
      catch (SQLException e) {
         mQueryEngine.getQueryEvaluator().closeQueryStatement(ps, rs);
         throw e;
      }
      return rs;
   }

   /**
    * Returns the produced SQL query string.
    */
   protected abstract String getSqlString();

   /**
    * Returns the query metadata to rebuild the result header (i.e., column names).
    */
   protected abstract QueryMetadata getQueryMetadata();

   /*
    * Private utility methods
    */

   private String preprocessSql(String sql, QueryModifiers modifiers)
   {
      if (modifiers.isSet()) {
         int limit = modifiers.getLimit();
         int offset = modifiers.getOffset();
         final List<String> ascOrder = modifiers.getAscendingOrder();
         final List<String> descOrder = modifiers.getDescendingOrder();
         SqlPaging paging = getPaging();
         sql = paging.createPaging(sql, limit, offset, ascOrder, descOrder);
      }
      return sql;
   }

   private PreparedStatement preparedStatement(String sql, UserStatementSettings settings) throws Exception
   {
      PreparedStatement ps = null;
      try {
         ps = mQueryEngine.getQueryEvaluator().prepareQueryStatement(sql);
         
         /*
          * These settings come from user code in SelectQuery class and they will override the
          * global JDBC statement parameters in the configuration file, if any.
          */
         if (settings.getQueryTimeout() != null) {
            ps.setQueryTimeout(settings.getQueryTimeout().intValue());
         }
         if (settings.getFetchSize() != null) {
            ps.setFetchSize(settings.getFetchSize().intValue());
         }
         if (settings.getMaxRows() != null) {
            ps.setMaxRows(settings.getMaxRows().intValue());
         }
      }
      catch (Exception e) {
         mQueryEngine.getQueryEvaluator().closeQueryStatement(ps, null);
         throw e;
      }
      return ps;
   }

   private SqlPaging getPaging()
   {
      return SqlPagingStrategy.buildPaging(mQueryEngine.getTargetDatabase().getDatabaseProduct());
   }
}
