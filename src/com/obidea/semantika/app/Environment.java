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
package com.obidea.semantika.app;

import org.apache.commons.configuration.PropertiesConfiguration;

public final class Environment
{
   public static final String VERSION = "1.0.0"; //$NON-NLS-1$

   /**
    * Application factory name binding
    */
   public static final String APPLICATION_FACTORY_NAME = "application_factory_name"; //$NON-NLS-1$

   /**
    * Prefix for arbitrary JDBC connection properties
    */
   public static final String CONNECTION_PREFIX = "connection"; //$NON-NLS-1$

   /**
    * JDBC driver class
    */
   public static final String CONNECTION_DRIVER = "connection.driver_class"; //$NON-NLS-1$

   /**
    * JDBC url string
    */
   public static final String CONNECTION_URL = "connection.url"; //$NON-NLS-1$

   /**
    * JDBC user
    */
   public static final String CONNECTION_USERNAME = "connection.username"; //$NON-NLS-1$

   /**
    * JDBC password
    */
   public static final String CONNECTION_PASSWORD = "connection.password"; //$NON-NLS-1$

   /**
    * Initial size for connection pool
    */
   public static final String POOL_INITIAL_SIZE = "connection.pool.intial_size"; //$NON-NLS-1$

   /**
    * Minimum size of connection pool
    */
   public static final String POOL_MIN_SIZE = "connection.pool.min_size"; //$NON-NLS-1$

   /**
    * Maximum size of connection pool
    */
   public static final String POOL_MAX_SIZE = "connection.pool.max_size"; //$NON-NLS-1$

   /**
    * Maximum idle time for connection pool in seconds
    */
   public static final String POOL_TIMEOUT = "connection.pool.timeout"; //$NON-NLS-1$

   /**
    * Maximum timeout for the driver to wait for a query execution in seconds.
    */
   public static final String TRANSACTION_TIMEOUT = "transaction.timeout"; //$NON-NLS-1$

   /**
    * Suggested size of rows that should be fetched per one network call
    */
   public static final String TRANSACTION_FETCH_SIZE = "transaction.fetch_size"; //$NON-NLS-1$

   /**
    * Maximum size of rows that any ResultSet object should hold
    */
   public static final String TRANSACTION_MAX_ROWS = "transaction.max_rows"; //$NON-NLS-1$

   /**
    * SQL dialect class
    */
   public static final String DIALECT = "dialect"; //$NON-NLS-1$

   /**
    * Ontology resource location
    */
   public static final String ONTOLOGY_SOURCE = "ontology-source"; //$NON-NLS-1$

   /**
    * Mapping resource location
    */
   public static final String MAPPING_SOURCE = "mapping-source"; //$NON-NLS-1$

   /**
    * Allow strict parsing, i.e., using the ontology to check entity names in mappings
    */
   public static final String STRICT_PARSING = "strict_parsing"; //$NON-NLS-1$

   /**
    * Creates a new blank system properties
    */
   public static PropertiesConfiguration getProperties()
   {
      return new PropertiesConfiguration();
   }

   /**
    * Check the input <code>properties</code> used obsolete property names. This method
    * will issue warnings to the users to perform necessary fixes.
    * 
    * @param properties
    *           The target properties to check.
    */
   public static void verify(PropertiesConfiguration properties)
   {
      // NO-OP: This method is dedicated for future use if any obsolete names are used.
   }
}
