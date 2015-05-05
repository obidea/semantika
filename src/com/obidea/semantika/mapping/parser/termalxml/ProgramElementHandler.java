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
package com.obidea.semantika.mapping.parser.termalxml;

import com.obidea.semantika.mapping.MappingSet;
import com.obidea.semantika.mapping.exception.MappingParserException;

public class ProgramElementHandler extends AbstractTermalElementHandler
{
   public ProgramElementHandler(TermalXmlParserHandler handler)
   {
      super(handler);
   }

   @Override
   public void endElement() throws MappingParserException
   {
      // NO-OP
   }
   
   @Override
   public void attribute(String name, String value) throws MappingParserException
   {
      // NO-OP
   }

   @Override
   protected void handleChild(MappingElementHandler handler)
   {
      final MappingSet mappingSet = handler.getMappingSet();
      getMappingSet().copy(mappingSet);
   }

   @Override
   protected void handleChild(LogicalTableElementHandler handler)
   {
      // NO-OP: Not an immediate child.
   }

   @Override
   protected void handleChild(SubjectMapElementHandler handler)
   {
      // NO-OP: Not an immediate child.
   }

   @Override
   protected void handleChild(PredicateObjectMapElementHandler handler)
   {
      // NO-OP: Not an immediate child.
   }
}
