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
package com.obidea.semantika.ontology;

import java.net.URI;

import com.obidea.semantika.expression.base.Iri;

public interface IOntology
{
   int getAxiomCount();

   /**
    * @deprecated since 1.8. Use {@link IOntology#containClass(Iri)} instead.
    */
   @Deprecated
   default boolean containClass(URI classUri)
   {
      return containClass(Iri.create(classUri));
   }

   boolean containClass(Iri classIri);

   /**
    * @deprecated since 1.8. Use {@link IOntology#containDataProperty(Iri)} instead.
    */
   @Deprecated
   default boolean containDataProperty(URI propertyUri)
   {
      return containDataProperty(Iri.create(propertyUri));
   }

   boolean containDataProperty(Iri propertyIri);

   /**
    * @deprecated since 1.8. Use {@link IOntology#containObjectProperty(Iri)} instead.
    */
   @Deprecated
   default boolean containObjectProperty(URI propertyUri)
   {
      return containObjectProperty(Iri.create(propertyUri));
   }

   boolean containObjectProperty(Iri propertyiri);
}
