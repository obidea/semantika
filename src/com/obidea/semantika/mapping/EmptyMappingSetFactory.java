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
package com.obidea.semantika.mapping;

import com.obidea.semantika.exception.IllegalOperationException;
import com.obidea.semantika.io.IDocumentSource;
import com.obidea.semantika.mapping.exception.MappingCreationException;
import com.obidea.semantika.mapping.parser.MappingParserConfiguration;

public class EmptyMappingSetFactory extends AbstractMappingFactory
{
   @Override
   public IMappingSet loadMappingSet(IDocumentSource document, IMappingLoadHandler mediator,
         MappingParserConfiguration configuration) throws MappingCreationException
   {
      throw new IllegalOperationException("Cannot load mapping file."); //$NON-NLS-1$
   }

   @Override
   public boolean canLoad(IDocumentSource document)
   {
      return false;
   }
}
