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
package com.obidea.semantika.materializer;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import com.obidea.semantika.datatype.DataType;
import com.obidea.semantika.mapping.UriTemplateBuilder;

/* package */class SesameStatement implements Statement
{
   private static final long serialVersionUID = 629451L;

   private ValueFactory mValueFactory = ValueFactoryImpl.getInstance();

   private String mSubjectValue;
   private String mPredicateValue;
   private String mObjectValue;

   private TriplesProjection mProjection;

   public SesameStatement(TriplesProjection projection, String subjectValue, String predicateValue, String objectValue)
   {
      mSubjectValue = subjectValue;
      mPredicateValue = predicateValue;
      mObjectValue = objectValue;
      mProjection = projection;
   }

   @Override
   public Resource getContext()
   {
      return null;
   }

   @Override
   public Resource getSubject()
   {
      int category = mProjection.getDataCategory(1);
      switch (category) {
         case TriplesProjection.DATA_OBJECT_CATEGORY:
            String uriString = UriTemplateBuilder.getUri(mSubjectValue);
            return mValueFactory.createURI(uriString);
         case TriplesProjection.DATA_LITERAL_VALUE_CATEGORY:
            throw new IllegalTermTypeException("Triple subject cannot be data value"); //$NON-NLS-1$
         case TriplesProjection.DATA_URI_VALUE_CATEGORY:
            return mValueFactory.createURI(mSubjectValue);
      }
      throw new IllegalTermTypeException("Unknown data category [" + category + "]"); //$NON-NLS-1$ //$NON-NLS-2$
   }

   @Override
   public URI getPredicate()
   {
      int category = mProjection.getDataCategory(2);
      switch (category) {
         case TriplesProjection.DATA_OBJECT_CATEGORY:
            throw new IllegalTermTypeException("Triple predicate cannot be data object"); //$NON-NLS-1$
         case TriplesProjection.DATA_LITERAL_VALUE_CATEGORY:
            throw new IllegalTermTypeException("Triple predicate cannot be data value"); //$NON-NLS-1$
         case TriplesProjection.DATA_URI_VALUE_CATEGORY:
            return mValueFactory.createURI(mPredicateValue);
      }
      throw new IllegalTermTypeException("Unknown data category [" + category + "]"); //$NON-NLS-1$ //$NON-NLS-2$
   }

   @Override
   public Value getObject()
   {
      int category = mProjection.getDataCategory(3);
      switch (category) {
         case TriplesProjection.DATA_OBJECT_CATEGORY:
            String uriString = UriTemplateBuilder.getUri(mObjectValue);
            return mValueFactory.createURI(uriString);
         case TriplesProjection.DATA_LITERAL_VALUE_CATEGORY:
            String datatype = mProjection.getDatatype(3);
            if (datatype.equals(DataType.STRING)) {
               // Create a literal object without a datatype URI for string type.
               return mValueFactory.createLiteral(mObjectValue);
            }
            else {
               return mValueFactory.createLiteral(mObjectValue, mValueFactory.createURI(datatype));
            }
         case TriplesProjection.DATA_URI_VALUE_CATEGORY:
            return mValueFactory.createURI(mObjectValue);
      }
      throw new IllegalTermTypeException("Unknown data category [" + category + "]"); //$NON-NLS-1$ //$NON-NLS-2$
   }
}
