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

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;

import com.obidea.semantika.queryanswer.internal.QueryMetadata.Column;
import com.obidea.semantika.queryanswer.result.IValue;
import com.obidea.semantika.queryanswer.result.Literal;
import com.obidea.semantika.queryanswer.result.QueryResult;
import com.obidea.semantika.queryanswer.result.Uri;
import com.obidea.semantika.queryanswer.result.ValueArray;
import com.obidea.semantika.util.LogUtils;
import com.obidea.semantika.util.TemplateStringHelper;

/**
 * @author Josef Hardi <josef.hardi@gmail.com>
 * @since 1.8
 */
public class TupleResultBuilder implements IQueryResultBuilder
{
   protected static final Logger LOG = LogUtils.createLogger("semantika.queryanswer"); //$NON-NLS-1$

   @Override
   public QueryResult buildQueryResult(ResultSet resultSet, QueryMetadata metadata)
   {
      QueryResult.Builder tuplesBuilder = new QueryResult.Builder();
      try {
         while (resultSet.next()) {
            ValueArray valueArray = getValueArrayFromResultSet(resultSet, metadata);
            tuplesBuilder.add(valueArray);
         }
      }
      catch (SQLException e) {
         LOG.error("Error while fetching database row: {}", e.getMessage());
      }
      return tuplesBuilder.build();
   }

   private ValueArray getValueArrayFromResultSet(ResultSet rs, QueryMetadata metadata) throws SQLException
   {
      ValueArray.Builder arrayBuilder = new ValueArray.Builder();
      for (int i = 1; i <= metadata.size(); i++) {
         String label = getLabel(metadata, i);
         IValue value = getValue(rs, metadata, i);
         arrayBuilder.put(label, value);
      }
      return arrayBuilder.build();
   }

   private String getLabel(QueryMetadata metadata, int position) throws SQLException
   {
      return metadata.getColumn(position).getLabel();
   }

   private IValue getValue(ResultSet resultSet, QueryMetadata metadata, int position) throws SQLException
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
}