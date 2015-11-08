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
package com.obidea.semantika.database.sql.parser;

import com.obidea.semantika.database.IDatabaseMetadata;
import com.obidea.semantika.mapping.base.sql.SqlQuery;

public class SqlFactory
{
   public final static JSqlParser DEFAULT_SQL_PARSER = new JSqlParser();

   private IDatabaseMetadata mMetadata;

   public SqlFactory(IDatabaseMetadata metadata)
   {
      mMetadata = metadata;
   }

   public SqlQuery create(String sqlString) throws SqlParserException
   {
      return create(sqlString, DEFAULT_SQL_PARSER);
   }

   public SqlQuery create(String sqlString, String parserName) throws SqlParserException
   {
      ISqlParser parser = SqlParserRegistry.getInstance().lookup(parserName);
      return create(sqlString, parser);
   }

   public SqlQuery create(String sqlString, ISqlParser parser) throws SqlParserException
   {
      return (SqlQuery) parser.parse(sqlString, mMetadata);
   }
}
