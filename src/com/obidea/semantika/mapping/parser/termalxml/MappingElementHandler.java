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
import com.obidea.semantika.mapping.MappingSet;
import com.obidea.semantika.mapping.base.IClassMapping;
import com.obidea.semantika.mapping.base.IPropertyMapping;
import com.obidea.semantika.mapping.base.sql.SqlQuery;
import com.obidea.semantika.mapping.exception.MappingParserException;

public class MappingElementHandler extends AbstractTermalElementHandler
{
   private SqlQuery mSourceQuery;

   private ITerm mSubjectTermMap;

   private MappingSet mMappingSet = new MappingSet();

   public MappingElementHandler(TermalXmlParserHandler handler)
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
      getParentElement().handleChild(this);
   }

   @Override
   public void attribute(String name, String value) throws MappingParserException
   {
      if (name.equals(TermalVocabulary.ID.getQName())) {
         // NO-OP: The name is not required for constructing the mapping rule.
      }
      else {
         throw unknownXmlAttributeException(name);
      }
   }

   @Override
   public MappingSet getMappingSet()
   {
      return mMappingSet;
   }

   private void setSourceQuery(SqlQuery sourceQuery)
   {
      mSourceQuery = sourceQuery;
   }

   protected SqlQuery getSourceQuery()
   {
      return mSourceQuery;
   }

   private void setSubjectMapValue(ITerm template)
   {
      mSubjectTermMap = template;
   }

   protected ITerm getSubjectMapValue()
   {
      return mSubjectTermMap;
   }

   @Override
   /* package */void handleChild(MappingElementHandler handler)
   {
      // NO-OP: No recursive child
   }

   @Override
   /* package */void handleChild(LogicalTableElementHandler handler)
   {
      final SqlQuery query = handler.getSourceQuery();
      setSourceQuery(query);
   }

   @Override
   /* package */void handleChild(SubjectMapElementHandler handler) throws MappingParserException
   {
      final ITerm subjectTerm = handler.getSubjectMapValue();
      setSubjectMapValue(subjectTerm);
      
      final URI classUri = handler.getClassUri();
      if (classUri != null) {
         IClassMapping cm = getMappingObjectFactory().createClassMapping(classUri, getSourceQuery());
         cm.setSubjectMapValue(subjectTerm);
         mMappingSet.add(cm);
      }
   }

   @Override
   /* package */void handleChild(PredicateObjectMapElementHandler handler)
   {
      final URI propertyUri = handler.getPropertyUri();
      IPropertyMapping pm = getMappingObjectFactory().createPropertyMapping(propertyUri, getSourceQuery());
      pm.setSubjectMapValue(getSubjectMapValue());
      pm.setObjectMapValue(handler.getObjectMapValue());
      mMappingSet.add(pm);
   }
}
