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
package com.obidea.semantika.queryanswer.paging;

import java.util.List;

public class DefaultPagingDialect implements IPagingDialect
{
   @Override
   public String paging(String sql, int limit, int offset, List<String> ascOrder, List<String> descOrder)
   {
      StringBuilder pagingQuery = new StringBuilder();
      return pagingQuery.append(baseSubQuery(sql))
            .append(getOrderBy(ascOrder, descOrder))
            .append(getLimit(limit))
            .append(getOffset(offset))
            .toString();
   }

   protected String baseSubQuery(String sql)
   {
      StringBuilder sb = new StringBuilder();
      sb.append("SELECT PAGING.* FROM ("); //$NON-NLS-1$
      sb.append("\n"); //$NON-NLS-1$
      sb.append("   "); //$NON-NLS-1$
      sb.append(indent(sql));
      sb.append("\n"); //$NON-NLS-1$
      sb.append(") AS PAGING"); //$NON-NLS-1$
      return sb.toString();
   }
   
   protected String getLimit(int limit)
   {
      String limitStr = ""; //$NON-NLS-1$
      if (limit > 0) {
         limitStr = "\n" + String.format("LIMIT %d", limit); //$NON-NLS-1$ //$NON-NLS-2$
      }
      return limitStr;
   }
   
   protected String getOffset(int offset)
   {
      String offsetStr = ""; //$NON-NLS-1$
      if (offset > 0) {
         offsetStr = "\n" + String.format("OFFSET %d", offset); //$NON-NLS-1$ //$NON-NLS-2$
      }
      return offsetStr;
   }
   
   protected String getOrderBy(List<String> ascOrder, List<String> descOrder)
   {
      String orderStr = ""; //$NON-NLS-1$
      StringBuilder orderBuilder = new StringBuilder();
      if (!ascOrder.isEmpty() || !descOrder.isEmpty()) {
         orderBuilder.append("ORDER BY"); //$NON-NLS-1$
         boolean needComma = false;
         for (String item : ascOrder) {
            if (needComma) {
               orderBuilder.append(", "); //$NON-NLS-1$
            }
            orderBuilder.append(item);
            orderBuilder.append(" ASC"); //$NON-NLS-1$
            needComma = true;
         }
         for (String item : descOrder) {
            if (needComma) {
               orderBuilder.append(", "); //$NON-NLS-1$
            }
            orderBuilder.append(item);
            orderBuilder.append(" DESC"); //$NON-NLS-1$
            needComma = true;
         }
         orderStr = "\n" + orderBuilder.toString(); //$NON-NLS-1$
      }
      return orderStr;
   }
   
   protected String indent(String sql)
   {
      return sql.replaceAll("\n", "\n   "); //$NON-NLS-1$ //$NON-NLS-2$
   }
}
