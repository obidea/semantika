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

import java.sql.Connection;
import java.sql.SQLException;

import com.obidea.semantika.exception.SemantikaException;

public class ConnectionManager
{
   private IDatabaseSession mSession;
   private Connection mConnection;

   private boolean mIsClosed = false;

   public ConnectionManager(IDatabaseSession session)
   {
      mSession = session;
   }

   public Connection getConnection() throws SemantikaException
   {
      if (mConnection == null) {
         openConnection();
      }
      return mConnection;
   }

   private void openConnection() throws ConnectionManagerException
   {
      if (mConnection != null) {
         return;
      }
      try {
         mConnection = mSession.getConnectionProvider().getConnection();
      }
      catch (SQLException e) {
         throw new ConnectionManagerException("Cannot open connection", e); //$NON-NLS-1$
      }
   }

   private void closeConnection() throws ConnectionManagerException
   {
      try {
         mSession.getConnectionProvider().closeConnection(mConnection);
         mConnection = null;
      }
      catch (SQLException e) {
         throw new ConnectionManagerException("Cannot release connection", e); //$NON-NLS-1$
      }
   }

   public void close() throws ConnectionManagerException
   {
      try {
         if (mConnection != null) {
            closeConnection();
         }
      }
      finally {
         mIsClosed = true;
      }
   }

   public boolean isClosed()
   {
      return mIsClosed;
   }
}
