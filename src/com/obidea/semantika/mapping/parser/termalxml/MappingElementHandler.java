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

import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.mapping.MappingSet;
import com.obidea.semantika.mapping.base.IMapping;
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

   /* package */SqlQuery getSourceQuery()
   {
      return mSourceQuery;
   }

   private void setSubjectTermMap(ITerm template)
   {
      mSubjectTermMap = template;
   }

   /* package */ITerm getSubjectTermMap()
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
      /*
       * Create a wrapper object to make easy the object sharing for subject map
       * and predicate-object map. Use the method
       * <code>getParentElement().getSourceQuery()</code> to get this object.
       */
      final SqlQuery query = handler.getSourceQuery();
      setSourceQuery(query);
   }

   @Override
   /* package */void handleChild(SubjectMapElementHandler handler) throws MappingParserException
   {
      /*
       * Users may not define the subject class, e.g., in the case of defining
       * many-to-many relationship. If so, the handler will produce no class
       * mapping and no new mapping is added to the mapping set.
       */
      final IMapping classMapping = handler.getMapping();
      if (classMapping != null) {
         mMappingSet.add(classMapping);
      }
      
      /*
       * Save subject template object in this parent element so that it can be
       * shared to the subsequent predicate-object map handlers. Use the method
       * <code>getParentElement().getSubjectTemplate()</code> to get this
       * object.
       */
      setSubjectTermMap(handler.getSubjectTermMap());
   }

   @Override
   /* package */void handleChild(PredicateObjectMapElementHandler handler)
   {
      /*
       * The handler will always produce a property mapping if users make the
       * definition properly in the mapping file.
       */
      final IMapping propertyMapping = handler.getMapping();
      mMappingSet.add(propertyMapping);
   }
}
