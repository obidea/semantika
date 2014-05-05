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

import com.obidea.semantika.database.IDatabase;
import com.obidea.semantika.database.connection.IConnectionProvider;
import com.obidea.semantika.database.sql.dialect.IDialect;
import com.obidea.semantika.knowledgebase.IPrefixManager;
import com.obidea.semantika.knowledgebase.processor.DisjunctionProcessor;
import com.obidea.semantika.knowledgebase.processor.KnowledgeBaseProcessorRegistry;
import com.obidea.semantika.knowledgebase.processor.ReferentialIntegrityProcessor;
import com.obidea.semantika.knowledgebase.processor.TMappingProcessor;
import com.obidea.semantika.mapping.IMappingSet;
import com.obidea.semantika.mapping.parser.MappingParserFactoryRegistry;
import com.obidea.semantika.mapping.parser.termalxml.TermalXmlParserFactory;
import com.obidea.semantika.mapping.sql.parser.JSqlParser;
import com.obidea.semantika.mapping.sql.parser.SqlMappingParserRegistry;
import com.obidea.semantika.ontology.IOntology;

public final class Settings
{
   private String mApplicationFactoryName;

   private IPrefixManager mPrefixManager;

   private IDatabase mDatabase;
   private IOntology mOntology;
   private IMappingSet mMappingSet;

   private IConnectionProvider mConnectionProvider;
   private IDialect mDialect;

   private Integer mTransactionTimeout;
   private Integer mTransactionFetchSize;
   private Integer mTransactionMaxRows;

   static {
      MappingParserFactoryRegistry registry = MappingParserFactoryRegistry.getInstance();
      registry.register(0, new TermalXmlParserFactory()); // top priority
   }

   static {
      SqlMappingParserRegistry registry = SqlMappingParserRegistry.getInstance();
      registry.register(new JSqlParser());
   }

   static {
      KnowledgeBaseProcessorRegistry registry = KnowledgeBaseProcessorRegistry.getInstance();
      registry.register(0, new TMappingProcessor()); // top priority
      registry.register(1, new DisjunctionProcessor());
      registry.register(2, new ReferentialIntegrityProcessor());
   }

   void setApplicationFactoryName(String name)
   {
      mApplicationFactoryName = name;
   }

   public String getApplicationFactoryName()
   {
      if (mApplicationFactoryName == null) {
         return "semantika/application_factory"; //$NON-NLS-1$
      }
      return mApplicationFactoryName;
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

   void setDialect(IDialect dialect)
   {
      mDialect = dialect;
   }

   public IDialect getDialect()
   {
      return mDialect;
   }

   public void setTransactionTimeout(Integer timeout)
   {
      mTransactionTimeout = timeout;
   }

   public Integer getTransactionTimeout()
   {
      return mTransactionTimeout;
   }

   public void setTransactionFetchSize(Integer fetchSize)
   {
      mTransactionFetchSize = fetchSize;
   }

   public Integer getTransactionFetchSize()
   {
      return mTransactionFetchSize;
   }

   public void setTransactionMaxRows(Integer maxRows)
   {
      mTransactionMaxRows = maxRows;
   }

   public Integer getTransactionMaxRows()
   {
      return mTransactionMaxRows;
   }

   /* package */Settings()
   {
      // NO-OP: Limited instantiation
   }
}
