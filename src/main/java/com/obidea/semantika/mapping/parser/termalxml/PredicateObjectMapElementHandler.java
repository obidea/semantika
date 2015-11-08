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
package com.obidea.semantika.mapping.parser.termalxml;

import java.net.URI;

import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.mapping.exception.MappingParserException;

public class PredicateObjectMapElementHandler extends AbstractMappingElementHandler
{
   private URI mPredicateUri;
   private ITerm mObjectMapValue;

   public PredicateObjectMapElementHandler(TermalXmlParserHandler handler)
   {
      super(handler);
   }

   @Override
   protected void decideDefaultTermType()
   {
      switch (getTermMap()) {
         case COLUMN_VALUE: mTermType = R2RmlVocabulary.LITERAL.getUri(); break;
         case CONSTANT_VALUE:
         case TEMPLATE_VALUE: mTermType = R2RmlVocabulary.IRI.getUri(); break;
      }
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
         setPredicateUri(getUri(value));
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

   @Override
   protected void setDatatype(String datatype)
   {
      super.setDatatype(datatype);
      setTermTypeAsLiteral(); // if data type is explicitly stated then term type = Literal
   }

   @Override
   protected void setLanguage(String language)
   {
      super.setLanguage(language);
      setTermTypeAsLiteral(); // if language tag is explicitly stated then term type = Literal
   }

   private void setTermTypeAsLiteral()
   {
      /*
       * Do not override user-defined term type
       */
      if (!bUserDefinedTermType) {
         mTermType = R2RmlVocabulary.LITERAL.getUri();
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

   private void setPredicateUri(URI propertyUri) throws PrefixNotFoundException, PropertyNotFoundException
   {
      checkPropertySignature(propertyUri);
      mPredicateUri = propertyUri;
   }

   public URI getPredicateUri()
   {
      return mPredicateUri;
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

   private void checkPropertySignature(URI uri) throws PropertyNotFoundException
   {
      if (uri == null) {
         throw new IllegalArgumentException("Missing property name"); //$NON-NLS-1$
      }
      if (isStrictParsing()) {
         if (getOntology().containObjectProperty(uri) || getOntology().containDataProperty(uri)) {
            return;
         }
         throw propertyNotFoundException(uri);
      }
   }
}
