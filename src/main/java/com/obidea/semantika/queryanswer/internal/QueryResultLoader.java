/*
 * Copyright (c) 2013-2015 Josef Hardi <josef.hardi@gmail.com>
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
import java.util.List;

import com.obidea.semantika.exception.SemantikaException;
import com.obidea.semantika.queryanswer.AbstractQueryEngine;
import com.obidea.semantika.queryanswer.internal.QueryMetadata.Column;
import com.obidea.semantika.queryanswer.paging.SqlPaging;
import com.obidea.semantika.queryanswer.paging.SqlPagingStrategy;
import com.obidea.semantika.queryanswer.result.IQueryResult;
import com.obidea.semantika.queryanswer.result.IValue;
import com.obidea.semantika.queryanswer.result.Literal;
import com.obidea.semantika.queryanswer.result.QueryResult;
import com.obidea.semantika.queryanswer.result.QueryResultBuilder;
import com.obidea.semantika.queryanswer.result.Uri;
import com.obidea.semantika.queryanswer.result.ValueArray;
import com.obidea.semantika.util.TemplateStringHelper;

public abstract class QueryResultLoader
{
   protected AbstractQueryEngine mQueryEngine;

   public QueryResultLoader(final AbstractQueryEngine queryEngine)
   {
      mQueryEngine = queryEngine;
   }

   protected IQueryResult evaluate(QueryModifiers modifiers, UserStatementSettings userSettings)
         throws SQLException, SemantikaException
   {
      String sql = preprocessSql(getSqlString(), modifiers);
      final PreparedStatement ps =  preparedStatement(sql, userSettings);
      final ResultSet rs = doQuery(ps);
      
      IQueryResult result = null;
      try {
         result = buildQueryResult(rs, getQueryMetadata());
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

   private PreparedStatement preparedStatement(String sql, UserStatementSettings settings) throws SQLException, SemantikaException
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

   private static QueryResult buildQueryResult(ResultSet rs, QueryMetadata metadata) throws SQLException
   {
      final List<String> selectLabels = new ArrayList<String>();
      QueryResultBuilder builder = new QueryResultBuilder();
      builder.start(metadata.getSelectNames());
      while (rs.next()) {
         ValueArray valueArray = getValueArrayFromResultSet(rs, metadata, selectLabels);
         builder.handleResultFragment(valueArray);
      }
      return builder.getQueryResult();
   }

   private static ValueArray getValueArrayFromResultSet(ResultSet rs, QueryMetadata metadata, List<String> selectLabels) throws SQLException
   {
      List<IValue> values = getSelectValues(rs, metadata, selectLabels);
      return new ValueArray(selectLabels, values);
   }

   private static List<IValue> getSelectValues(ResultSet rs, QueryMetadata metadata, List<String> selectLabels) throws SQLException
   {
      boolean needAdd = selectLabels.isEmpty();
      List<IValue> values = new ArrayList<IValue>();
      for (int i = 1; i <= metadata.size(); i++) {
         if (needAdd) {
            String label = getLabel(metadata, i);
            selectLabels.add(label);
         }
         IValue value = getValue(rs, metadata, i);
         values.add(value);
      }
      return values;
   }

   private static String getLabel(QueryMetadata metadata, int position) throws SQLException
   {
      return metadata.getColumn(position).getLabel();
   }

   private static IValue getValue(ResultSet resultSet, QueryMetadata metadata, int position) throws SQLException
   {
      String value = resultSet.getString(position);
      /*
       * If the JDBC ResultSet gives null, then this method returns null as well.
       */
      if (value == null) {
         return null;
      }
      
      Column c = metadata.getColumn(position);
      if (c.isLiteral()) {
         return new Literal(value, URI.create(c.getDatatype()));
      }
      else {
         if (!validUri(value)) {
            /*
             * We assume if the URI string is invalid it means the given value is a
             * URI template construction, i.e., <template> : <value1> <value2> etc. 
             */
            value = TemplateStringHelper.buildUri(value);
         }
         return new Uri(value);
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
      return SqlPagingStrategy.buildPaging(mQueryEngine.getTargetDatabase().getDatabaseProduct());
   }
}
