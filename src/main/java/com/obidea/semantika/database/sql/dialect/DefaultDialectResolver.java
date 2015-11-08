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

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import com.obidea.semantika.database.DatabaseProduct;

public class DefaultDialectResolver
{
   /* package */IDialect getDialect(DatabaseMetaData metadata) throws SQLException
   {
      String databaseName = metadata.getDatabaseProductName();
//      int databaseMajorVersion = metadata.getDatabaseMajorVersion();
      String quoteString = metadata.getIdentifierQuoteString();
      String catalogSeparator = metadata.getCatalogSeparator();
      
      IDialect dialect = null;
      if (databaseName.equals(DatabaseProduct.H2)) {
         dialect = new H2Dialect();
         dialect.setQuoteString(quoteString);
         dialect.setSeparator(catalogSeparator);
      }
      if (databaseName.equals(DatabaseProduct.MYSQL)) {
         dialect = new MySqlDialect();
         dialect.setQuoteString(quoteString);
         dialect.setSeparator(catalogSeparator);
      }
      if (databaseName.equals(DatabaseProduct.PGSQL)) {
         dialect = new PostgreSqlDialect();
         dialect.setQuoteString(quoteString);
         dialect.setSeparator(catalogSeparator);
      }
      return dialect;
   }
}
