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
package com.obidea.semantika.app;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.slf4j.Logger;

import com.obidea.semantika.exception.SemantikaException;
import com.obidea.semantika.util.LogUtils;

public abstract class SettingFactory
{
   protected static final Logger LOG = LogUtils.createLogger("semantika.application"); //$NON-NLS-1$

   public Settings buildSettings(PropertiesConfiguration properties) throws SemantikaException
   {
      Settings settings = new Settings();

      loadSystemProperties(properties, settings);

      LOG.info("Loading [DATABASE] object..."); //$NON-NLS-1$
      loadDatabaseFromProperties(properties, settings);

      LOG.info("Loading [ONTOLOGY] object..."); //$NON-NLS-1$
      loadOntologyFromProperties(properties, settings);

      LOG.info("Loading [MAPPING SET] object..."); //$NON-NLS-1$
      loadMappingFromProperties(properties, settings);

      return settings;
   }

   abstract void loadSystemProperties(PropertiesConfiguration properties, Settings settings) throws SemantikaException;

   abstract void loadDatabaseFromProperties(PropertiesConfiguration properties, Settings settings) throws SemantikaException;

   abstract void loadOntologyFromProperties(PropertiesConfiguration properties, Settings settings) throws SemantikaException;

   abstract void loadMappingFromProperties(PropertiesConfiguration properties, Settings settings) throws SemantikaException;
}
