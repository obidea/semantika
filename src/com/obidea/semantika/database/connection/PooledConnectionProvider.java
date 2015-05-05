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
package com.obidea.semantika.database.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;

import com.mchange.v2.c3p0.DataSources;
import com.obidea.semantika.app.Environment;
import com.obidea.semantika.exception.ConfigurationException;
import com.obidea.semantika.util.LogUtils;
import com.obidea.semantika.util.StringUtils;

/**
 * A connection provider using C3P0 connection pool.
 */
public class PooledConnectionProvider implements IConnectionProvider
{
   private String mUrl = ""; //$NON-NLS-1$
   private String mUser = ""; //$NON-NLS-1$
   private String mPassword = ""; //$NON-NLS-1$

   private DataSource mDataSource;

   private static final String C3P0_MAX_POOL_SIZE = "c3p0.maxPoolSize"; //$NON-NLS-1$
   private static final String C3P0_MIN_POOL_SIZE = "c3p0.minPoolSize"; //$NON-NLS-1$
   private static final String C3P0_TIMEOUT = "c3p0.maxIdleTime"; //$NON-NLS-1$
   private static final String C3P0_INITIAL_POOL_SIZE = "c3p0.initialPoolSize"; //$NON-NLS-1$

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
         throw new ConfigurationException("JDBC password is not specified in the configuration file."); //$NON-NLS-1$
      }
      
      try {
         Properties c3p0Properties = new Properties();
         setC3P0Properties(Environment.POOL_MAX_SIZE, C3P0_MAX_POOL_SIZE, properties, c3p0Properties);
         setC3P0Properties(Environment.POOL_MIN_SIZE, C3P0_MIN_POOL_SIZE, properties, c3p0Properties);
         setC3P0Properties(Environment.POOL_TIMEOUT, C3P0_TIMEOUT, properties, c3p0Properties);
         setC3P0Properties(Environment.POOL_INITIAL_SIZE, C3P0_INITIAL_POOL_SIZE, properties, c3p0Properties);
         
         DataSource unpooled = DataSources.unpooledDataSource(mUrl, mUser, mPassword);
         mDataSource = DataSources.pooledDataSource(unpooled, c3p0Properties);
      }
      catch (SQLException e) {
         throw new ConfigurationException("Failed to create connection pool", e); //$NON-NLS-1$
      }
      
      LOG.debug("Configuring {} mode", getName()); //$NON-NLS-1$
   }

   @Override
   public Connection getConnection() throws SQLException
   {
      LOG.debug("Taking connection from the pool."); //$NON-NLS-1$
      return mDataSource.getConnection();
   }

   @Override
   public void closeConnection(Connection conn) throws SQLException
   {
      LOG.debug("Returning connection to the pool."); //$NON-NLS-1$
      conn.close();
   }

   @Override
   public void close() throws SQLException
   {
      LOG.debug("Closing connection pool."); //$NON-NLS-1$
      DataSources.destroy(mDataSource);
   }

   @Override
   public String getName()
   {
      return "C3P0 pool"; //$NON-NLS-1$
   }

   private void setC3P0Properties(String obdaKey, String c3p0Key, Properties obdaProperties, Properties c3p0Properties)
   {
      String value = obdaProperties.getProperty(obdaKey);
      if (!StringUtils.isEmpty(value)) {
         c3p0Properties.put(c3p0Key, value);
      }
   }
}
