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
package com.obidea.semantika.database.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;

import com.obidea.semantika.app.Environment;
import com.obidea.semantika.exception.ConfigurationException;
import com.obidea.semantika.util.LogUtils;
import com.obidea.semantika.util.StringUtils;

/**
 * A connection provider using standard JDBC library (no pooling).
 */
public class JdbcConnectionProvider implements IConnectionProvider
{
   private String mUrl = ""; //$NON-NLS-1$
   private String mUser = ""; //$NON-NLS-1$
   private String mPassword = ""; //$NON-NLS-1$

   private static final Logger LOG = LogUtils.createLogger("semantika.database.connection"); //$NON-NLS-1$

   @Override
   public void configure(Properties properties) throws ConfigurationException
   {
      String driverClass = properties.getProperty(Environment.CONNECTION_DRIVER);
      if (StringUtils.isEmpty(driverClass)) {
         throw new ConfigurationException("JDBC driver is not specified in the configuration file."); //$NON-NLS-1$
      }

      try {
         Class.forName(driverClass);
      }
      catch (ClassNotFoundException e) {
         throw new ConfigurationException("JDBC driver class not found.", e); //$NON-NLS-1$
      }

      mUrl = properties.getProperty(Environment.CONNECTION_URL);
      if (StringUtils.isEmpty(mUrl)) {
         throw new ConfigurationException("JDBC URL is not specified in the configuration file."); //$NON-NLS-1$
      }

      mUser = properties.getProperty(Environment.CONNECTION_USERNAME);
      if (StringUtils.isEmpty(mUser)) {
         throw new ConfigurationException("JDBC user is not specified in the configuration file."); //$NON-NLS-1$
      }

      mPassword = properties.getProperty(Environment.CONNECTION_PASSWORD);
      if (StringUtils.isEmpty(mPassword)) {
         LOG.warn("An empty password is being used."); //$NON-NLS-1$
      }
      
      LOG.debug("Configuring {} mode.", getName()); //$NON-NLS-1$
   }

   @Override
   public Connection getConnection() throws SQLException
   {
      LOG.debug("Opening new JDBC connection."); //$NON-NLS-1$
      Connection conn = DriverManager.getConnection(mUrl, mUser, mPassword);
      conn.setAutoCommit(false); // always execute query in transaction block
      return conn;
   }

   @Override
   public void closeConnection(Connection conn) throws SQLException
   {
      LOG.debug("Closing JDBC connection."); //$NON-NLS-1$
      conn.close();
   }

   @Override
   public void close() throws SQLException
   {
      // NO-OP
   }

   @Override
   public String getName()
   {
      return "Single JDBC"; //$NON-NLS-1$
   }
}
