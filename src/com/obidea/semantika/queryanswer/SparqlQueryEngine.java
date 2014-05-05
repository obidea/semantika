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

import com.obidea.semantika.app.IApplicationManager;
import com.obidea.semantika.exception.SemantikaException;
import com.obidea.semantika.queryanswer.internal.ConnectionManager;
import com.obidea.semantika.queryanswer.internal.ConnectionManagerException;
import com.obidea.semantika.queryanswer.internal.DatabaseSession;
import com.obidea.semantika.queryanswer.internal.ISelectQuery;
import com.obidea.semantika.queryanswer.internal.QueryParameters;
import com.obidea.semantika.queryanswer.internal.QueryPlan;
import com.obidea.semantika.queryanswer.internal.SelectQuery;
import com.obidea.semantika.queryanswer.result.IQueryResult;

public class SparqlQueryEngine extends AbstractQueryEngine
{
   private ConnectionManager mConnectionManager;
   private DatabaseSession mSession = new DatabaseSession(this);

   public SparqlQueryEngine(final IApplicationManager manager)
   {
      super(manager);
   }

   @Override
   public void start() throws QueryEngineException
   {
      LOG.debug("Starting query engine."); //$NON-NLS-1$
      mConnectionManager = new ConnectionManager(mSession);
   }

   @Override
   public void stop() throws QueryEngineException
   {
      try {
         throwExceptionIfNull();
         LOG.debug("Stopping query engine."); //$NON-NLS-1$
         mConnectionManager.close();
      }
      catch (ConnectionManagerException e) {
         throw new QueryEngineException(e);
      }
      catch (SemantikaException e) {
         throw new QueryEngineException(e);
      }
   }

   @Override
   public boolean isStarted()
   {
      try {
         throwExceptionIfNull();
         return mConnectionManager.isClosed() ? false : true;
      }
      catch (SemantikaException e) {
         return false;
      }
   }

   @Override
   public ConnectionManager getConnectionManager()
   {
      return mConnectionManager;
   }

   @Override
   public String translate(String sparql) throws SemantikaException
   {
      QueryPlan plan = getQueryPlan(sparql);
      return plan.getSqlString();
   }

   @Override
   public IQueryResult evaluate(String sparql) throws SemantikaException
   {
      return createQuery(sparql).evaluate();
   }

   @Override
   public ISelectQuery createQuery(String sparql) throws SemantikaException
   {
      return new SelectQuery(sparql, this, getQueryPlan(sparql).getReturnMetadata());
   }

   @Override
   public IQueryResult evaluate(String sparql, QueryParameters queryParameters) throws SemantikaException
   {
      QueryPlan plan = getQueryPlan(sparql);
      IQueryResult results = plan.evaluateQuery(queryParameters);
      return results;
   }

   /*
    * Private utility methods
    */

   private QueryPlan getQueryPlan(String queryString) throws SemantikaException
   {
      return mSession.getQueryPlanCache().getQueryPlan(queryString);
   }

   private void throwExceptionIfNull() throws SemantikaException
   {
      if (mConnectionManager == null) {
         throw new SemantikaException("Call start() first to initialize query engine"); //$NON-NLS-1$
      }
   }
}
