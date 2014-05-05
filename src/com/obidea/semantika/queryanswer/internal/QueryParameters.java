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
package com.obidea.semantika.queryanswer.internal;

public final class QueryParameters
{
   private QueryReturnMetadata mQueryReturnMetadata;
   private QueryModifiers mQueryModifiers;
   private StatementSettings mStatementSettings;

   public QueryParameters(QueryReturnMetadata metadata)
   {
      this(metadata, new QueryModifiers(), new StatementSettings());
   }

   public QueryParameters(QueryReturnMetadata metadata, QueryModifiers modifiers, StatementSettings settings)
   {
      mQueryReturnMetadata = metadata;
      mQueryModifiers = modifiers;
      mStatementSettings = settings;
   }

   public QueryReturnMetadata getQueryReturnMetadata()
   {
      return mQueryReturnMetadata;
   }

   public QueryModifiers getQueryModifiers()
   {
      return mQueryModifiers;
   }

   public StatementSettings getStatementSettings()
   {
      return mStatementSettings;
   }
}