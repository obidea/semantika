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
package com.obidea.semantika.queryanswer;

import org.slf4j.Logger;

import com.obidea.semantika.app.ApplicationManager;
import com.obidea.semantika.database.IDatabase;
import com.obidea.semantika.database.connection.IConnectionProvider;
import com.obidea.semantika.exception.SemantikaRuntimeException;
import com.obidea.semantika.knowledgebase.model.IKnowledgeBase;
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
   private ApplicationManager mAppManager;

   private IQueryEvaluator mQueryEvaluator;

   protected static final Logger LOG = LogUtils.createLogger("semantika.queryanswer"); //$NON-NLS-1$

   public AbstractQueryEngine(final ApplicationManager manager)
   {
      mAppManager = manager;
   }

   @Override
   public IKnowledgeBase getKnowledgeBase()
   {
      return mAppManager.getKnowledgeBase();
   }

   public IDatabase getTargetDatabase()
   {
      return mAppManager.getTargetDatabase();
   }

   public IConnectionProvider getConnectionProvider()
   {
      return mAppManager.getConnectionProvider();
   }

   /**
    * Returns a query reformulator object to expand the initial input query. This method will create
    * a new object in its call.
    */
   public IReformulator getQueryReformulator()
   {
      return createReformulator();
   }

   /**
    * Returns a query unfolder object to transform input query to database SQL query. This method
    * will create a new object in its call.
    */
   public IUnfolder getQueryUnfolder()
   {
      return createUnfolder();
   }

   /**
    * Returns a query optimizer object to optimize the database SQL query. This method will create
    * a new object in its call.
    */
   public IOptimizer getQueryOptimizers()
   {
      return createOptimizer();
   }

   /**
    * Returns a query evaluator that manages and executes each given SQL query.
    */
   public IQueryEvaluator getQueryEvaluator()
   {
      IQueryEvaluator evaluator = mQueryEvaluator;
      if (evaluator == null) {
         if (getConnectionManager() != null) {
            evaluator = createQueryEvaluator(getConnectionManager());
            evaluator.setTransactionTimeout(mAppManager.getSystemProperties().getTransactionTimeout());
            evaluator.setTransactionFetchSize(mAppManager.getSystemProperties().getTransactionFetchSize());
            evaluator.setTransactionMaxRows(mAppManager.getSystemProperties().getTransactionMaxRows());
         }
         else {
            throw new SemantikaRuntimeException("Failed to create query evaluator. Start the query engine first."); //$NON-NLS-1$
         }
      }
      return evaluator;
   }

   /**
    * Returns the connection manager used by this query engine.
    */
   protected abstract ConnectionManager getConnectionManager();

   /*
    * Private utility methods
    */

   private IReformulator createReformulator()
   {
      return new EmptyRewriter(getKnowledgeBase());
   }

   private IUnfolder createUnfolder()
   {
      return new QueryUnfolder(getKnowledgeBase());
   }

   private IOptimizer createOptimizer()
   {
      return new QueryReducer();
   }

   private IQueryEvaluator createQueryEvaluator(ConnectionManager connectionManager)
   {
      return new QueryEvaluator(connectionManager);
   }
}
