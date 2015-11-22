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
package com.obidea.semantika.mapping;

import java.net.URI;
import java.util.List;

import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.expression.base.Iri;
import com.obidea.semantika.expression.base.TermUtils;
import com.obidea.semantika.mapping.base.ClassMapping;
import com.obidea.semantika.mapping.base.PropertyMapping;
import com.obidea.semantika.mapping.base.TripleAtom;
import com.obidea.semantika.mapping.base.sql.SqlQuery;
import com.obidea.semantika.util.Namespaces;

public class MappingObjectFactory
{
   private static MappingObjectFactory sInstance = new MappingObjectFactory();

   public static MappingObjectFactory getInstance()
   {
      return sInstance;
   }

   /**
    * @deprecated since 1.8. Use {@link MappingObjectFactory#createClassMapping(Iri, SqlQuery, ITerm)} instead.
    */
   @Deprecated
   public ClassMapping createClassMapping(URI classSignature, SqlQuery sourceQuery, ITerm subjectMapValue)
   {
      return createClassMapping(Iri.create(classSignature), sourceQuery, subjectMapValue);
   }

   public ClassMapping createClassMapping(Iri classSignature, SqlQuery sourceQuery, ITerm subjectMapValue)
   {
      ClassMapping cm = new ClassMapping(classSignature, sourceQuery);
      cm.setSubjectMapValue(subjectMapValue);
      return cm;
   }

   /**
    * @deprecated since 1.8. Use {@link MappingObjectFactory#createPropertyMapping(Iri, SqlQuery, ITerm, ITerm)} instead.
    */
   @Deprecated
   public PropertyMapping createPropertyMapping(URI propertySignature, SqlQuery sourceQuery, ITerm subjectMapValue, ITerm objectMapValue)
   {
      return createPropertyMapping(Iri.create(propertySignature), sourceQuery, subjectMapValue, objectMapValue);
   }

   public PropertyMapping createPropertyMapping(Iri propertySignature, SqlQuery sourceQuery, ITerm subjectMapValue, ITerm objectMapValue)
   {
      PropertyMapping pm = new PropertyMapping(propertySignature, sourceQuery);
      pm.setSubjectMapValue(subjectMapValue);
      pm.setObjectMapValue(objectMapValue);
      return pm;
   }

   /**
    * @deprecated since 1.8. User {@link MappingObjectFactory#createClassAtom(Iri, ITerm)} instead.
    */
   @Deprecated
   public TripleAtom createClassAtom(URI classSignature, ITerm subjectMapValue)
   {
      return createClassAtom(Iri.create(classSignature), subjectMapValue);
   }

   public TripleAtom createClassAtom(Iri classSignature, ITerm subjectMapValue)
   {
      String predicate = Namespaces.RDF + "type"; //$NON-NLS-1$
      String object = classSignature.toString();
      return new TripleAtom(subjectMapValue, TermUtils.makeIriReference(predicate), TermUtils.makeIriReference(object));
   }

   /**
    * @deprecated since 1.8. Use {@link MappingObjectFactory#createPropertyAtom(Iri, ITerm, ITerm)} instead.
    */
   @Deprecated
   public TripleAtom createPropertyAtom(URI propertySignature, ITerm subjectMapValue, ITerm objectMapValue)
   {
      return createPropertyAtom(Iri.create(propertySignature), subjectMapValue, objectMapValue);
   }

   public TripleAtom createPropertyAtom(Iri propertySignature, ITerm subjectMapValue, ITerm objectMapValue)
   {
      String predicate = propertySignature.toString();
      return new TripleAtom( subjectMapValue, TermUtils.makeIriReference(predicate), objectMapValue);
   }

   /**
    * @deprecated since 1.8. Use {@link MappingObjectFactory#createIriTemplate(String, List)} instead.
    */
   @Deprecated
   public UriTemplate createUriTemplate(String templateString, List<? extends ITerm> parameters)
   {
      return new UriTemplate(templateString, parameters);
   }

   public IriTemplate createIriTemplate(String templateString, List<? extends ITerm> parameters)
   {
      return new IriTemplate(templateString, parameters);
   }

   private MappingObjectFactory()
   {
      // NO-OP: To prevent class instantiation
   }
}
