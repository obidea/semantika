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

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.obidea.semantika.io.FileDocumentSource;
import com.obidea.semantika.io.IDocumentSource;
import com.obidea.semantika.io.StreamDocumentSource;
import com.obidea.semantika.io.UriDocumentSource;
import com.obidea.semantika.mapping.IMappingFactory;
import com.obidea.semantika.mapping.IMappingSet;
import com.obidea.semantika.mapping.IMetaModel;
import com.obidea.semantika.mapping.exception.MappingCreationException;
import com.obidea.semantika.mapping.exception.MappingFactoryNotFoundException;
import com.obidea.semantika.mapping.parser.MappingParserConfiguration;

public class MappingLoader extends MappingLoaderBase
{
   private IMetaModel mMetaModel;

   private List<IMappingFactory> mMappingSetFactories = new ArrayList<IMappingFactory>();

   public MappingLoader(IMetaModel metaModel)
   {
      mMetaModel = metaModel;
   }

   public void addMappingSetFactory(IMappingFactory factory)
   {
      mMappingSetFactories.add(0, factory);
      factory.setMetaModel(mMetaModel);
   }

   public List<IMappingFactory> getMappingSetFactories()
   {
      return Collections.unmodifiableList(mMappingSetFactories);
   }

   public IMappingSet loadMappingFromDocument(File file) throws MappingCreationException
   {
      return loadMappingFromDocument(new FileDocumentSource(file));
   }

   public IMappingSet loadMappingFromDocument(InputStream inputStream) throws MappingCreationException
   {
      return loadMappingFromDocument(new StreamDocumentSource(inputStream));
   }

   public IMappingSet loadMappingFromDocument(URI documentUri) throws MappingCreationException
   {
      return loadMappingFromDocument(new UriDocumentSource(documentUri));
   }

   private IMappingSet loadMappingFromDocument(IDocumentSource inputDocument) throws MappingCreationException
   {
      return loadMapping(inputDocument, new MappingParserConfiguration());
   }

   public IMappingSet loadMappingFromDocument(File file, MappingParserConfiguration configuration)
         throws MappingCreationException
   {
      return loadMapping(new FileDocumentSource(file), configuration);
   }

   public IMappingSet loadMappingFromDocument(InputStream inputStream, MappingParserConfiguration configuration)
         throws MappingCreationException
   {
      return loadMapping(new StreamDocumentSource(inputStream), configuration);
   }

   public IMappingSet loadMappingFromDocument(URI documentUri, MappingParserConfiguration configuration)
         throws MappingCreationException
   {
      return loadMapping(new UriDocumentSource(documentUri), configuration);
   }

   @Override
   protected IMappingSet loadMapping(IDocumentSource inputDocument, MappingParserConfiguration configuration)
         throws MappingCreationException
   {
      for (IMappingFactory factory : mMappingSetFactories) {
         if (factory.canLoad(inputDocument)) {
            return factory.loadMappingSet(inputDocument, this, configuration);
         }
      }
      throw new MappingFactoryNotFoundException(inputDocument.getDocumentUri());
   }
}
