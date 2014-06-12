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

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.obidea.semantika.datatype.DataType;
import com.obidea.semantika.exception.SemantikaException;
import com.obidea.semantika.mapping.UriTemplateBuilder;
import com.obidea.semantika.queryanswer.AbstractQueryEngine;
import com.obidea.semantika.queryanswer.paging.SqlPaging;
import com.obidea.semantika.queryanswer.paging.SqlPagingStrategy;
import com.obidea.semantika.queryanswer.result.IQueryResult;
import com.obidea.semantika.queryanswer.result.QueryResult;
import com.obidea.semantika.queryanswer.result.QueryResultBuilder;
import com.obidea.semantika.queryanswer.result.Value;
import com.obidea.semantika.queryanswer.result.ValueList;

public abstract class QueryResultLoader
{
   protected AbstractQueryEngine mQueryEngine;

   public QueryResultLoader(final AbstractQueryEngine queryEngine)
   {
      mQueryEngine = queryEngine;
   }

   protected IQueryResult evaluate(QueryParameters queryParameters) throws SQLException, SemantikaException
   {
      String sql = preprocessSql(getSqlString(), queryParameters.getQueryModifiers());
      final PreparedStatement ps =  preparedStatement(sql, queryParameters.getStatementSettings());
      final ResultSet rs = doQuery(ps);
      
      IQueryResult result = null;
      try {
         result = buildQueryResult(rs, queryParameters.getQueryReturnMetadata());
      }
      finally {
         mQueryEngine.getQueryEvaluator().closeQueryStatement(ps, rs);
      }
      return result;
   }

   protected ResultSet doQuery(PreparedStatement ps) throws SQLException, SemantikaException
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

   protected abstract String getSqlString();

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

   private PreparedStatement preparedStatement(String sql, StatementSettings settings) throws SQLException, SemantikaException
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
      catch (SQLException e) {
         mQueryEngine.getQueryEvaluator().closeQueryStatement(ps, null);
         throw e;
      }
      catch (SemantikaException e) {
         mQueryEngine.getQueryEvaluator().closeQueryStatement(ps, null);
         throw e;
      }
      return ps;
   }

   private static QueryResult buildQueryResult(ResultSet rs, QueryReturnMetadata metadata) throws SQLException
   {
      QueryResultBuilder builder = new QueryResultBuilder();
      builder.start(Arrays.asList(metadata.getReturnLabels()));
      while (rs.next()) {
         ValueList valueList = getValueListFromResultSet(rs, metadata);
         builder.handleResultFragment(valueList);
      }
      return builder.getQueryResult();
   }

   private static ValueList getValueListFromResultSet(ResultSet rs, QueryReturnMetadata metadata) throws SQLException
   {
      List<String> selectLabels = new ArrayList<String>();
      List<Value> values = getSelectValues(rs, metadata, selectLabels);
      return new ValueList(selectLabels, values);
   }

   private static List<Value> getSelectValues(ResultSet rs, QueryReturnMetadata metadata, List<String> selectLabels) throws SQLException
   {
      int cols = metadata.getReturnSize();
      
      List<Value> values = new ArrayList<Value>();
      for (int i = 1; i <= cols; i++) {
         String name = getName(metadata, i);
         selectLabels.add(name);
         Value value = getValue(rs, metadata, i);
         values.add(value);
      }
      return values;
   }

   private static String getName(QueryReturnMetadata metadata, int position) throws SQLException
   {
      return metadata.getReturnLabel(position);
   }

   private static Value getValue(ResultSet resultSet, QueryReturnMetadata metadata, int position) throws SQLException
   {
      Object value = resultSet.getObject(position);
      String datatype = metadata.getReturnType(position);
      if (value != null) {
         if (datatype.equals(DataType.ANY_URI)) {
            String uriString = (String) value;
            if (!validUri(uriString)) {
               /*
                * We assume if the URI string is invalid it means the string is a
                * URI template construction, i.e., <template> : <value1> <value2> etc. 
                */
               uriString = UriTemplateBuilder.getUri(uriString);
            }
            value = URI.create(uriString);
         }
         return new Value(value, datatype);
      }
      else {
         return new Value(null, datatype); // if the database returns null then put null to Value object.
      }
   }

   private static boolean validUri(String uriString)
   {
      try {
         new URI(uriString);
      }
      catch (URISyntaxException e) {
         return false;
      }
      return true;
   }

   private SqlPaging getPaging()
   {
      return SqlPagingStrategy.buildPaging(mQueryEngine.getSettings().getDatabase().getDatabaseProduct());
   }
}
