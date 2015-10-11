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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.obidea.semantika.exception.ConfigurationException;
import com.obidea.semantika.exception.ResourceNotFoundException;
import com.obidea.semantika.exception.SemantikaException;
import com.obidea.semantika.util.ConfigHelper;
import com.obidea.semantika.util.LogUtils;
import com.obidea.semantika.util.StringUtils;
import com.obidea.semantika.util.XmlHelper;

public class ApplicationFactory
{
   public static final String DEFAULT_CONFIGURATION_FILENAME = "application.cfg.xml"; //$NON-NLS-1$

   private PropertiesConfiguration mProperties;

   private SettingFactory mSettingFactory;

   private XmlHelper mXmlHelper;

   private static final Logger LOG = LogUtils.createLogger("semantika.application"); //$NON-NLS-1$

   protected ApplicationFactory(SettingFactory factory)
   {
      LOG.debug("Initializing ApplicationFactory."); //$NON-NLS-1$
      mSettingFactory = factory;
      mXmlHelper = new XmlHelper();
      mProperties = Environment.getProperties();
   }

   public ApplicationFactory()
   {
      this(new DefaultSettingFactory());
   }

   public ApplicationFactory addProperty(String propertyName, Object value)
   {
      mProperties.setProperty(propertyName, value);
      return this;
   }

   public ApplicationFactory setName(String name)
   {
      mProperties.setProperty(Environment.APPLICATION_FACTORY_NAME, name);
      return this;
   }

   public ApplicationFactory setOntologySource(String resource)
   {
      mProperties.setProperty(Environment.ONTOLOGY_SOURCE, resource);
      return this;
   }

   public ApplicationFactory addMappingSource(String resource)
   {
      return addMappingSource(resource, true);
   }

   public ApplicationFactory addMappingSource(String resource, boolean useStrictParsing)
   {
      mProperties.addProperty(Environment.MAPPING_SOURCE, resource);
      mProperties.addProperty(Environment.STRICT_PARSING, useStrictParsing);
      return this;
   }

   public ApplicationFactory configure() throws ConfigurationException
   {
      return configure(DEFAULT_CONFIGURATION_FILENAME); //$NON-NLS-1$
   }

   public ApplicationFactory configure(String resource) throws ConfigurationException
   {
      LOG.debug("Reading configuration (path: {})", resource); //$NON-NLS-1$
      InputStream stream = ConfigHelper.getResourceInputStream(resource);
      return doConfigure(stream, resource);
   }

   public ApplicationFactory configure(File resource) throws ConfigurationException
   {
      LOG.debug("Reading configuration (file: {})", resource.getName()); //$NON-NLS-1$
      try {
         InputStream stream = new FileInputStream(resource);
         return doConfigure(stream, resource.toString());
      }
      catch (FileNotFoundException e) {
         throw new ResourceNotFoundException(resource + " is not found", e); //$NON-NLS-1$
      }
   }

   public ApplicationFactory configure(URL resource) throws ConfigurationException
   {
      LOG.debug("Reading configuration (url: {})" + resource.toString()); //$NON-NLS-1$
      try {
         InputStream stream = resource.openStream();
         return doConfigure(stream, resource.toString());
      }
      catch (IOException e) {
         throw new ResourceNotFoundException(resource + " is not found", e); //$NON-NLS-1$
      }
   }

   public ApplicationManager createApplicationManager()
   {
      LOG.info("Initializing ApplicationManager."); //$NON-NLS-1$
      try {
         debugConfigurationProperties();
         Environment.verify(mProperties);
         PropertiesConfiguration copy = new PropertiesConfiguration();
         copy.append(mProperties);
         Settings settings = buildSettings(copy);
         return new ApplicationManager(settings);
      }
      catch (SemantikaException e) {
         throw new ApplicationStartupException("Failed to create ApplicationManager", e); //$NON-NLS-1$
      }
   }

   private void debugConfigurationProperties()
   {
      LOG.debug("* {} = {}", //$NON-NLS-1$
            Environment.APPLICATION_FACTORY_NAME,
            mProperties.getString(Environment.APPLICATION_FACTORY_NAME));
      LOG.debug("* {} = {}", //$NON-NLS-1$
            Environment.CONNECTION_URL,
            mProperties.getString(Environment.CONNECTION_URL));
      LOG.debug("* {} = {}", //$NON-NLS-1$
            Environment.TRANSACTION_TIMEOUT,
            mProperties.getString(Environment.TRANSACTION_TIMEOUT));
      LOG.debug("* {} = {}", //$NON-NLS-1$
            Environment.TRANSACTION_FETCH_SIZE,
            mProperties.getString(Environment.TRANSACTION_FETCH_SIZE));
      LOG.debug("* {} = {}", //$NON-NLS-1$
            Environment.TRANSACTION_MAX_ROWS,
            mProperties.getString(Environment.TRANSACTION_MAX_ROWS));
      LOG.debug("* {} = {}", //$NON-NLS-1$
            Environment.ONTOLOGY_SOURCE,
            mProperties.getString(Environment.ONTOLOGY_SOURCE));
      
      String[] mappingSources = mProperties.getStringArray(Environment.MAPPING_SOURCE);
      String[] useStrictParsing = mProperties.getStringArray(Environment.STRICT_PARSING);
      for (int i = 0; i < mappingSources.length; i++) {
         LOG.debug("* {} = {} (strict-parsing={})", //$NON-NLS-1$
               Environment.MAPPING_SOURCE,
               mappingSources[i], useStrictParsing[i]);
      }
   }

   private Settings buildSettings(PropertiesConfiguration properties) throws SemantikaException
   {
      Settings settings = mSettingFactory.buildSettings(properties);
      return settings;
   }

   protected ApplicationFactory doConfigure(InputStream stream, String resourceName) throws ConfigurationException
   {
      Document doc = null;
      try {
         List<SAXParseException> errorList = new ArrayList<SAXParseException>();
         doc = mXmlHelper.createDocumentBuilder(resourceName, errorList, XmlHelper.DEFAULT_DTD_RESOLVER).parse(stream);
         if (errorList.size() != 0) {
            throw new ConfigurationException("Invalid configuration", errorList.get(0)); //$NON-NLS-1$
         }
      }
      catch (SAXException e) {
         throw new ConfigurationException("Syntax error", e); //$NON-NLS-1$
      }
      catch (IOException e) {
         throw new ConfigurationException("Configuration loading error", e); //$NON-NLS-1$
      }
      catch (ParserConfigurationException e) {
         throw new ConfigurationException("Configuration error", e); //$NON-NLS-1$
      }
      finally {
         try {
            stream.close();
         }
         catch (IOException e) {
            LOG.warn("Could not close input stream {}", resourceName); //$NON-NLS-1$
            LOG.warn("Detailed cause: {}", e.getMessage()); //$NON-NLS-1$
         }
      }
      return doConfigure(doc);
   }

   protected ApplicationFactory doConfigure(Document doc) throws ConfigurationException
   {
      /*
       * Optional but recommended
       * http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
       */
      doc.getDocumentElement().normalize();
      
      Element afNode = getElementByTagName(doc, "application-factory"); //$NON-NLS-1$
      addApplicationFactoryProperties(afNode);
      
      Element dsNode = getElementByTagName(doc, "data-source"); //$NON-NLS-1$
      addDataSourceProperties(dsNode);
      
      Element osNode = getElementByTagName(doc, "ontology-source"); //$NON-NLS-1$
      addOntologyResource(osNode);
      
      NodeList msNodeList = getElementsByTagName(doc, "mapping-source"); //$NON-NLS-1$
      addMappingResource(msNodeList);
      
      LOG.debug("ApplicationFactory is sucessfully created."); //$NON-NLS-1$
      
      return this;
   }

   private void addApplicationFactoryProperties(Element element)
   {
      String name = element.getAttribute("name"); //$NON-NLS-1$
      if (!StringUtils.isEmpty(name)) {
         mProperties.setProperty(Environment.APPLICATION_FACTORY_NAME, name);
      }
   }

   private static NodeList getElementsByTagName(Document doc, String elementName)
   {
      return doc.getElementsByTagName(elementName);
   }

   private static Element getElementByTagName(Document doc, String elementName)
   {
      return (Element) getElementsByTagName(doc, elementName).item(0);
   }

   private void addDataSourceProperties(Element parent)
   {
      NodeList properties = parent.getChildNodes();
      for (int i = 0; i < properties.getLength(); i++) {
         Node childNode = properties.item(i);
         if (childNode instanceof Element) {
            Element node = (Element) properties.item(i);
            String name = node.getAttribute("name"); //$NON-NLS-1$
            String value = node.getTextContent();
            mProperties.setProperty(name, value);
         }
      }
   }

   private void addOntologyResource(Element parent)
   {
      /*
       * The ontology-source element is an optional. The system will prepare an
       * empty ontology if users don't specify one.
       */
      if (parent != null) {
         String value = parent.getAttribute("resource"); //$NON-NLS-1$
         mProperties.setProperty(Environment.ONTOLOGY_SOURCE, value);
      }
   }

   private void addMappingResource(NodeList list)
   {
      for (int i = 0; i < list.getLength(); i++) {
         Element parent = (Element) list.item(i);
         String value = parent.getAttribute("resource"); //$NON-NLS-1$
         mProperties.addProperty(Environment.MAPPING_SOURCE, value);
         String isStrict = parent.getAttribute("strict-parsing"); //$NON-NLS-1$
         mProperties.addProperty(Environment.STRICT_PARSING, isStrict);
      }
   }

   public PropertiesConfiguration getProperties()
   {
      return mProperties;
   }
}
