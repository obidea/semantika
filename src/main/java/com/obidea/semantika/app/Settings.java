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
package com.obidea.semantika.app;

import com.obidea.semantika.database.IDatabase;
import com.obidea.semantika.database.connection.IConnectionProvider;
import com.obidea.semantika.database.sql.parser.JSqlParser;
import com.obidea.semantika.database.sql.parser.SqlParserRegistry;
import com.obidea.semantika.knowledgebase.IPrefixManager;
import com.obidea.semantika.knowledgebase.processor.DisjunctionProcessor;
import com.obidea.semantika.knowledgebase.processor.KnowledgeBaseProcessorRegistry;
import com.obidea.semantika.knowledgebase.processor.ReferentialIntegrityProcessor;
import com.obidea.semantika.knowledgebase.processor.TMappingProcessor;
import com.obidea.semantika.mapping.IMappingSet;
import com.obidea.semantika.mapping.parser.MappingParserFactoryRegistry;
import com.obidea.semantika.mapping.parser.r2rml.R2RmlParserFactory;
import com.obidea.semantika.mapping.parser.termalxml.TermalXmlParserFactory;
import com.obidea.semantika.ontology.IOntology;

/* package */
final class Settings
{
   private IPrefixManager mPrefixManager;

   private IDatabase mDatabase;
   private IOntology mOntology;
   private IMappingSet mMappingSet;

   private IConnectionProvider mConnectionProvider;

   private SystemProperties mSystemProperties = new SystemProperties();

   static {
      MappingParserFactoryRegistry registry = MappingParserFactoryRegistry.getInstance();
      registry.register(0, new TermalXmlParserFactory()); // top priority
      registry.register(new R2RmlParserFactory());
   }

   static {
      SqlParserRegistry registry = SqlParserRegistry.getInstance();
      registry.register(new JSqlParser());
   }

   static {
      KnowledgeBaseProcessorRegistry registry = KnowledgeBaseProcessorRegistry.getInstance();
      registry.register(0, new TMappingProcessor()); // top priority
      registry.register(1, new DisjunctionProcessor());
      registry.register(2, new ReferentialIntegrityProcessor());
   }

   void setPrefixManager(IPrefixManager manager)
   {
      mPrefixManager = manager;
   }

   public IPrefixManager getPrefixManager()
   {
      return mPrefixManager;
   }

   void setDatabase(IDatabase database)
   {
      mDatabase = database;
   }

   public IDatabase getDatabase()
   {
      return mDatabase;
   }

   void setOntology(IOntology ontology)
   {
      mOntology = ontology;
   }

   public IOntology getOntology()
   {
      return mOntology;
   }

   void setMappingSet(IMappingSet mappingSet)
   {
      mMappingSet = mappingSet;
   }

   public IMappingSet getMappingSet()
   {
      return mMappingSet;
   }

   void setConnectionProvider(IConnectionProvider provider)
   {
      mConnectionProvider = provider;
   }

   public IConnectionProvider getConnectionProvider()
   {
      return mConnectionProvider;
   }

   public void addSystemProperties(String key, String value)
   {
      mSystemProperties.put(key, value);
   }

   public SystemProperties getSystemProperties()
   {
      return mSystemProperties;
   }

   /* package */
   Settings()
   {
      // NO-OP: Limited instantiation
   }
}
