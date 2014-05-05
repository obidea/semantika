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
package com.obidea.semantika.queryanswer;

import org.slf4j.Logger;

import com.obidea.semantika.app.IApplicationManager;
import com.obidea.semantika.app.Settings;
import com.obidea.semantika.knowledgebase.model.IKnowledgeBase;
import com.obidea.semantika.knowledgebase.model.KnowledgeBase;
import com.obidea.semantika.queryanswer.internal.ConnectionManager;
import com.obidea.semantika.queryanswer.internal.IQueryEvaluator;
import com.obidea.semantika.queryanswer.internal.QueryEvaluator;
import com.obidea.semantika.queryanswer.processor.EmptyRewriter;
import com.obidea.semantika.queryanswer.processor.IOptimizer;
import com.obidea.semantika.queryanswer.processor.IReformulator;
import com.obidea.semantika.queryanswer.processor.IUnfolder;
import com.obidea.semantika.queryanswer.processor.QueryReducer;
import com.obidea.semantika.queryanswer.processor.QueryUnfolder;
import com.obidea.semantika.util.LogUtils;

public abstract class AbstractQueryEngine implements IQueryEngineExt
{
   protected Settings mSettings;
   protected IKnowledgeBase mKnowledgeBase;

   protected static final Logger LOG = LogUtils.createLogger("semantika.queryanswer"); //$NON-NLS-1$

   public AbstractQueryEngine(final IApplicationManager manager)
   {
      mSettings = manager.getSettings();
      mKnowledgeBase = manager.getKnowledgeBase();
   }

   public KnowledgeBase getKnowledgeBase()
   {
      return (KnowledgeBase) mKnowledgeBase;
   }

   public Settings getSettings()
   {
      return mSettings;
   }

   public IReformulator getQueryReformulator()
   {
      IReformulator reformulator = new EmptyRewriter(getKnowledgeBase());
      LOG.debug("Registered reformulator class {}", reformulator.getClass().toString()); //$NON-NLS-1$
      return reformulator;
   }

   public IUnfolder getQueryUnfolder()
   {
      IUnfolder unfolder = new QueryUnfolder(getKnowledgeBase());
      LOG.debug("Registered unfolder class {}", unfolder.getClass().toString()); //$NON-NLS-1$
      return unfolder;
   }

   public IOptimizer getQueryOptimizers()
   {
      IOptimizer optimizer = new QueryReducer();
      LOG.debug("Registered optimizer class {}", optimizer.getClass().toString()); //$NON-NLS-1$
      return optimizer;
   }

   public IQueryEvaluator getQueryEvaluator()
   {
      /*
       * For every query service request, the QueryEngine will create a QueryEvaluator to evaluate
       * the query. Therefore, a QueryEngine can produce many QueryEvaluators, as many as user
       * service requests.
       */
      return createEvaluator(mSettings);
   }

   private IQueryEvaluator createEvaluator(Settings settings)
   {
      IQueryEvaluator evaluator = new QueryEvaluator(getConnectionManager());
      if (settings.getTransactionTimeout() != null) {
         evaluator.setTransactionTimeout(settings.getTransactionTimeout().intValue());
      }
      if (settings.getTransactionFetchSize() != null) {
         evaluator.setTransactionFetchSize(settings.getTransactionFetchSize().intValue());
      }
      if (settings.getTransactionMaxRows() != null) {
         evaluator.setTransactionMaxRows(settings.getTransactionMaxRows().intValue());
      }
      return evaluator;
   }

   protected abstract ConnectionManager getConnectionManager();
}
