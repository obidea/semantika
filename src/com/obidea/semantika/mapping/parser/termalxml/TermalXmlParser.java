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
package com.obidea.semantika.mapping.parser.termalxml;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.obidea.semantika.io.IDocumentSource;
import com.obidea.semantika.knowledgebase.DefaultPrefixManager;
import com.obidea.semantika.knowledgebase.IPrefixManager;
import com.obidea.semantika.mapping.IMappingFactory.IMetaModel;
import com.obidea.semantika.mapping.IMappingSet;
import com.obidea.semantika.mapping.exception.MappingParserException;
import com.obidea.semantika.mapping.parser.AbstractMappingParser;
import com.obidea.semantika.mapping.parser.MappingParserConfiguration;

public class TermalXmlParser extends AbstractMappingParser
{
   public TermalXmlParser(IMetaModel metaModel)
   {
      super(metaModel);
   }

   @Override
   public IPrefixManager parse(final IDocumentSource inputDocument, final IMappingSet mappingSet,
         final MappingParserConfiguration configuration) throws MappingParserException, IOException
   {
      InputSource is = getInputSource(inputDocument);
      try {
         DefaultPrefixManager prefixManager = new DefaultPrefixManager();
         SAXParserFactory factory = SAXParserFactory.newInstance();
         factory.setNamespaceAware(true);
         SAXParser parser = factory.newSAXParser();
         TermalXmlParserHandler handler = new TermalXmlParserHandler(mappingSet, getMetaModel(), configuration);
         parser.parse(is, handler);
         prefixManager.setAll(handler.getPrefixMapper());
         return prefixManager;
      }
      catch (ParserConfigurationException e) {
         throw new MappingParserException(e.getMessage());
      }
      catch (SAXException e) {
         throw new MappingParserException(e.getMessage());
      }
      finally {
         if (is != null && is.getByteStream() != null) {
            is.getByteStream().close();
         } else if (is != null && is.getCharacterStream() != null) {
            is.getCharacterStream().close();
         }
      }
   }

   private InputSource getInputSource(IDocumentSource inputDocument) throws IOException
   {
      InputSource is = new InputSource(inputDocument.getInputStream());
      is.setSystemId(inputDocument.getDocumentUri().toString());
      return is;
   }

   @Override
   public String getSyntax()
   {
      return "TERMAL/XML"; //$NON-NLS-1$
   }
}
