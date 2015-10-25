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

import org.slf4j.Logger;

import com.obidea.semantika.io.IDocumentSource;
import com.obidea.semantika.knowledgebase.IPrefixManager;
import com.obidea.semantika.mapping.IMappingFactory.IMappingLoadHandler;
import com.obidea.semantika.mapping.IMappingSet;
import com.obidea.semantika.mapping.exception.MappingCreationException;
import com.obidea.semantika.mapping.parser.MappingParserConfiguration;
import com.obidea.semantika.util.LogUtils;

public abstract class MappingLoaderBase implements IMappingLoadHandler
{
   private IPrefixManager mPrefixManager;

   private static final Logger LOG = LogUtils.createLogger("semantika.application"); //$NON-NLS-1$

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

   /**
    * Gets the prefix manager associated to the recent mapping loading.
    */
   public IPrefixManager getPrefixManager()
   {
      return mPrefixManager;
   }

   protected abstract IMappingSet loadMapping(IDocumentSource document, MappingParserConfiguration configuration)
         throws MappingCreationException;
}
