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

import static com.obidea.semantika.database.connection.ConnectionProviderFactory.getConnectionProperties;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.configuration.PropertiesConfiguration;

import com.obidea.semantika.database.JdbcDatabase;
import com.obidea.semantika.database.connection.ConnectionProviderFactory;
import com.obidea.semantika.database.connection.IConnectionProvider;
import com.obidea.semantika.database.sql.dialect.DialectFactory;
import com.obidea.semantika.database.sql.dialect.IDialect;
import com.obidea.semantika.exception.ConfigurationException;
import com.obidea.semantika.exception.SemantikaException;
import com.obidea.semantika.knowledgebase.DefaultPrefixManager;
import com.obidea.semantika.knowledgebase.IPrefixManager;
import com.obidea.semantika.mapping.IMappingSet;
import com.obidea.semantika.mapping.MappingSet;
import com.obidea.semantika.mapping.parser.MappingParserConfiguration;
import com.obidea.semantika.ontology.IOntology;
import com.obidea.semantika.util.ConfigHelper;
import com.obidea.semantika.util.StringUtils;

public class DefaultSettingFactory extends SettingFactory
{
   @Override
   /* package */
   void loadSystemProperties(PropertiesConfiguration properties, Settings settings) throws SemantikaException
   {
      settings.addSystemProperties(Environment.APPLICATION_FACTORY_NAME, properties.getString(Environment.APPLICATION_FACTORY_NAME));
      settings.addSystemProperties(Environment.TRANSACTION_TIMEOUT, properties.getString(Environment.TRANSACTION_TIMEOUT, "-1"));
      settings.addSystemProperties(Environment.TRANSACTION_FETCH_SIZE, properties.getString(Environment.TRANSACTION_FETCH_SIZE, "-1"));
      settings.addSystemProperties(Environment.TRANSACTION_MAX_ROWS, properties.getString(Environment.TRANSACTION_MAX_ROWS, "-1"));
   }

   @Override
   /* package */
   void loadDatabaseFromProperties(PropertiesConfiguration properties, Settings settings) throws SemantikaException
   {
      try {
         /*
          * Register the Connection object as a weak reference to ease garbage collection. This
          * connection is used to fetch database metadata on-demand when parsing the mappings. Manual
          * connection closing is not feasible.
          */
         IConnectionProvider provider = createConnectionProvider(properties);
         WeakReference<Connection> weakConnection = new WeakReference<Connection>(provider.getConnection());
         JdbcDatabase database = new JdbcDatabase(weakConnection.get());
         database.setDialect(determineDialect(properties, weakConnection.get()));
         settings.setDatabase(database);
         settings.setConnectionProvider(provider);
      }
      catch (SQLException e) {
         throw new SemantikaException("Exception occurred when initializing database object", e); //$NON-NLS-1$
      }
   }

   private static IConnectionProvider createConnectionProvider(PropertiesConfiguration properties) throws ConfigurationException
   {
      return ConnectionProviderFactory.createConnectionProvider(getConnectionProperties(properties));
   }

   @Override
   /* package */
   void loadOntologyFromProperties(PropertiesConfiguration properties, Settings settings) throws SemantikaException
   {
      OntologyLoader loader = buildOntologyLoader(settings);
      String resource = properties.getString(Environment.ONTOLOGY_SOURCE);
      if (StringUtils.isEmpty(resource)) {
         LOG.debug("Ontology source is not specified. An empty ontology is created."); //$NON-NLS-1$
         IOntology ontology = loader.createEmptyOntology();
         settings.setOntology(ontology);
      }
      else {
         LOG.debug("Parsing ontology {}", resource); //$NON-NLS-1$
         InputStream in = ConfigHelper.getResourceStream(resource);
         IOntology ontology = loader.loadOntologyFromDocument(in);
         settings.setOntology(ontology);
      }
   }

   @Override
   /* package */
   void loadMappingFromProperties(PropertiesConfiguration properties, Settings settings) throws SemantikaException
   {
      IMappingSet mappingSet = new MappingSet();
      IPrefixManager prefixManager = new DefaultPrefixManager();
      
      MappingLoader loader = buildMappingLoader(settings);
      /*
       * feature/multiple-mappings: Support multiple mapping entries in the configuration file.
       * The iteration will check and parse each mapping resource and collect the results.
       */
      String[] resource = properties.getStringArray(Environment.MAPPING_SOURCE);
      for (int i = 0; i < resource.length; i++) {
         LOG.debug("Parsing mapping {}", resource[i]); //$NON-NLS-1$
         InputStream in = ConfigHelper.getResourceStream(resource[i]);
         collectMappingEntries(mappingSet, loader.loadMappingFromDocument(in, createParserConfiguration(properties, i)));
         collectPrefixEntries(prefixManager, loader.getPrefixManager());
      }
      settings.setMappingSet(mappingSet);
      settings.setPrefixManager(prefixManager);
   }

   /*
    * Private utility methods
    */

   private static OntologyLoader buildOntologyLoader(Settings settings)
   {
      return new OntologyLoader();
   }

   private static MappingLoader buildMappingLoader(Settings settings)
   {
      return MappingLoaderFactory.createMappingLoader(MetaModel.getInstance(settings));
   }

   private static MappingParserConfiguration createParserConfiguration(PropertiesConfiguration properties, int order)
   {
      MappingParserConfiguration configuration = new MappingParserConfiguration();
      configuration.setStrictParsing(Boolean.parseBoolean(properties.getStringArray(Environment.STRICT_PARSING)[order]));
      return configuration;
   }

   private static IDialect determineDialect(PropertiesConfiguration properties, Connection conn)
   {
      String dialectName = properties.getString(Environment.DIALECT);
      IDialect dialect = DialectFactory.buildDialect(dialectName, conn);
      LOG.debug("* dialect = " + dialect.getClass().toString()); //$NON-NLS-1$
      return dialect;
   }

   private static void collectMappingEntries(IMappingSet parent, IMappingSet child)
   {
      parent.copy(child);
   }

   private static void collectPrefixEntries(IPrefixManager parent, IPrefixManager child)
   {
      parent.copy(child.getPrefixMapper());
   }
}
