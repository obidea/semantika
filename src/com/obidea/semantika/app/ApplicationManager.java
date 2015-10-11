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

import java.util.List;

import org.slf4j.Logger;

import com.obidea.semantika.database.IDatabase;
import com.obidea.semantika.database.connection.IConnectionProvider;
import com.obidea.semantika.knowledgebase.IPrefixManager;
import com.obidea.semantika.knowledgebase.model.IKnowledgeBase;
import com.obidea.semantika.knowledgebase.model.KnowledgeBase;
import com.obidea.semantika.knowledgebase.processor.IKnowledgeBaseProcessor;
import com.obidea.semantika.knowledgebase.processor.KnowledgeBaseProcessorException;
import com.obidea.semantika.knowledgebase.processor.KnowledgeBaseProcessorRegistry;
import com.obidea.semantika.mapping.IMappingSet;
import com.obidea.semantika.materializer.IMaterializerEngineFactory;
import com.obidea.semantika.materializer.RdfMaterializerEngine;
import com.obidea.semantika.ontology.IOntology;
import com.obidea.semantika.queryanswer.IQueryEngineFactory;
import com.obidea.semantika.queryanswer.SparqlQueryEngine;
import com.obidea.semantika.util.LogUtils;
import com.obidea.semantika.util.PrinterUtils;

public class ApplicationManager implements IApplicationManager, IQueryEngineFactory, IMaterializerEngineFactory
{
   private Settings mSettings;

   private KnowledgeBase mKnowledgeBase;

   private static final Logger LOG = LogUtils.createLogger("semantika.application"); //$NON-NLS-1$

   public ApplicationManager(final Settings settings)
   {
      mSettings = settings;
      
      LOG.info("Creating knowledge base..."); //$NON-NLS-1$
      IKnowledgeBase masterKb = createMasterKnowledgeBase(settings);
      
      LOG.info("Optimizing knowledge base..."); //$NON-NLS-1$
      for (IKnowledgeBaseProcessor processor : getKnowledgeBaseProcessors()) {
         try {
            LOG.debug("* Running {}...", processor.getName()); //$NON-NLS-1$
            processor.optimize(masterKb);
         }
         catch (KnowledgeBaseProcessorException e) {
            LOG.error("Processor \"{}\" failed to run: {}", processor.getName(), e.getMessage()); //$NON-NLS-1$
         }
      }
      IMappingSet mappingSet = masterKb.getMappingSet();
      LOG.debug("Generated mappings ({} items):\n{}", mappingSet.size(), PrinterUtils.print(mappingSet));
      
      /*
       * Encapsulate the mutable master knowledge base into read-only knowledge base.
       */
      mKnowledgeBase = new KnowledgeBase(masterKb);
      
      LOG.debug("ApplicationManager is ready to use."); //$NON-NLS-1$
   }

   @Override
   public IKnowledgeBase getKnowledgeBase()
   {
      return mKnowledgeBase;
   }

   @Override
   public String getApplicationName()
   {
      return mSettings.getSystemProperties().getApplicationName();
   }

   @Override
   public IPrefixManager getPrefixManager()
   {
      return mSettings.getPrefixManager();
   }

   @Override
   public IConnectionProvider getConnectionProvider()
   {
      return mSettings.getConnectionProvider();
   }

   @Override
   public SystemProperties getSystemProperties()
   {
      return mSettings.getSystemProperties();
   }

   @Override
   public List<IKnowledgeBaseProcessor> getKnowledgeBaseProcessors()
   {
      return KnowledgeBaseProcessorRegistry.getInstance().getProcessors();
   }

   @Override
   public SparqlQueryEngine createQueryEngine()
   {
      LOG.debug(""); //$NON-NLS-1$
      LOG.debug("Obtaining QueryEngine from ApplicationManager."); //$NON-NLS-1$
      return new SparqlQueryEngine(this);
   }

   @Override
   public RdfMaterializerEngine createMaterializerEngine()
   {
      LOG.debug(""); //$NON-NLS-1$
      LOG.debug("Obtaining MaterializerEngine from ApplicationManager."); //$NON-NLS-1$
      return new RdfMaterializerEngine(this);
   }

   public IDatabase getTargetDatabase()
   {
      return mSettings.getDatabase();
   }

   public String getSystemInfo()
   {
      return getSystemProperties().toString();
   }

   /*
    * A utility method to compile ontology, database and mapping set into a single knowledge base
    * object. The mapping set in this knowledge base is modifiable and thus can be optimized
    * further.
    */
   private static IKnowledgeBase createMasterKnowledgeBase(final Settings settings)
   {
      return new IKnowledgeBase()
      {
         @Override
         public IOntology getOntology()
         {
            return settings.getOntology();
         }

         @Override
         public IDatabase getDatabase()
         {
            return settings.getDatabase();
         }

         @Override
         public IMappingSet getMappingSet()
         {
            return settings.getMappingSet();
         }
      };
   }
}
