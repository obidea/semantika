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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.ResultSet;

import com.obidea.semantika.io.IDocumentTarget;

public abstract class AbstractRdfMaterializer implements IRdfMaterializer
{
   @Override
   public int materializeTuples(ResultSet resultSet, TriplesProjection resultMetadata, IDocumentTarget outputDocument) throws MaterializationException
   {
      int counter = 0;
      if (outputDocument.isWriterAvailable()) {
         try {
            Writer writer = outputDocument.getWriter();
            counter = materializeTuples(resultSet, resultMetadata, writer);
            writer.close();
         }
         catch (IOException e) {
            throw new MaterializationException(e);
         }
      }
      else if (outputDocument.isOutputStreamAvailable()) {
         Writer writer = null;
         try {
            writer = new BufferedWriter(new OutputStreamWriter(outputDocument.getOutputStream(), "UTF-8")); //$NON-NLS-1$
            counter = materializeTuples(resultSet, resultMetadata, writer);
            writer.close();
         }
         catch (IOException e) {
            throw new MaterializationException(e);
         }
      }
      else {
         throw new MaterializationException("No writer or output stream are available to store the tuples."); //$NON-NLS-1$
      }
      return counter;
   }

   protected abstract int materializeTuples(ResultSet resultSet, TriplesProjection projector, Writer writer) throws MaterializationException;
}
