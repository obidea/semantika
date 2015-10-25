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
package com.obidea.semantika.mapping.parser;

import java.io.IOException;

import com.obidea.semantika.io.IDocumentSource;
import com.obidea.semantika.knowledgebase.IPrefixManager;
import com.obidea.semantika.mapping.IMappingSet;
import com.obidea.semantika.mapping.exception.MappingParserException;

public interface IMappingParser
{
   /**
    * Parses the input document and put the results into the input mapping
    * program.
    * 
    * @param inputDocument
    *           The input source which points to the file location. Depending on
    *           the implementor, the document source can provide a
    *           <code>Reader</code>, <code>InputStream</code> or
    *           <code>URI</code> to read from.
    * @param mappingProgram
    *           The container in which the parsed objects will be put into.
    * @param configuration
    *           A configuration object that provides various generic options to
    *           the parser.
    * @return A <code>IPrefixManager</code> which stores the prefix and
    *         namespace definitions used by the mappings.
    * @throws MappingParserException
    *            if there was a problem when parsing the input document. This
    *            indicates an error in the syntax that the parser couldn't
    *            recognize.
    * @throws IOException
    *            if there was a problem related to input/output when parsing the
    *            input document.
    */
   public IPrefixManager parse(IDocumentSource inputDocument, IMappingSet mappingProgram,
         MappingParserConfiguration configuration) throws MappingParserException, IOException;

   /**
    * Returns mapping syntax name.
    */
   public String getSyntax();
}
