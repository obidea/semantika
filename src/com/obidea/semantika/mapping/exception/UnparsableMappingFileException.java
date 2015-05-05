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
package com.obidea.semantika.mapping.exception;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.obidea.semantika.mapping.parser.IMappingParser;

public class UnparsableMappingFileException extends MappingCreationException
{
   private static final long serialVersionUID = 629451L;

   private URI mDocumentURI;

   private Map<IMappingParser, MappingParserException> mExceptions;

   public UnparsableMappingFileException(URI documentURI, Map<IMappingParser, MappingParserException> exceptions)
   {
      super("Could not parse mapping program from document: " + documentURI); //$NON-NLS-1$
      mDocumentURI = documentURI;
      mExceptions = new LinkedHashMap<IMappingParser, MappingParserException>(exceptions);
   }

   @Override
   public String getMessage()
   {
      StringBuilder msg = new StringBuilder();
      msg.append("Problem while parsing "); //$NON-NLS-1$
      msg.append(mDocumentURI); //$NON-NLS-1$
      msg.append("\n"); //$NON-NLS-1$
      msg.append("Could not parse the mapping file. Either a suitable parser could not be found or parsing failed.\n"); //$NON-NLS-1$
      msg.append("See error logs below for explanation.\n\n"); //$NON-NLS-1$
      msg.append("The following parsers were tried:\n"); //$NON-NLS-1$
      int counter = 1;
      for (IMappingParser parser : mExceptions.keySet()) {
         msg.append(counter);
         msg.append(") "); //$NON-NLS-1$
         msg.append(parser.getClass().getSimpleName());
         msg.append("\n"); //$NON-NLS-1$
         msg.append("   ");
         msg.append(mExceptions.get(parser).getMessage());
         msg.append("\n");
         counter++;
      }
      return msg.toString();
   }

   /**
    * Gets the mapping document URI.
    * 
    * @return The mapping document IRI
    */
   public URI getDocumentURI()
   {
      return mDocumentURI;
   }

   /**
    * Gets a map that lists the parsers (that were used to parse the mapping program)
    * and the errors that they generated.
    * 
    * @return The map of parsers and their errors.
    */
   public Map<IMappingParser, MappingParserException> getExceptions()
   {
      return Collections.unmodifiableMap(mExceptions);
   }
}
