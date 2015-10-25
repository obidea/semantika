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
package com.obidea.semantika.mapping.parser.termalxml;

import static com.obidea.semantika.mapping.parser.termalxml.TermalVocabulary.COMMENT;
import static com.obidea.semantika.mapping.parser.termalxml.TermalVocabulary.LOGICAL_TABLE;
import static com.obidea.semantika.mapping.parser.termalxml.TermalVocabulary.MAPPING;
import static com.obidea.semantika.mapping.parser.termalxml.TermalVocabulary.PREDICATE_OBJECT_MAP;
import static com.obidea.semantika.mapping.parser.termalxml.TermalVocabulary.PREFIX;
import static com.obidea.semantika.mapping.parser.termalxml.TermalVocabulary.PROGRAM;
import static com.obidea.semantika.mapping.parser.termalxml.TermalVocabulary.SUBJECT_MAP;
import static com.obidea.semantika.mapping.parser.termalxml.TermalVocabulary.URI_TEMPLATE;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.obidea.semantika.database.IDatabaseMetadata;
import com.obidea.semantika.expression.ExpressionObjectFactory;
import com.obidea.semantika.mapping.IMappingSet;
import com.obidea.semantika.mapping.IMetaModel;
import com.obidea.semantika.mapping.MappingObjectFactory;
import com.obidea.semantika.mapping.MutableMappingSet;
import com.obidea.semantika.mapping.exception.MappingParserException;
import com.obidea.semantika.mapping.parser.MappingParserConfiguration;
import com.obidea.semantika.ontology.IOntology;
import com.obidea.semantika.ontology.owlapi.AbstractOwlOntology;
import com.obidea.semantika.util.StringUtils;

public class TermalXmlParserHandler extends DefaultHandler
{
   private MutableMappingSet mMappingSet;

   private IDatabaseMetadata mDatabaseMetadata;

   private AbstractOwlOntology mOntology;

   private MappingParserConfiguration mConfiguration;

   private Locator mLocator;

   private Map<String, String> mPrefixMapper;

   private Map<String, String> mUriTemplateMapper;

   private Map<String, AbstractTermalElementHandlerFactory> mHandlerMap;

   private Stack<AbstractTermalElementHandler> mHandlerStack;

   public TermalXmlParserHandler(IMappingSet mappingSet, IMetaModel metaModel, MappingParserConfiguration configuration) throws MappingParserException
   {
      mMappingSet = (MutableMappingSet) mappingSet;
      mConfiguration = configuration;
      mDatabaseMetadata = metaModel.getDatabaseMetadata();
      mOntology = owlOntology(metaModel.getOntology());
      mPrefixMapper = new HashMap<String, String>();
      mUriTemplateMapper = new HashMap<String, String>();
      mHandlerMap = new HashMap<String, AbstractTermalElementHandlerFactory>();
      mHandlerStack = new Stack<AbstractTermalElementHandler>();
      
      addFactory(new AbstractTermalElementHandlerFactory(PROGRAM)
      {
         @Override
         public AbstractTermalElementHandler createElementHandler(TermalXmlParserHandler handler) {
            return new ProgramElementHandler(handler);
         }
      });
      
      addFactory(new AbstractTermalElementHandlerFactory(MAPPING)
      {
         @Override
         public AbstractTermalElementHandler createElementHandler(TermalXmlParserHandler handler) {
            return new MappingElementHandler(handler);
         }
      });
      
      addFactory(new AbstractTermalElementHandlerFactory(LOGICAL_TABLE)
      {
         @Override
         public AbstractTermalElementHandler createElementHandler(TermalXmlParserHandler handler) {
            return new LogicalTableElementHandler(handler);
         }
      });
      
      addFactory(new AbstractTermalElementHandlerFactory(SUBJECT_MAP)
      {
         @Override
         public AbstractTermalElementHandler createElementHandler(TermalXmlParserHandler handler) {
            return new SubjectMapElementHandler(handler);
         }
      });
      
      addFactory(new AbstractTermalElementHandlerFactory(PREDICATE_OBJECT_MAP)
      {
         @Override
         public AbstractTermalElementHandler createElementHandler(TermalXmlParserHandler handler) {
            return new PredicateObjectMapElementHandler(handler);
         }
      });
   }

   private static AbstractOwlOntology owlOntology(IOntology ontology) throws MappingParserException
   {
      if (!(ontology instanceof AbstractOwlOntology)) {
         throw new MappingParserException("Mapping parser requires OWL ontology object"); //$NON-NLS-1$
      }
      return (AbstractOwlOntology) ontology;
   }

   public boolean isStrictParsing()
   {
      return mConfiguration.isStrictParsing();
   }

   public IDatabaseMetadata getDatabaseMetadata()
   {
      return mDatabaseMetadata;
   }

   public AbstractOwlOntology getOntology()
   {
      return mOntology;
   }

   public MutableMappingSet getMappingSet()
   {
      return mMappingSet;
   }

   public ExpressionObjectFactory getExpressionObjectFactory()
   {
      return ExpressionObjectFactory.getInstance();
   }

   public MappingObjectFactory getMappingObjectFactory()
   {
      return MappingObjectFactory.getInstance();
   }

   @Override
   public void setDocumentLocator(Locator locator)
   {
      super.setDocumentLocator(locator);
      mLocator = locator;
   }

   public int getLineNumber()
   {
      if (mLocator != null) {
         return mLocator.getLineNumber();
      }
      else {
         return -1;
      }
   }

   public int getColumnNumber()
   {
      if (mLocator != null) {
         return mLocator.getColumnNumber();
      }
      else {
         return -1;
      }
   }

   public Map<String, String> getPrefixMapper()
   {
      return mPrefixMapper;
   }

   public Map<String, String> getUriTemplateMapper()
   {
      return mUriTemplateMapper;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
   {
      try {
         if (localName.equals(COMMENT.getLocalName())) {
            return; // skip it
         }
         
         // Collect the prefix declarations
         if (localName.equals(PREFIX.getLocalName())) {
            String prefixName = attributes.getValue(TermalVocabulary.NAME.getQName());
            String namespaceUri = attributes.getValue(TermalVocabulary.NAMESPACE.getQName());
            if (!StringUtils.isEmpty(namespaceUri)) {
               mPrefixMapper.put(prefixName, namespaceUri);
            }
            return;
         }
         
         // Collect the identifier-template declarations
         if (localName.equals(URI_TEMPLATE.getLocalName())) {
            String templateName = attributes.getValue(TermalVocabulary.NAME.getQName());
            String templateString = attributes.getValue(TermalVocabulary.VALUE.getQName());
            if (!StringUtils.isEmpty(templateName) && !StringUtils.isEmpty(templateString)) {
               mUriTemplateMapper.put(templateName, templateString);
            }
            return;
         }
         
         AbstractTermalElementHandlerFactory handlerFactory = mHandlerMap.get(localName);
         if (handlerFactory != null) {
            AbstractTermalElementHandler handler = handlerFactory.createElementHandler(this);
            if (!mHandlerStack.isEmpty()) {
               AbstractTermalElementHandler topElement = mHandlerStack.peek();
               handler.setParentElement(topElement);
            }
            mHandlerStack.push(handler);
            for (int i = 0; i < attributes.getLength(); i++) {
               handler.attribute(attributes.getQName(i), attributes.getValue(i));
            }
            handler.startElement(localName);
         }
         else {
            throw new UnknownXmlElementException("Unknown XML element \"<" + localName + ">\"", //$NON-NLS-1$ //$NON-NLS-2$
                  getLineNumber(), getColumnNumber());
         }
      }
      catch (MappingParserException e) {
         throw new SAXException(e);
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException
   {
      try {
         if (localName.equals(PREFIX.getLocalName())) {
            return; // ignore this tag
         }
         if (localName.equals(URI_TEMPLATE.getLocalName())) {
            return; // ignore this tag
         }
         if (localName.equals(COMMENT.getLocalName())) {
            return; // ignore this tag
         }
         if (!mHandlerStack.isEmpty()) {
            AbstractTermalElementHandler handler = mHandlerStack.pop();
            handler.endElement();
         }
      }
      catch (MappingParserException e) {
         throw new SAXException(e);
      }
   }

   @Override
   public void characters(char[] ch, int start, int length) throws SAXException
   {
      try {
         if (!mHandlerStack.isEmpty()) {
            AbstractTermalElementHandler handler = mHandlerStack.peek();
            String localName = handler.getElementName();
            if (localName.equals(LOGICAL_TABLE.getLocalName())) {
               handler.characters(ch, start, length);
            }
         }
      }
      catch (MappingParserException e) {
         throw new SAXException(e);
      }
   }

   private void addFactory(AbstractTermalElementHandlerFactory factory, String... legacyElementNames)
   {
      mHandlerMap.put(factory.getElementName(), factory);
      for (String elementName : legacyElementNames) {
         mHandlerMap.put(elementName, factory);
      }
   }
}
