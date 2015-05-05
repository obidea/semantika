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
package com.obidea.semantika.queryanswer.paging;

import com.obidea.semantika.database.DatabaseProduct;

public class SqlPagingStrategy
{
   public static SqlPaging buildPaging(String databaseProduct)
   {
      SqlPaging paging = new SqlPaging();
      if (databaseProduct.equals(DatabaseProduct.MYSQL)) { //$NON-NLS-1$
         MySqlPagingDialect dialect = new MySqlPagingDialect();
         paging.setDialect(dialect);
      }
      else if (databaseProduct.equals(DatabaseProduct.PGSQL)) { //$NON-NLS-1$
         PostgreSqlPagingDialect dialect = new PostgreSqlPagingDialect();
         paging.setDialect(dialect);
      }
      else if (databaseProduct.equals(DatabaseProduct.H2)) { //$NON-NLS-1$
         H2PagingDialect dialect = new H2PagingDialect();
         paging.setDialect(dialect);
      }
      return paging;
   }
}
