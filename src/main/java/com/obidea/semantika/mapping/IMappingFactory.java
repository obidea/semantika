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
package com.obidea.semantika.mapping;

import com.obidea.semantika.io.IDocumentSource;
import com.obidea.semantika.knowledgebase.IPrefixManager;
import com.obidea.semantika.mapping.exception.MappingCreationException;
import com.obidea.semantika.mapping.parser.MappingParserConfiguration;

public interface IMappingFactory
{
   /**
    * Sets the meta-information about the data model and domain model. These
    * objects are required to to construct and validate the mapping assertions.
    * 
    * @param metaModel
    *           The meta-model for constructing the mapping. Cannot be null.
    */
   void setMetaModel(IMetaModel metaModel);

   /**
    * Loads and creates a mapping set.
    * 
    * @param inputDocument
    *           The document source that contains the mapping assertions.
    * @param mediator
    *           A pointer to the loader to notify the creation of a mapping set.
    * @param configuration
    *           A configuration object which can be used to pass various options
    *           to the parser.
    * @return The newly loaded and created mapping set
    * @throws MappingCreationException
    *            if the mapping set could not be created
    */
   IMappingSet loadMappingSet(IDocumentSource inputDocument, IMappingLoadHandler mediator,
         MappingParserConfiguration configuration) throws MappingCreationException;

   /**
    * Determines if the factory can load the mappings from a specified input
    * source.
    * 
    * @param inputDocument
    *           The input source from which the mapping assertions can be found.
    * @return Returns <code>true</code> if the factory can load from the
    *         specified input source.
    */
   boolean canLoad(IDocumentSource inputDocument);

   public interface IMappingLoadHandler
   {
      void mappingLoaded(IMappingSet mappingSet, IPrefixManager prefixManager);
   }
}
