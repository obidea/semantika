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

import org.slf4j.Logger;

import com.obidea.semantika.database.IDatabaseMetadata;
import com.obidea.semantika.knowledgebase.IPrefixManager;
import com.obidea.semantika.mapping.IMappingFactory.IMetaModel;
import com.obidea.semantika.mapping.IMappingSet;
import com.obidea.semantika.ontology.IOntology;
import com.obidea.semantika.util.LogUtils;

public abstract class MappingLoaderBase implements IMetaModel, IMappingLoader
{
   private IDatabaseMetadata mDatabaseMetadata;
   private IOntology mOntology;

   private IPrefixManager mPrefixManager;

   private static final Logger LOG = LogUtils.createLogger("semantika.application"); //$NON-NLS-1$

   public MappingLoaderBase(IDatabaseMetadata databaseMetadata, IOntology ontology)
   {
      mDatabaseMetadata = databaseMetadata;
      mOntology = ontology;
   }

   @Override
   public IDatabaseMetadata getDatabaseMetadata()
   {
      return mDatabaseMetadata;
   }

   @Override
   public IOntology getOntology()
   {
      return mOntology;
   }

   @Override
   public void mappingLoaded(IMappingSet mappingSet, IPrefixManager prefixManager)
   {
      LOG.debug("* Mapping count = {}", mappingSet.size()); //$NON-NLS-1$
      LOG.debug("* Class mapping count = {}", mappingSet.getClassMappings().size()); //$NON-NLS-1$
      LOG.debug("* Property mapping count = {}", mappingSet.getPropertyMappings().size()); //$NON-NLS-1$
      
      mPrefixManager = prefixManager;
      for (String prefix : prefixManager.getPrefixNames()) {
         LOG.debug("* Use prefix {}: {}", prefix, prefixManager.getNamespace(prefix)); //$NON-NLS-1$
      }
   }

   public IPrefixManager getPrefixManager()
   {
      return mPrefixManager;
   }
}
