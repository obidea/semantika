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
package com.obidea.semantika.mapping.parser.termalxml;

import java.net.URI;

import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.mapping.exception.MappingParserException;

public class PredicateObjectMapElementHandler extends AbstractMappingElementHandler
{
   private URI mPropertyUri;
   private ITerm mObjectMapValue;

   public PredicateObjectMapElementHandler(TermalXmlParserHandler handler)
   {
      super(handler);
   }

   @Override
   public void startElement(String name) throws MappingParserException
   {
      super.startElement(name);
   }

   @Override
   public void endElement() throws MappingParserException
   {
      processObjectMap();
      getParentElement().handleChild(this);
   }

   @Override
   public void attribute(String name, String value) throws MappingParserException
   {
      if (name.equals(R2RmlVocabulary.PREDICATE.getQName())) {
         setPropertyUri(getUri(value));
      }
      else if (name.equals(R2RmlVocabulary.COLUMN.getQName())) {
         setTermMap(TermMap.COLUMN_VALUE);
         setValue(value);
      }
      else if (name.equals(R2RmlVocabulary.TEMPLATE.getQName())) {
         setTermMap(TermMap.TEMPLATE_VALUE);
         setValue(value);
      }
      else if (name.equals(R2RmlVocabulary.OBJECT.getQName())) {
         setTermMap(TermMap.CONSTANT_VALUE);
         setValue(value);
      }
      else if (name.equals(R2RmlVocabulary.TERM_TYPE.getQName())) {
         setTermType(getUri(value).toString());
      }
      else if (name.equals(R2RmlVocabulary.DATAYPE.getQName())) {
         setDatatype(getUri(value).toString());
      }
      else {
         throw unknownXmlAttributeException(name);
      }
   }

   private void processObjectMap() throws MappingParserException
   {
      switch (getTermMap()) {
         case COLUMN_VALUE:
            setObjectMapValue(getColumnTerm(getValue(), getTermType(), getDatatype()));
            break;
         case CONSTANT_VALUE:
            setObjectMapValue(getLiteralTerm(getValue(), getTermType(), getDatatype()));
            break;
         case TEMPLATE_VALUE:
            setObjectMapValue(getTemplateTerm(getValue(), getTermType(), getDatatype()));
            break;
      }
   }

   private void setPropertyUri(URI propertyUri) throws PrefixNotFoundException, PropertyNotFoundException
   {
      checkProperty(propertyUri);
      mPropertyUri = propertyUri;
   }

   public URI getPropertyUri()
   {
      return mPropertyUri;
   }

   private void setObjectMapValue(ITerm objectTerm)
   {
      mObjectMapValue = objectTerm;
   }

   public ITerm getObjectMapValue()
   {
      return mObjectMapValue;
   }

   /*
    * Private utility methods
    */

   private void checkProperty(URI uri) throws PropertyNotFoundException
   {
      if (isStrictParsing()) {
         if (getOntology().containObjectProperty(uri) || getOntology().containDataProperty(uri)) {
            return;
         }
         throw propertyNotFoundException(uri);
      }
   }
}
