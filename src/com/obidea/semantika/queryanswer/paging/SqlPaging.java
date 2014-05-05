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
package com.obidea.semantika.queryanswer.paging;

import java.util.List;

public class SqlPaging
{
   private IPagingDialect mDialect;

   public SqlPaging()
   {
      mDialect = new DefaultPagingDialect();
   }

   public void setDialect(IPagingDialect dialect)
   {
      mDialect = dialect;
   }

   public String createPaging(String sql, int limit, int offset, List<String> ascOrder, List<String> descOrder)
   {
      return mDialect.paging(sql, limit, offset, ascOrder, descOrder);
   }
}
