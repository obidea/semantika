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
package com.obidea.semantika.mapping.base.sql.parser;

import com.obidea.semantika.database.IDatabaseMetadata;
import com.obidea.semantika.database.sql.parser.ISqlParser;
import com.obidea.semantika.mapping.base.sql.SqlQuery;

public abstract class SqlMappingParser implements ISqlParser
{
   private String mName;

   public SqlMappingParser(String name)
   {
      mName = name;
   }

   @Override
   public String getName()
   {
      return mName;
   }

   @Override
   public SqlQuery parse(String sqlString) throws SqlMappingParserException
   {
      return null; // NO-OP: Implementation by subclasses
   }

   public abstract void setMetadata(IDatabaseMetadata metadata);
}
