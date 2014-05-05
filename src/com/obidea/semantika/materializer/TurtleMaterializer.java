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
package com.obidea.semantika.materializer;

import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.turtle.TurtleWriter;

public class TurtleMaterializer extends AbstractRdfMaterializer
{
   @Override
   public String getFormatName()
   {
      return "Turtle (Terse RDF Triple Language)"; //$NON-NLS-1$
   }

   @Override
   protected int materializeTuples(ResultSet resultSet, TriplesProjection projection, Writer writer) throws MaterializationException
   {
      int counter = 0;
      TurtleWriter handler = new TurtleWriter(writer);
      try {
         handler.startRDF();
         while (resultSet.next()) {
            Statement stmt = new SesameStatement(projection,
                  resultSet.getString("subject"), //$NON-NLS-1$
                  resultSet.getString("predicate"), //$NON-NLS-1$
                  resultSet.getString("object")); //$NON-NLS-1$
            handler.handleStatement(stmt);
            counter++; // count triples
         }
         handler.endRDF();
      }
      catch (SQLException e) {
         throw new MaterializationException(e);
      }
      catch (RDFHandlerException e) {
         throw new MaterializationException(e);
      }
      return counter;
   }
}
