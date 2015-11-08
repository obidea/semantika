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
package com.obidea.semantika.database.sql.dialect;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import com.obidea.semantika.exception.SemantikaRuntimeException;
import com.obidea.semantika.util.StringUtils;

public class DialectFactory
{
   private static DefaultDialectResolver sDialectResolver = new DefaultDialectResolver();

   public static IDialect buildDialect(String dialectName, Connection conn)
   {
      if (StringUtils.isEmpty(dialectName)) {
         return determineDialect(conn);
      }
      else {
         return buildDialect(dialectName);
      }
   }

   private static IDialect determineDialect(Connection conn)
   {
      if (conn == null) {
         throw new SemantikaRuntimeException("Failed to obtain DB connection"); //$NON-NLS-1$
      }
      try {
         final DatabaseMetaData metadata = conn.getMetaData();
         IDialect dialect = sDialectResolver.getDialect(metadata);
         if (dialect == null) {
            String msg = String.format(
                  "Unable to determine Dialect to use [productName=%s, majorVersion=%s]; " + //$NON-NLS-1$
                  "User can explicitly set 'dialect' in configuration", //$NON-NLS-1$
                  metadata.getDatabaseProductName(),
                  metadata.getDatabaseMajorVersion());
            throw new SemantikaRuntimeException(msg);
         }
         return dialect;
      }
      catch (SQLException e) {
         throw new SemantikaRuntimeException("Failed to determine proper Dialect to use", e); //$NON-NLS-1$
      }
   }

   private static IDialect buildDialect(String dialectName)
   {
      try {
         ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
         return (IDialect) contextClassLoader.loadClass(dialectName).newInstance();
      }
      catch (ClassNotFoundException e) {
         throw new SemantikaRuntimeException("Dialect class not found: " + dialectName);
      }
      catch (Exception e) {
         throw new SemantikaRuntimeException("Could not instantiate dialect class", e);
      }
   }
}
