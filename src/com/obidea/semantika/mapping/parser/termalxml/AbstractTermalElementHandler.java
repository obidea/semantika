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

import java.net.URI;
import java.util.Map;

import org.slf4j.Logger;

import com.obidea.semantika.database.IDatabaseMetadata;
import com.obidea.semantika.database.sql.parser.SqlParserException;
import com.obidea.semantika.expression.ExpressionObjectFactory;
import com.obidea.semantika.mapping.MappingObjectFactory;
import com.obidea.semantika.mapping.MutableMappingSet;
import com.obidea.semantika.mapping.exception.DataTypeOverrideException;
import com.obidea.semantika.mapping.exception.MappingParserException;
import com.obidea.semantika.ontology.owlapi.AbstractOwlOntology;
import com.obidea.semantika.util.LogUtils;

public abstract class AbstractTermalElementHandler implements IMappingElementHandler
{
   private TermalXmlParserHandler mHandler;

   private AbstractTermalElementHandler mParentElement;

   private StringBuilder mStringBuilder;

   private String mElementName;

   protected static final Logger LOG = LogUtils.createLogger("semantika.termalxml"); //$NON-NLS-1$

   public AbstractTermalElementHandler(TermalXmlParserHandler handler)
   {
      mHandler = handler;
   }

   @Override
   public void startElement(String name) throws MappingParserException
   {
      mStringBuilder = null;
      mElementName = name;
   }

   @Override
   public void characters(char[] ch, int start, int length) throws MappingParserException
   {
      // NO-OP: To be implemented by children classes
   }

   protected boolean isStrictParsing()
   {
      return mHandler.isStrictParsing();
   }

   protected ExpressionObjectFactory getExpressionObjectFactory()
   {
      return mHandler.getExpressionObjectFactory();
   }

   protected MappingObjectFactory getMappingObjectFactory()
   {
      return mHandler.getMappingObjectFactory();
   }

   protected MutableMappingSet getMappingSet()
   {
      return mHandler.getMappingSet();
   }

   protected Map<String, String> getPrefixMapper()
   {
      return mHandler.getPrefixMapper();
   }

   protected Map<String, String> getUriTemplateMapper()
   {
      return mHandler.getUriTemplateMapper();
   }

   protected int getLineNumber()
   {
      return mHandler.getLineNumber();
   }

   protected int getColumnNumber()
   {
      return mHandler.getColumnNumber();
   }

   protected IDatabaseMetadata getDatabaseMetadata()
   {
      return mHandler.getDatabaseMetadata();
   }

   protected AbstractOwlOntology getOntology()
   {
      return mHandler.getOntology();
   }

   protected String getElementName()
   {
      return mElementName;
   }

   public void setParentElement(AbstractTermalElementHandler handler)
   {
      mParentElement = handler;
   }

   protected AbstractTermalElementHandler getParentElement()
   {
      return mParentElement;
   }

   /* package */abstract void handleChild(MappingElementHandler handler) throws MappingParserException;

   /* package */abstract void handleChild(LogicalTableElementHandler handler) throws MappingParserException;

   /* package */abstract void handleChild(SubjectMapElementHandler handler) throws MappingParserException;

   /* package */abstract void handleChild(PredicateObjectMapElementHandler handler) throws MappingParserException;

   protected UnknownXmlAttributeException unknownXmlAttributeException(String value)
   {
      return new UnknownXmlAttributeException("Unknown XML attribute \"" + value + "\"", //$NON-NLS-1$ //$NON-NLS-2$
            getLineNumber(), getColumnNumber());
   }

   protected UnknownXmlDataTypeException unknownXmlDataTypeException(String value)
   {
      return new UnknownXmlDataTypeException("Unknown XML data type \"" + value + "\"", //$NON-NLS-1$ //$NON-NLS-2$
            getLineNumber(), getColumnNumber());
   }

   protected UnsupportedTermTypeException unsupportedTermTypeException(String value)
   {
      return new UnsupportedTermTypeException("Unsupported term type \"" + value + "\"", //$NON-NLS-1$ //$NON-NLS-2$
            getLineNumber(), getColumnNumber());
   }

   protected UnknownTermTypeException unknownTermTypeException(String value)
   {
      return new UnknownTermTypeException("Unknown term type argument \"" + value + "\"", //$NON-NLS-1$ //$NON-NLS-2$
            getLineNumber(), getColumnNumber());
   }

   protected DataTypeOverrideException datatypeOverrideException(String sourceType, String targetType)
   {
      return new DataTypeOverrideException("Unable to convert type \"" + sourceType + "\" to type \"" + targetType + "\"", //$NON-NLS-1$ //$NON-NLS-2$  //$NON-NLS-3$
            getLineNumber(), getColumnNumber());
   }

   protected ClassNotFoundException classNotFoundException(URI value)
   {
      // XXX Strange bug related to line number
      return new ClassNotFoundException("Class URI not found in ontology \"" + value + "\"", //$NON-NLS-1$ //$NON-NLS-2$
            getLineNumber()-1, getColumnNumber());
   }

   protected PropertyNotFoundException propertyNotFoundException(URI value)
   {
      return new PropertyNotFoundException("Property URI not found in ontology \"" + value + "\"", //$NON-NLS-1$ //$NON-NLS-2$
            getLineNumber(), getColumnNumber());
   }

   protected PrefixNotFoundException prefixNotFoundException(String value)
   {
      // XXX Strange bug related to line number
      return new PrefixNotFoundException("Unspecified prefix name \"" + value + "\"", //$NON-NLS-1$ //$NON-NLS-2$
            getLineNumber()-1, getColumnNumber());
   }

   protected TemplateNotFoundException templateNotFoundException(String value)
   {
      return new TemplateNotFoundException("Unspecified template name \"" + value + "\"", //$NON-NLS-1$ //$NON-NLS-2$
            getLineNumber(), getColumnNumber());
   }

   protected SelectItemNotFoundException selectItemNameNotFoundException(String value)
   {
      return new SelectItemNotFoundException("Column name not found in logical table \"" + value + "\"", //$NON-NLS-1$ //$NON-NLS-2$
            getLineNumber(), getColumnNumber());
   }

   protected InvalidFunctionCallException invalidFunctionCallException(String value)
   {
      return new InvalidFunctionCallException("Invalid URI template usage \"" + value + "\"", //$NON-NLS-1$ //$NON-NLS-2$
            getLineNumber(), getColumnNumber());
   }

   protected UnsupportedUriTemplateArgumentException unsupportedUriTemplateArgumentException(String value)
   {
      return new UnsupportedUriTemplateArgumentException("Only support column reference as URI template argument \"" + value + "\"", //$NON-NLS-1$ //$NON-NLS-2$
            getLineNumber(), getColumnNumber());
   }

   protected IllegalTermalMappingException illegalTermalMappingException(String message)
   {
      return new IllegalTermalMappingException(message, getLineNumber(), getColumnNumber());
   }

   protected SourceQueryParsingException sourceQueryParsingException(SqlParserException e)
   {
      return new SourceQueryParsingException(e.getMessage(), getLineNumber(), getColumnNumber());
   }

   @Override
   public String getText()
   {
      if (mStringBuilder == null) {
         return ""; //$NON-NLS-1$
      }
      else {
         return mStringBuilder.toString();
      }
   }
}
