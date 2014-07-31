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

public class SubjectMapElementHandler extends AbstractMappingElementHandler
{
   private URI mSubjectUri;
   private ITerm mSubjectMapValue;

   public SubjectMapElementHandler(TermalXmlParserHandler handler)
   {
      super(handler);
   }

   @Override
   protected void decideDefaultTermType()
   {
      mTermType = R2RmlVocabulary.IRI.getUri();
   }

   @Override
   public void startElement(String name) throws MappingParserException
   {
      super.startElement(name);
   }

   @Override
   public void endElement() throws MappingParserException
   {
      processSubjectMap();
      getParentElement().handleChild(this);
   }

   @Override
   public void attribute(String name, String value) throws MappingParserException
   {
      if (name.equals(R2RmlVocabulary.CLASS.getQName())) {
         setSubjectUri(getUri(value));
      }
      else if (name.equals(R2RmlVocabulary.COLUMN.getQName())) {
         setTermMap(TermMap.COLUMN_VALUE);
         setValue(value);
      }
      else if (name.equals(R2RmlVocabulary.TEMPLATE.getQName())) {
         setTermMap(TermMap.TEMPLATE_VALUE);
         setValue(value);
      }
      else if (name.equals(R2RmlVocabulary.SUBJECT.getQName())) {
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

   private void processSubjectMap() throws MappingParserException
   {
      switch (getTermMap()) {
         case COLUMN_VALUE:
            setSubjectMapValue(getColumnTerm(getValue(), getTermType(), getDatatype()));
            break;
         case CONSTANT_VALUE:
            setSubjectMapValue(getLiteralTerm(getValue(), getTermType(), getDatatype()));
            break;
         case TEMPLATE_VALUE:
            setSubjectMapValue(getTemplateTerm(getValue(), getTermType(), getDatatype()));
            break;
      }
   }

   private void setSubjectUri(URI classUri) throws ClassNotFoundException, PrefixNotFoundException
   {
      checkClassSignature(classUri);
      mSubjectUri = classUri;
   }

   public URI getSubjectUri()
   {
      return mSubjectUri;
   }

   private void setSubjectMapValue(ITerm subjectTerm)
   {
      mSubjectMapValue = subjectTerm;
   }

   public ITerm getSubjectMapValue()
   {
      return mSubjectMapValue;
   }

   /*
    * Private utility methods
    */

   private void checkClassSignature(URI uri) throws ClassNotFoundException
   {
      if (uri == null) {
         return; // if input is null then nothing to check
      }
      if (isStrictParsing()) {
         if (getOntology().containClass(uri)) {
            return;
         }
         throw classNotFoundException(uri);
      }
   }
}
