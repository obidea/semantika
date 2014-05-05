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
package com.obidea.semantika.mapping.sql.parser;

import com.obidea.semantika.database.IDatabaseMetadata;
import com.obidea.semantika.mapping.sql.SqlQuery;

public class SqlFactory
{
   public final static JSqlParser DEFAULT_SQL_PARSER = new JSqlParser();

   private IDatabaseMetadata mMetadata;

   public SqlFactory(IDatabaseMetadata metadata)
   {
      mMetadata = metadata;
   }

   public SqlQuery create(String sqlString) throws SqlMappingParserException
   {
      return create(sqlString, DEFAULT_SQL_PARSER);
   }

   public SqlQuery create(String sqlString, String parserName) throws SqlMappingParserException
   {
      SqlMappingParser parser = SqlMappingParserRegistry.getInstance().lookup(parserName);
      return create(sqlString, (SqlMappingParser) parser);
   }

   public SqlQuery create(String sqlString, SqlMappingParser parser) throws SqlMappingParserException
   {
      parser.setMetadata(mMetadata);
      return parser.parse(sqlString);
   }
}
