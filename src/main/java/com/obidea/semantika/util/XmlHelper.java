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
package com.obidea.semantika.util;

import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import com.obidea.semantika.app.SemantikaEntityResolver;

public class XmlHelper
{
   public static final EntityResolver DEFAULT_DTD_RESOLVER = new SemantikaEntityResolver();

   private DocumentBuilder mDocBuilder;

   private static final Logger LOG = LogUtils.createLogger("semantika.utility"); //$NON-NLS-1$

   public DocumentBuilder createDocumentBuilder(String file, List<SAXParseException> errorList, EntityResolver entityResolver) throws ParserConfigurationException
   {
      if (mDocBuilder == null) {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         mDocBuilder = factory.newDocumentBuilder();
      }
      mDocBuilder.setEntityResolver(entityResolver);
      mDocBuilder.setErrorHandler(new ErrorLogger(file, errorList));
      return mDocBuilder;
   }

   public static class ErrorLogger implements ErrorHandler
   {
      private String mFile;
      private List<SAXParseException> mErrorList;

      ErrorLogger(String file, List<SAXParseException> errorList)
      {
         this.mFile = file;
         this.mErrorList = errorList;
      }

      public void error(SAXParseException error)
      {
         LOG.error("Error parsing XML \"{}\" (line: {})", mFile, error.getLineNumber()); //$NON-NLS-1$
         LOG.error("Detailed cause: {}", error.getMessage()); //$NON-NLS-1$
         mErrorList.add(error);
      }

      public void fatalError(SAXParseException error)
      {
         error(error);
      }

      public void warning(SAXParseException warn)
      {
         LOG.warn("Warning parsing XML \"{}\" (line: {})", mFile, warn.getLineNumber()); //$NON-NLS-1$
         LOG.warn("Detailed cause: {}", warn.getMessage()); //$NON-NLS-1$
      }
   }
}
