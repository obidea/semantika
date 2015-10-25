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
package com.obidea.semantika.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.obidea.semantika.util.StringUtils;

public class SystemProperties extends Properties
{
   private static final long serialVersionUID = 629451L;

   /**
    * Returns the application identifier.
    */
   public String getApplicationName()
   {
      String name = getProperty(Environment.APPLICATION_FACTORY_NAME);
      if (StringUtils.isEmpty(name)) {
         return "semantika"; //$NON-NLS-1$
      }
      return name;
   }

   /**
    * Returns the database connection string.
    */
   public String getConnectionUrl()
   {
      return getProperty(Environment.CONNECTION_URL);
   }

   /**
    * Returns the connection pool initial size when pooled connection is used.
    * If instead a single JDBC connection is used then this method will return -1.
    */
   public int getPoolInitialSize()
   {
      return Integer.parseInt(getProperty(Environment.POOL_INITIAL_SIZE));
   }

   /**
    * Returns the connection pool minimum size when pooled connection is used.
    * If instead a single JDBC connection is used then this method will return -1.
    */
   public int getPoolMinSize()
   {
      return Integer.parseInt(getProperty(Environment.POOL_MIN_SIZE));
   }

   /**
    * Returns the connection pool maximum size when pooled connection is used.
    * If instead a single JDBC connection is used then this method will return -1.
    */
   public int getPoolMaxSize()
   {
      return Integer.parseInt(getProperty(Environment.POOL_MAX_SIZE));
   }

   /**
    * Returns the connection pool timeout when pooled connection is used.
    * If instead a single JDBC connection is used then this method will return -1.
    */
   public int getPoolTimeout()
   {
      return Integer.parseInt(getProperty(Environment.POOL_TIMEOUT));
   }

   /**
    * Returns the transaction timeout for the underlying database system. If users
    * don't specify this value then this method will return -1.
    */
   public int getTransactionTimeout()
   {
      return Integer.parseInt(getProperty(Environment.TRANSACTION_TIMEOUT));
   }

   /**
    * Returns the transaction fetch size for the underlying database system. If users
    * don't specify this value then this method will return -1.
    */
   public int getTransactionFetchSize()
   {
      return Integer.parseInt(getProperty(Environment.TRANSACTION_FETCH_SIZE));
   }

   /**
    * Returns the transaction max rows for the underlying database system. If users
    * don't specify this value then this method will return -1.
    */
   public int getTransactionMaxRows()
   {
      return Integer.parseInt(getProperty(Environment.TRANSACTION_MAX_ROWS));
   }

   /**
    * Returns the file object of the input ontology resource.
    */
   public File getOntologySource()
   {
      return new File(getProperty(Environment.ONTOLOGY_SOURCE));
   }

   /**
    * Returns a list of file object of the input mapping resources.
    */
   public List<File> getMappingSources()
   {
      final List<File> toReturn = new ArrayList<File>();
      String[] files = StringUtils.splitArrayString(getProperty(Environment.MAPPING_SOURCE));
      for (int i = 0; i < files.length; i++) {
         toReturn.add(new File(files[i]));
      }
      return toReturn;
   }

   @Override
   public String toString()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("List of system properties"); //$NON-NLS-1$
      sb.append("\n"); //$NON-NLS-1$
      sb.append(Environment.APPLICATION_FACTORY_NAME).append("=").append(getApplicationName()); //$NON-NLS-1$
      sb.append("\n"); //$NON-NLS-1$
      sb.append(Environment.CONNECTION_URL).append("=").append(getConnectionUrl()); //$NON-NLS-1$
      sb.append("\n"); //$NON-NLS-1$
      sb.append(Environment.POOL_INITIAL_SIZE).append("=").append(getPoolInitialSize()); //$NON-NLS-1$
      sb.append("\n"); //$NON-NLS-1$
      sb.append(Environment.POOL_MIN_SIZE).append("=").append(getPoolMinSize()); //$NON-NLS-1$
      sb.append("\n"); //$NON-NLS-1$
      sb.append(Environment.POOL_MAX_SIZE).append("=").append(getPoolMaxSize()); //$NON-NLS-1$
      sb.append("\n"); //$NON-NLS-1$
      sb.append(Environment.POOL_TIMEOUT).append("=").append(getPoolTimeout()); //$NON-NLS-1$
      sb.append("\n"); //$NON-NLS-1$
      sb.append(Environment.TRANSACTION_TIMEOUT).append("=").append(getTransactionTimeout()); //$NON-NLS-1$
      sb.append("\n"); //$NON-NLS-1$
      sb.append(Environment.TRANSACTION_FETCH_SIZE).append("=").append(getTransactionFetchSize()); //$NON-NLS-1$
      sb.append("\n"); //$NON-NLS-1$
      sb.append(Environment.TRANSACTION_MAX_ROWS).append("=").append(getTransactionMaxRows()); //$NON-NLS-1$
      sb.append("\n"); //$NON-NLS-1$
      sb.append(Environment.ONTOLOGY_SOURCE).append("=").append(getOntologySource().getPath()); //$NON-NLS-1$
      sb.append("\n"); //$NON-NLS-1$
      
      boolean needNewline = false;
      for (File file : getMappingSources()) {
         if (needNewline) {
            sb.append("\n"); //$NON-NLS-1$
         }
         sb.append(Environment.MAPPING_SOURCE).append("=").append(file.getPath()); //$NON-NLS-1$
         needNewline = true;
      }
      return sb.toString();
   }
}
