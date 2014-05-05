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
package com.obidea.semantika.database.connection;

import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.configuration.PropertiesConfiguration;

import com.obidea.semantika.app.Environment;
import com.obidea.semantika.exception.ConfigurationException;
import com.obidea.semantika.util.StringUtils;

public class ConnectionProviderFactory
{
   public static IConnectionProvider createConnectionProvider(Properties properties) throws ConfigurationException
   {
      IConnectionProvider provider;
      if (!StringUtils.isEmpty(properties.getProperty(Environment.POOL_MAX_SIZE))) {
         provider = new PooledConnectionProvider();
      }
      else {
         provider = new JdbcConnectionProvider();
      }
      provider.configure(properties);
      return provider;
   }

   public static Properties getConnectionProperties(PropertiesConfiguration properties)
   {
      Properties toReturn = new Properties();
      Iterator<String> iter = properties.getKeys();
      while (iter.hasNext()) {
         String propKey = iter.next();
         if (propKey.contains(Environment.CONNECTION_PREFIX)) {
            toReturn.setProperty(propKey, properties.getString(propKey));
         }
      }
      return toReturn;
   }

   // Prevent initialization
   private ConnectionProviderFactory()
   {
      // NO-OP
   }
}
