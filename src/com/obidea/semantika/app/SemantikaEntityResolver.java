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

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.obidea.semantika.exception.ConfigurationException;
import com.obidea.semantika.util.ConfigHelper;
import com.obidea.semantika.util.LogUtils;

public class SemantikaEntityResolver implements EntityResolver
{
   private static final String SEMANTIKA_NAMESPACE = "http://www.obidea.com/semantika/dtd/"; //$NON-NLS-1$

   private static final String USER_NAMESPACE = "classpath://"; //$NON-NLS-1$

   private static final Logger LOG = LogUtils.createLogger("semantika.application"); //$NON-NLS-1$

   @Override
   public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
   {
      if (systemId == null) {
         return null; // use default behavior
      }
      
      if (systemId.startsWith(SEMANTIKA_NAMESPACE)) {
         String path = "com/obidea/semantika/" + systemId.substring(SEMANTIKA_NAMESPACE.length());
         InputStream dtdStream = resolveInSemantikaNamespace(path);
         if (dtdStream == null) {
            LOG.error("Unable to locate {} on classpath", systemId); //$NON-NLS-1$
         }
         else {
            InputSource source = new InputSource(dtdStream);
            source.setPublicId(publicId);
            source.setSystemId(systemId);
            return source;
         }
      }
      else if (systemId.startsWith(USER_NAMESPACE)) {
         String path = systemId.substring(USER_NAMESPACE.length());
         InputStream dtdStream = resolveInLocalNamespace(path);
         if (dtdStream == null) {
            LOG.error("Unable to locate {} on classpath", systemId); //$NON-NLS-1$
         }
         else {
            InputSource source = new InputSource(dtdStream);
            source.setPublicId(publicId);
            source.setSystemId(systemId);
            return source;
         }
      }
      return null; // use default behavior
   }

   private InputStream resolveInSemantikaNamespace(String path)
   {
      return this.getClass().getClassLoader().getResourceAsStream(path);
   }

   private InputStream resolveInLocalNamespace(String path) throws IOException
   {
      try {
         return ConfigHelper.getUserResourceInputStream(path);
      }
      catch (ConfigurationException e) {
         throw new IOException(e);
      }
   }
}
