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
import com.obidea.semantika.mapping.base.IClassMapping;
import com.obidea.semantika.mapping.base.sql.SqlQuery;
import com.obidea.semantika.mapping.exception.MappingParserException;
import com.obidea.semantika.mapping.parser.R2RmlVocabulary;

public class SubjectMapElementHandler extends AbstractMappingElementHandler
{
   private URI mClassSignature;
   private SqlQuery mSourceQuery;

   private ITerm mSubjectMapValue;

   public SubjectMapElementHandler(TermalXmlParserHandler handler)
   {
      super(handler);
   }

   @Override
   public void startElement(String name) throws MappingParserException
   {
      super.startElement(name);
      mSourceQuery = getParentElement().getSourceQuery();
   }

   @Override
   public void endElement() throws MappingParserException
   {
      setMapping(createMapping());
      getParentElement().handleChild(this);
   }

   @Override
   public void attribute(String name, String value) throws MappingParserException
   {
      if (name.equals(R2RmlVocabulary.CLASS.getQName())) {
         URI classUri = getUri(value);
         checkClassSignature(classUri);
         mClassSignature = classUri;
      }
      else if (name.equals(R2RmlVocabulary.TEMPLATE.getQName())) {
         mSubjectMapValue = getUriTemplateFunction(value);
      }
      else if (name.equals(R2RmlVocabulary.SUBJECT.getQName())) {
         mSubjectMapValue = getUriReference(value);
      }
      else {
         throw unknownXmlAttributeException(name);
      }
   }

   @Override
   protected IClassMapping createMapping()
   {
      if (mClassSignature == null) {
         return null; // returns null if class atom isn't stated
      }
      else {
         IClassMapping cm = getMappingObjectFactory().createClassMapping(mClassSignature, mSourceQuery);
         cm.setSubjectMapValue(mSubjectMapValue); // subject template
         return cm;
      }
   }

   protected ITerm getSubjectTermMap()
   {
      return mSubjectMapValue;
   }

   /*
    * Private utility methods
    */

   private void checkClassSignature(URI uri) throws ClassNotFoundException
   {
      if (isStrictParsing()) {
         if (getOntology().containClass(uri)) {
            return;
         }
         throw classNotFoundException(uri);
      }
   }
}
