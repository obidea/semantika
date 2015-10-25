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
package com.obidea.semantika.mapping;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.obidea.semantika.exception.IllegalOperationException;
import com.obidea.semantika.io.IDocumentSource;
import com.obidea.semantika.knowledgebase.IPrefixManager;
import com.obidea.semantika.mapping.exception.MappingCreationException;
import com.obidea.semantika.mapping.exception.MappingCreationIOException;
import com.obidea.semantika.mapping.exception.MappingParserException;
import com.obidea.semantika.mapping.exception.UnparsableMappingFileException;
import com.obidea.semantika.mapping.parser.IMappingParser;
import com.obidea.semantika.mapping.parser.IMappingParserFactory;
import com.obidea.semantika.mapping.parser.MappingParserConfiguration;
import com.obidea.semantika.mapping.parser.MappingParserFactoryRegistry;
import com.obidea.semantika.util.LogUtils;

/**
 * A mapping program factory that creates mapping programs by parsing documents
 * containing terminology to relational data mapping assertions. This mapping
 * program factory will claim that it is suitable for creating a mapping program
 * if the document URI can be opened for reading. This factory will not create
 * an empty mapping program. Parsers are instantiated by using a list of
 * <code>IMappingParserFactory</code> objects that are obtained from the
 * <code>MappingParserRegistry</code>.
 */
public class ParsableMappingSetFactory extends AbstractMappingFactory
{
   private static final Logger LOG = LogUtils.createLogger("semantika.mapping"); //$NON-NLS-1$

   @Override
   public MappingSet createEmptyMappingSet()
   {
      throw new IllegalOperationException("Cannot create an empty mapping program."); //$NON-NLS-1$
   }

   public List<IMappingParser> getParsers()
   {
      List<IMappingParser> parsers = new ArrayList<IMappingParser>();
      List<IMappingParserFactory> factories = MappingParserFactoryRegistry.getInstance().getFactories();
      for (IMappingParserFactory factory : factories) {
         IMappingParser parser = factory.createParser(getMetaModel());
         parsers.add(parser);
      }
      return parsers;
   }

   @Override
   public IMappingSet loadMappingSet(IDocumentSource inputDocument, IMappingLoadHandler mediator,
         MappingParserConfiguration configuration) throws MappingCreationException
   {
      Map<IMappingParser, MappingParserException> exceptions = new LinkedHashMap<IMappingParser, MappingParserException>();
      
      MutableMappingSet mappingSet = super.createEmptyMappingSet();
      for (final IMappingParser parser : getParsers()) {
         try {
            IPrefixManager pm = parser.parse(inputDocument, mappingSet, configuration);
            mediator.mappingLoaded(mappingSet, pm);
            return mappingSet;
         }
         catch (IOException e) {
            throw new MappingCreationIOException(e);
         }
         catch (MappingParserException e) {
            exceptions.put(parser, e);
         }
         catch (RuntimeException e) {
            throw e;
         }
      }
      /*
       * The system couldn't find a proper parser to parse the mapping file. Throw
       * an exception whose message contains the stack traces from all of the
       * parsers that the system has tried.
       */
      throw new UnparsableMappingFileException(inputDocument.getDocumentUri(), exceptions);
   }

   @Override
   public boolean canLoad(IDocumentSource document)
   {
      if (document.isReaderAvailable()) {
         return true;
      }
      if (document.isInputStreamAvailable()) {
         return true;
      }
      try {
         InputStream is = document.getDocumentUri().toURL().openStream();
         is.close();
         return true;
      }
      catch (UnknownHostException e) {
         LOG.error("Unknown host"); //$NON-NLS-1$
         LOG.error("Detailed cause: {}", e.getMessage()); //$NON-NLS-1$
      }
      catch (MalformedURLException e) {
         LOG.error("Malformed URL"); //$NON-NLS-1$
         LOG.error("Detailed cause: {}", e.getMessage()); //$NON-NLS-1$
      }
      catch (FileNotFoundException e) {
         LOG.error("File not found"); //$NON-NLS-1$
         LOG.error("Detailed cause: {}", e.getMessage()); //$NON-NLS-1$
      }
      catch (IOException e) {
         LOG.error("Problem in I/O stream"); //$NON-NLS-1$
         LOG.error("Detailed cause: {}", e.getMessage()); //$NON-NLS-1$
      }
      return false;
   }
}
