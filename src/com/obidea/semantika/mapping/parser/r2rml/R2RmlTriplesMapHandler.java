package com.obidea.semantika.mapping.parser.r2rml;

import java.util.List;

import io.github.johardi.r2rmlparser.document.PredicateObjectMap;
import io.github.johardi.r2rmlparser.document.TriplesMap;

import com.obidea.semantika.mapping.IMappingFactory.IMetaModel;
import com.obidea.semantika.mapping.IMappingSet;
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
      R2RmlMappingHandler handler = new R2RmlMappingHandler();
      handler.setBaseIri(mParsingConfiguration.getBaseIri());
      handler.setStrictParsing(mParsingConfiguration.isStrictParsing());
      handler.setOntology(mMetaModel.getOntology());
      handler.setDatabaseMetadata(mMetaModel.getDatabaseMetadata());
      return handler;
   }
}
