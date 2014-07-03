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
package com.obidea.semantika.database.sql.parser;

import java.io.StringReader;

import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;

import org.slf4j.Logger;

import com.obidea.semantika.database.IDatabaseMetadata;
import com.obidea.semantika.database.exception.InternalDatabaseException;
import com.obidea.semantika.database.sql.base.ISqlQuery;
import com.obidea.semantika.exception.SemantikaRuntimeException;
import com.obidea.semantika.mapping.base.sql.SqlQuery;
import com.obidea.semantika.util.LogUtils;

public class JSqlParser implements ISqlParser
{
   private static final Logger LOG = LogUtils.createLogger("semantika.mapping.sqlparser"); //$NON-NLS-1$

   @Override
   public ISqlQuery parse(String sqlString, IDatabaseMetadata metadata) throws SqlParserException
   {
      final StringReader reader = new StringReader(sqlString);
      CCJSqlParser parser = new CCJSqlParser(reader);
      try {
         Statement stmt = parser.Statement();
         if (stmt instanceof Select) {
            Select ss = (Select) stmt;
            SelectStatementHandler ssh = new SelectStatementHandler(metadata);
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
         throw new SqlParserException("SQL syntax error", e); //$NON-NLS-1$
      }
      catch (SqlException e) {
         throw new SqlParserException("Invalid SQL", e); //$NON-NLS-1$
      }
      catch (InternalDatabaseException e) {
         throw new SqlParserException("Database metadata access error", e); //$NON-NLS-1$
      }
   }

   @Override
   public SqlQuery parse(String sqlString) throws SqlParserException
   {
      throw new SemantikaRuntimeException("SQL parser requires database metadata. " +
            "Use method parse(String, IDatabaseMetadata) instead"); //$NON-NLS-1$
   }

   @Override
   public String getName()
   {
      return "JSqlParser"; //$NON-NLS-1$
   }
}
