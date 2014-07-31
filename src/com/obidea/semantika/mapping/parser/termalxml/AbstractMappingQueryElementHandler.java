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
package com.obidea.semantika.mapping.parser.termalxml;

import com.obidea.semantika.mapping.base.sql.SqlQuery;

public abstract class AbstractMappingQueryElementHandler extends AbstractTermalElementHandler
{
   private SqlQuery mQuery;

   public AbstractMappingQueryElementHandler(TermalXmlParserHandler handler)
   {
      super(handler);
   }

   public SqlQuery getSourceQuery()
   {
      return mQuery;
   }

   protected void setSqlQuery(SqlQuery query)
   {
      mQuery = query;
   }

   protected abstract SqlQuery createQuery() throws Exception;

   @Override
   protected void handleChild(MappingElementHandler handler)
   {
      // NO-OP: No child node afterwards
   }

   @Override
   protected void handleChild(LogicalTableElementHandler handler)
   {
      // NO-OP: No child node afterwards
   }

   @Override
   protected void handleChild(SubjectMapElementHandler handler)
   {
      // NO-OP: No child node afterwards
   }

   @Override
   protected void handleChild(PredicateObjectMapElementHandler handler)
   {
      // NO-OP: No child node afterwards
   }
}
