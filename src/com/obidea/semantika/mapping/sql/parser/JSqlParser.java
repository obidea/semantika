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

import java.io.StringReader;

import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;

import org.slf4j.Logger;

import com.obidea.semantika.database.IDatabaseMetadata;
import com.obidea.semantika.database.exception.InternalDatabaseException;
import com.obidea.semantika.mapping.sql.SqlQuery;
import com.obidea.semantika.util.LogUtils;

public class JSqlParser extends SqlMappingParser
{
   private IDatabaseMetadata mMetadata;

   private static final Logger LOG = LogUtils.createLogger("semantika.mapping.sqlparser"); //$NON-NLS-1$

   public JSqlParser()
   {
      super("jsqlparser"); //$NON-NLS-1$
   }

   @Override
   public void setMetadata(IDatabaseMetadata metadata)
   {
      mMetadata = metadata;
   }

   @Override
   public SqlQuery parse(String sqlString) throws SqlMappingParserException
   {
      final StringReader reader = new StringReader(sqlString);
      CCJSqlParser parser = new CCJSqlParser(reader);
      try {
         Statement stmt = parser.Statement();
         if (stmt instanceof Select) {
            Select ss = (Select) stmt;
            SelectStatementHandler ssh = new SelectStatementHandler(mMetadata);
            return ssh.parse(ss);
         }
         else {
            throw new UnsupportedSqlExpressionException("Only SELECT statement is valid"); //$NON-NLS-1$
         }
      }
      catch (UnsupportedSqlExpressionException e) {
         LOG.warn("Creating UserQuery object: " + e.getMessage()); //$NON-NLS-1$
         UserQueryHandler handler = new UserQueryHandler();
         return handler.parse(sqlString);
      }
      catch (ParseException e) {
         throw new SqlMappingParserException("SQL syntax error", e); //$NON-NLS-1$
      }
      catch (SqlException e) {
         throw new SqlMappingParserException("Invalid SQL", e); //$NON-NLS-1$
      }
      catch (InternalDatabaseException e) {
         throw new SqlMappingParserException("Database metadata access error", e); //$NON-NLS-1$
      }
   }
}
