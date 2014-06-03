package com.obidea.semantika.mapping.parser.r2rml;

import java.io.IOException;
import java.io.InputStream;

import org.openrdf.rio.turtle.TurtleParser;

import io.github.johardi.r2rmlparser.R2RmlHandler;

import com.obidea.semantika.io.IDocumentSource;
import com.obidea.semantika.knowledgebase.DefaultPrefixManager;
import com.obidea.semantika.knowledgebase.IPrefixManager;
import com.obidea.semantika.mapping.IMappingFactory.IMetaModel;
import com.obidea.semantika.mapping.IMappingSet;
import com.obidea.semantika.mapping.exception.MappingParserException;
import com.obidea.semantika.mapping.parser.AbstractMappingParser;
import com.obidea.semantika.mapping.parser.MappingParserConfiguration;

public class R2RmlParser extends AbstractMappingParser
{
   public R2RmlParser(IMetaModel metaModel)
   {
      super(metaModel);
   }

   @Override
   public IPrefixManager parse(IDocumentSource inputDocument, IMappingSet mappingSet,
         MappingParserConfiguration configuration) throws MappingParserException, IOException
   {
      R2RmlTriplesMapHandler triplesMapHandler = new R2RmlTriplesMapHandler(mappingSet, getMetaModel(), configuration);
      InputStream is = inputDocument.getInputStream();
      try {
         DefaultPrefixManager prefixManager = new DefaultPrefixManager();
         TurtleParser parser = new TurtleParser();
         R2RmlHandler parserHandler = new R2RmlHandler();
         parser.setRDFHandler(parserHandler);
         parser.parse(is, configuration.getBaseIri());
         triplesMapHandler.handle(parserHandler.getTriplesMaps());
         prefixManager.setAll(parserHandler.getPrefixMapper());
         return prefixManager;
      }
      catch (Exception e) {
         throw new MappingParserException(e.getMessage());
      }
      finally {
         if (is != null) {
            is.close();
         }
      }
   }

   @Override
   public String getSyntax()
   {
      return "R2RML/Turtle"; //$NON-NLS-1$
   }
}
