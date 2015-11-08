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

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;

import com.obidea.semantika.ontology.owlapi.AbstractOwlOntology;

public class OwlOntology extends AbstractOwlOntology
{
   public OwlOntology(OWLOntology ontology)
   {
      super(ontology);
   }

   @Override
   public int getAxiomCount()
   {
      return mOwlOntology.getAxiomCount();
   }

   @Override
   public boolean containClass(URI classUri)
   {
      return mOwlOntology.containsClassInSignature(toIri(classUri), Imports.INCLUDED);
   }

   @Override
   public boolean containObjectProperty(URI propertyUri)
   {
      return mOwlOntology.containsObjectPropertyInSignature(toIri(propertyUri), Imports.INCLUDED);
   }

   @Override
   public boolean containDataProperty(URI propertyUri)
   {
      return mOwlOntology.containsDataPropertyInSignature(toIri(propertyUri), Imports.INCLUDED);
   }
}
