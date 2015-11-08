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
package com.obidea.semantika.queryanswer.internal;

public final class UserStatementSettings
{
   private Integer mQueryTimeout = null;
   private Integer mFetchSize = null;
   private Integer mMaxRows = null;

   public void setQueryTimeout(Integer timeout)
   {
      mQueryTimeout = timeout;
   }

   public Integer getQueryTimeout()
   {
      return mQueryTimeout;
   }

   public void setFetchSize(Integer fetchSize)
   {
      mFetchSize = fetchSize;
   }

   public Integer getFetchSize()
   {
      return mFetchSize;
   }

   public void setMaxRows(Integer maxRows)
   {
      mMaxRows = maxRows;
   }

   public Integer getMaxRows()
   {
      return mMaxRows;
   }
}
