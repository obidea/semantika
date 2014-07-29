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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;

import com.obidea.semantika.exception.SemantikaException;
import com.obidea.semantika.util.LogUtils;

public class QueryEvaluator implements IQueryEvaluator
{
   private ConnectionManager mConnectionManager;

   private Set<PreparedStatement> mStatementsToClose = new HashSet<PreparedStatement>();
   private Set<ResultSet> mResultSetsToClose = new HashSet<ResultSet>();

   private long mTransactionTimeout = -1;
   private int mTransactionFetchSize = -1;
   private int mTransactionMaxRows = -1;

   private boolean mIsTransactionTimeoutSet = false;
   private boolean mIsTransactionFetchSizeSet = false;
   private boolean mIsTransactionMaxRowsSet = false;

   private static final Logger LOG = LogUtils.createLogger("semantika.queryanswer"); //$NON-NLS-1$

   public QueryEvaluator(ConnectionManager connectionManager)
   {
      mConnectionManager = connectionManager;
   }

   @Override
   public PreparedStatement prepareQueryStatement(String sql) throws SQLException, SemantikaException
   {
      PreparedStatement ps = mConnectionManager.getConnection().prepareStatement(sql);
      setTimeout(ps);
      setFetchSize(ps);
      setMaxRows(ps);
      mStatementsToClose.add(ps);
      return ps;
   }

   private void setTimeout(PreparedStatement stmt) throws SQLException, QueryEvaluatorException
   {
      if (mIsTransactionTimeoutSet) {
         int timeout = (int) (mTransactionTimeout - (System.currentTimeMillis() / 1000));
         if (timeout <= 0) {
            throw new QueryEvaluatorException("Transaction timeout expired"); //$NON-NLS-1$
         }
         else {
            stmt.setQueryTimeout(timeout);
         }
      }
   }

   private void setFetchSize(PreparedStatement stmt) throws SQLException, QueryEvaluatorException
   {
      if (mIsTransactionFetchSizeSet) {
         if (mTransactionFetchSize <= 0) {
            throw new QueryEvaluatorException("Invalid fetch size: " + mTransactionFetchSize); //$NON-NLS-1$
         }
         else {
            stmt.setFetchSize(mTransactionFetchSize);
         }
      }
   }

   private void setMaxRows(PreparedStatement stmt) throws SQLException, QueryEvaluatorException
   {
      if (mIsTransactionMaxRowsSet) {
         if (mTransactionMaxRows <= 0) {
            throw new QueryEvaluatorException("Invalid max rows size: " + mTransactionMaxRows); //$NON-NLS-1$
         }
         else {
            stmt.setMaxRows(mTransactionMaxRows);
         }
      }
   }

   @Override
   public void closeQueryStatement(PreparedStatement ps, ResultSet rs) throws SQLException
   {
      mStatementsToClose.remove(ps);
      if (rs != null) {
         mResultSetsToClose.remove(rs);
      }
      try {
         if (rs != null) {
            rs.close();
         }
      }
      finally {
         closeQueryStatement(ps);
      }
   }

   private void closeQueryStatement(PreparedStatement ps) throws SQLException
   {
      try {
         if (ps.getMaxRows() != 0) {
            ps.setMaxRows(0);
         }
         if ( ps.getQueryTimeout() != 0 ) ps.setQueryTimeout(0);
      }
      catch (Exception e) {
         LOG.warn("Exception occurred when clearing max rows or query timeout", e); //$NON-NLS-1$
         return;
      }
      finally {
         closedPreparedStatement(ps);
      }
   }

   private void closedPreparedStatement(PreparedStatement ps) throws SQLException
   {
      ps.close();
   }

   @Override
   public ResultSet getResultSet(PreparedStatement ps) throws SQLException
   {
      ResultSet rs = ps.executeQuery();
      mResultSetsToClose.add(rs);
      return rs;
   }

   public void releaseResources()
   {
      Iterator<ResultSet> iter = mResultSetsToClose.iterator();
      while (iter.hasNext()) {
         try {
            iter.next().close();
         }
         catch (SQLException e) {
            LOG.warn("Could not close a JDBC result set", e); //$NON-NLS-1$
         }
      }
      mResultSetsToClose.clear();
      
      Iterator<PreparedStatement> iter2 = mStatementsToClose.iterator();
      while (iter2.hasNext()) {
         try {
            closeQueryStatement(iter2.next());
         }
         catch (SQLException e) {
            LOG.warn("Could not close a JDBC statement", e); //$NON-NLS-1$
         }
      }
      mStatementsToClose.clear();
   }

   @Override
   public void setTransactionTimeout(int seconds)
   {
      mIsTransactionTimeoutSet = true;
      mTransactionTimeout = System.currentTimeMillis() / 1000 + seconds;
   }

   @Override
   public void unsetTransactionTimeout()
   {
      mIsTransactionTimeoutSet = false;
   }

   @Override
   public void setTransactionFetchSize(int fetchSize)
   {
      mIsTransactionFetchSizeSet = true;
      mTransactionFetchSize = fetchSize;
   }

   @Override
   public void unsetTransactionFetchSize()
   {
      mIsTransactionFetchSizeSet = false;
   }

   @Override
   public void setTransactionMaxRows(int maxRows)
   {
      mIsTransactionMaxRowsSet = true;
      mTransactionMaxRows = maxRows;
   }

   @Override
   public void unsetTransactionMaxRows()
   {
      mIsTransactionMaxRowsSet = false;
   }

   public boolean hasOpenResources()
   {
      return mResultSetsToClose.size() > 0 || mStatementsToClose.size() > 0;
   }
}
