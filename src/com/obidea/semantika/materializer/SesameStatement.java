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

import static java.lang.String.format;

import java.net.URISyntaxException;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import com.obidea.semantika.datatype.AbstractXmlType;
import com.obidea.semantika.datatype.XmlDataTypeProfile;
import com.obidea.semantika.datatype.primitive.XsdString;
import com.obidea.semantika.util.TemplateStringHelper;

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
         case TriplesProjection.DATA_URI:
            return mValueFactory.createURI(getUriString(mSubjectValue));
         case TriplesProjection.DATA_LITERAL:
            throw new TriplesStatementException("Subject cannot be literal"); //$NON-NLS-1$
         default:
            throw new TriplesStatementException(format("Illegal data category (%s)", category)); //$NON-NLS-1$
      }
   }

   @Override
   public URI getPredicate()
   {
      int category = mProjection.getDataCategory(2);
      switch (category) {
         case TriplesProjection.DATA_URI:
            return mValueFactory.createURI(mPredicateValue);
         case TriplesProjection.DATA_LITERAL:
            throw new TriplesStatementException("Predicate cannot be literal"); //$NON-NLS-1$
         default:
            throw new TriplesStatementException(format("Illegal data category (%s)", category)); //$NON-NLS-1$
      }
   }

   @Override
   public Value getObject()
   {
      int category = mProjection.getDataCategory(3);
      switch (category) {
         case TriplesProjection.DATA_URI:
            return mValueFactory.createURI(getUriString(mObjectValue));
         case TriplesProjection.DATA_LITERAL:
            try {
               AbstractXmlType<?> xmlType = XmlDataTypeProfile.getXmlDatatype(mProjection.getDatatype(3));
               Object value = xmlType.getValue(mObjectValue);
               return (xmlType instanceof XsdString) ?
                     mValueFactory.createLiteral(String.valueOf(value)) : // use syntactic sugar
                     mValueFactory.createLiteral(String.valueOf(value), mValueFactory.createURI(xmlType.getName()));
            }
            catch (Exception e) {
               throw new TriplesStatementException("Failed to create literal", e); //$NON-NLS-1$
            }
         default:
            throw new TriplesStatementException(format("Illegal data category (%s)", category)); //$NON-NLS-1$
      }
   }

   private String getUriString(String value)
   {
      String uriString = value;
      
      /*
       * Check if the given object value is a URI-template string or a URI string.
       */
      if (!validUri(uriString)) {
         /*
          * If it is a URI template string then reconstruct it to be a URI string.
          */
         uriString = TemplateStringHelper.buildUri(value);
      }
      return uriString;
   }

   /*
    * A utility method to check if the given URI string is a valid URI construction.
    * This method is used exclusively to check if the given value is in the form of
    * a URI template string or an already URI string.
    */
   private static boolean validUri(String uriString)
   {
      try {
         new java.net.URI(uriString);
      }
      catch (URISyntaxException e) {
         return false;
      }
      return true;
   }
}
