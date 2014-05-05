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
package com.obidea.semantika.queryanswer.processor;

import org.slf4j.Logger;

import com.obidea.semantika.expression.base.IQueryExt;
import com.obidea.semantika.expression.base.QuerySet;
import com.obidea.semantika.knowledgebase.model.KnowledgeBase;
import com.obidea.semantika.util.LogUtils;

public class EmptyRewriter implements IReformulator
{
   private static final Logger LOG = LogUtils.createLogger("semantika.queryanswer"); //$NON-NLS-1$

   public EmptyRewriter(KnowledgeBase kb)
   {
      // NO-OP
   }

   @Override
   public QuerySet<IQueryExt> reformulate(QuerySet<? extends IQueryExt> querySet) throws QueryReformulationException
   {
      LOG.debug("Expanding query..."); //$NON-NLS-1$
      QuerySet<IQueryExt> toReturn = new QuerySet<IQueryExt>();
      for (IQueryExt query : querySet.getAll()) {
         toReturn.copy(reformulate(query));
      }
      return toReturn;
   }

   @Override
   public QuerySet<IQueryExt> reformulate(IQueryExt query) throws QueryReformulationException
   {
      QuerySet<IQueryExt> toReturn = new QuerySet<IQueryExt>();
      toReturn.add(query);
      return toReturn;
   }

   @Override
   public String getName()
   {
      return EmptyRewriter.class.getCanonicalName(); //$NON-NLS-1$
   }
}
