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
package com.obidea.semantika.mapping.parser.r2rml;

import java.util.List;

import io.github.johardi.r2rmlparser.document.PredicateObjectMap;
import io.github.johardi.r2rmlparser.document.TriplesMap;

import com.obidea.semantika.mapping.IMappingSet;
import com.obidea.semantika.mapping.IMetaModel;
import com.obidea.semantika.mapping.MutableMappingSet;
import com.obidea.semantika.mapping.parser.MappingParserConfiguration;

public class R2RmlTriplesMapHandler
{
   private MutableMappingSet mMappingSet;
   private IMetaModel mMetaModel;
   private MappingParserConfiguration mParsingConfiguration;

   public R2RmlTriplesMapHandler(IMappingSet mappingSet, IMetaModel metaModel, MappingParserConfiguration configuration)
   {
      mMappingSet = (MutableMappingSet) mappingSet;
      mMetaModel = metaModel;
      mParsingConfiguration = configuration;
   }

   public void handle(List<TriplesMap> triplesMaps)
   {
      for (TriplesMap triplesMap : triplesMaps) {
         R2RmlMappingHandler handler = createMappingHandler();
         triplesMap.getLogicalTable().accept(handler);
         triplesMap.getSubjectMap().accept(handler);
         for (PredicateObjectMap predicateObjectMap : triplesMap.getPredicateObjectMaps()) {
            predicateObjectMap.accept(handler);
         }
         mMappingSet.addAll(handler.getMappings());
      }
   }

   /*
    * Private utility methods
    */

   private R2RmlMappingHandler createMappingHandler()
   {
      R2RmlMappingHandler handler = new R2RmlMappingHandler(mMetaModel);
      handler.setBaseIri(mParsingConfiguration.getBaseIri());
      handler.setStrictParsing(mParsingConfiguration.isStrictParsing());
      return handler;
   }
}
