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

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.configuration.PropertiesConfiguration;

import com.obidea.semantika.database.IDatabase;
import com.obidea.semantika.database.JdbcDatabase;
import com.obidea.semantika.database.connection.ConnectionProviderFactory;
import com.obidea.semantika.database.connection.IConnectionProvider;
import com.obidea.semantika.database.sql.dialect.DialectFactory;
import com.obidea.semantika.database.sql.dialect.IDialect;
import com.obidea.semantika.exception.ConfigurationException;
import com.obidea.semantika.exception.SemantikaException;
import com.obidea.semantika.io.IDocumentSource;
import com.obidea.semantika.io.StreamDocumentSource;
import com.obidea.semantika.mapping.IMappingSet;
import com.obidea.semantika.mapping.parser.MappingParserConfiguration;
import com.obidea.semantika.ontology.IOntology;
import com.obidea.semantika.util.ConfigHelper;
import com.obidea.semantika.util.StringUtils;

public class DefaultSettingFactory extends SettingFactory
{
   @Override
   /* package */void loadDatabaseFromProperties(PropertiesConfiguration properties, Settings settings) throws SemantikaException
   {
      Properties connectionProperties = ConnectionProviderFactory.getConnectionProperties(properties);
      IConnectionProvider provider = ConnectionProviderFactory.createConnectionProvider(connectionProperties);
      settings.setConnectionProvider(provider);
      settings.setTransactionTimeout(determineTransactionTimeout(properties));
      settings.setTransactionFetchSize(determineTransactionFetchSize(properties));
      settings.setTransactionMaxRows(determineTransactionMaxRows(properties));
      try {
         /*
          * Register the Connection object as a weak reference to ease garbage collection.
          * This connection is used to fetch database metadata on-demand when parsing the
          * mappings. Ad hoc connection closing is not feasible.
          */
         WeakReference<Connection> weakConnection = new WeakReference<Connection>(provider.getConnection());
         IDatabase database = new JdbcDatabase(weakConnection.get());
         settings.setDatabase(database);
         settings.setDialect(determineDialect(properties, weakConnection.get()));
      }
      catch (SQLException e) {
         throw new SemantikaException("Exception occurred when initializing database object", e);
      }
   }

   @Override
   /* package */void loadOntologyFromProperties(PropertiesConfiguration properties, Settings settings) throws SemantikaException
   {
      String resource = properties.getString(Environment.ONTOLOGY_SOURCE);
      OntologyLoader loader = new OntologyLoader();
      if (StringUtils.isEmpty(resource)) {
         IOntology ontology = loader.createEmptyOntology();
         settings.setOntology(ontology);
         LOG.debug("Ontology source is not specified. An empty ontology is created."); //$NON-NLS-1$
      }
      else {
         InputStream in = ConfigHelper.getResourceStream(resource);
         IOntology ontology = loader.loadOntologyFromDocument(in);
         settings.setOntology(ontology);
      }
   }

   @Override
   /* package */void loadMappingFromProperties(PropertiesConfiguration properties, Settings settings) throws SemantikaException
   {
      String[] resource = properties.getStringArray(Environment.MAPPING_SOURCE);
      String[] isStrict = properties.getStringArray(Environment.STRICT_PARSING);
      if (resource.length == 0) {
         throw new ConfigurationException("Mapping resource is not specified in the configuration file."); //$NON-NLS-1$
      }
      MappingLoader loader = buildLoader(settings);
      IMappingSet mappingSet = loader.createEmptyMappingSet();
      for (int i = 0; i < resource.length; i++) {
         InputStream in = ConfigHelper.getResourceStream(resource[i]);
         IDocumentSource documentSource = new StreamDocumentSource(in, URI.create(resource[i]));
         
         // Construct the parsing configuration for each mapping
         MappingParserConfiguration configuration = new MappingParserConfiguration();
         configuration.setStrictParsing(Boolean.parseBoolean(isStrict[i]));
         mappingSet.copy(loader.loadMappingFromDocument(documentSource, configuration));
      }
      settings.setMappingSet(mappingSet);
   }

   private static MappingLoader buildLoader(Settings settings)
   {
      return MappingLoaderFactory.createMappingLoader(settings.getDatabase(), settings.getOntology());
   }

   private static Integer determineTransactionTimeout(PropertiesConfiguration properties)
   {
      Integer timeout = properties.getInteger(Environment.TRANSACTION_TIMEOUT, null);
      if (timeout != null) {
         LOG.debug("* transaction.timeout = " + timeout + "s"); //$NON-NLS-1$ //$NON-NLS-2$
      }
      return timeout;
   }

   private static Integer determineTransactionFetchSize(PropertiesConfiguration properties)
   {
      Integer fetchSize = properties.getInteger(Environment.TRANSACTION_FETCH_SIZE, null);
      if (fetchSize != null) {
         LOG.debug("* transaction.fetch_size = " + fetchSize); //$NON-NLS-1$
      }
      return fetchSize;
   }

   private static Integer determineTransactionMaxRows(PropertiesConfiguration properties)
   {
      Integer maxRows = properties.getInteger(Environment.TRANSACTION_MAX_ROWS, null);
      if (maxRows != null) {
         LOG.debug("* transaction.max_rows = " + maxRows); //$NON-NLS-1$
      }
      return maxRows;
   }

   private static IDialect determineDialect(PropertiesConfiguration properties, Connection conn)
   {
      String dialectName = properties.getString(Environment.DIALECT);
      IDialect dialect = DialectFactory.buildDialect(dialectName, conn);
      LOG.debug("* dialect = " + dialect.getClass().toString()); //$NON-NLS-1$
      return dialect;
   }
}
