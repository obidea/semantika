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

import com.obidea.semantika.datatype.AbstractXmlType;
import com.obidea.semantika.datatype.DataType;
import com.obidea.semantika.datatype.TypeConversion;
import com.obidea.semantika.datatype.XmlDataTypeProfile;
import com.obidea.semantika.datatype.exception.UnsupportedDataTypeException;
import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.mapping.base.IPropertyMapping;
import com.obidea.semantika.mapping.base.sql.SqlColumn;
import com.obidea.semantika.mapping.base.sql.SqlQuery;
import com.obidea.semantika.mapping.exception.DataTypeOverrideException;
import com.obidea.semantika.mapping.exception.MappingParserException;
import com.obidea.semantika.mapping.parser.R2RmlVocabulary;

public class PredicateObjectMapElementHandler extends AbstractMappingElementHandler
{
   private URI mPropertySignature;
   private SqlQuery mSourceQuery;

   private ITerm mSubjectMapValue;
   private ITerm mObjectMapValue;

   public PredicateObjectMapElementHandler(TermalXmlParserHandler handler)
   {
      super(handler);
   }

   @Override
   public void startElement(String name) throws MappingParserException
   {
      super.startElement(name);
      mSubjectMapValue = getParentElement().getSubjectTermMap();
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
      if (name.equals(R2RmlVocabulary.PREDICATE.getQName())) {
         URI propertyUri = getUri(value);
         checkProperty(propertyUri);
         mPropertySignature = propertyUri;
      }
      else if (name.equals(R2RmlVocabulary.DATAYPE.getQName())) {
         String userDatatype = getUri(value).toString();
         overrideObjectDatatype(userDatatype);
      }
      else if (name.equals(R2RmlVocabulary.COLUMN.getQName())) {
         mObjectMapValue = getColumnExpression(value);
      }
      else if (name.equals(R2RmlVocabulary.TEMPLATE.getQName())) {
         mObjectMapValue = getUriTemplateFunction(value);
      }
      else if (name.equals(R2RmlVocabulary.OBJECT.getQName())) {
         mObjectMapValue = getUriReference(value);
      }
      else if (name.equals(R2RmlVocabulary.TERM_TYPE.getQName())) {
         overrideTermType(value);
      }
      else {
         throw unknownXmlAttributeException(name);
      }
   }

   @Override
   protected IPropertyMapping createMapping()
   {
      IPropertyMapping pm = getMappingObjectFactory().createPropertyMapping(mPropertySignature, mSourceQuery);
      pm.setSubjectMapValue(mSubjectMapValue); // subject template
      pm.setObjectMapValue(mObjectMapValue);
      return pm;
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

   private void overrideTermType(String value) throws UnsupportedTermTypeException, UnknownTermTypeException
   {
      if (mObjectMapValue instanceof SqlColumn) {
         SqlColumn columnMap = (SqlColumn) mObjectMapValue;
         if (value.equals(R2RmlVocabulary.IRI.getQName())) {
            columnMap.setUserDatatype(DataType.ANY_URI);
         }
         else if (value.equals(R2RmlVocabulary.LITERAL.getQName())) {
            columnMap.setUserDatatype(DataType.PLAIN_LITERAL);
         }
         else if (value.equals(R2RmlVocabulary.BLANK_NODE.getQName())) {
            throw unsupportedTermTypeException(value);
         }
         else {
            throw unknownTermTypeException(value);
         }
      }
      else {
         LOG.warn("rr:termType is only applicable to column-based term map"); //$NON-NLS-1$
         return;
      }
   }

   private void overrideObjectDatatype(String newDatatype) throws UnknownXmlDataTypeException, DataTypeOverrideException
   {
      if (mObjectMapValue instanceof SqlColumn) {
         SqlColumn columnMap = (SqlColumn) mObjectMapValue;
         String oldDatatype = columnMap.getDatatype();
         if (!oldDatatype.equals(newDatatype)) {
            checkTypeConversion(oldDatatype, newDatatype);
            columnMap.setUserDatatype(newDatatype);
         }
      }
      else {
         LOG.warn("rr:datatype is only applicable to column-based term map"); //$NON-NLS-1$
         return;
      }
   }

   private void checkTypeConversion(String oldDatatype, String newDatatype) throws UnknownXmlDataTypeException, DataTypeOverrideException
   {
      AbstractXmlType<?> sourceType = getXmlDatatype(oldDatatype);
      AbstractXmlType<?> targetType = getXmlDatatype(newDatatype);
      boolean pass = TypeConversion.verify(sourceType, targetType);
      if (!pass) {
         throw datatypeOverrideException(oldDatatype, newDatatype);
      }
   }

   private AbstractXmlType<?> getXmlDatatype(String datatypeUri) throws UnknownXmlDataTypeException
   {
      try {
         return XmlDataTypeProfile.getXmlDatatype(datatypeUri);
      }
      catch (UnsupportedDataTypeException e) {
         throw unknownXmlDataTypeException(datatypeUri);
      }
   }
}
