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
package com.obidea.semantika.app;

import java.io.File;
import java.io.InputStream;
import java.net.URI;

import org.coode.owlapi.functionalparser.OWLFunctionalSyntaxParserFactory;
import org.coode.owlapi.functionalrenderer.OWLFunctionalSyntaxOntologyStorer;
import org.coode.owlapi.owlxml.renderer.OWLXMLOntologyStorer;
import org.coode.owlapi.owlxmlparser.OWLXMLParserFactory;
import org.semanticweb.owlapi.io.OWLParserFactoryRegistry;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.NonMappingOntologyIRIMapper;

import uk.ac.manchester.cs.owl.owlapi.EmptyInMemOWLOntologyFactory;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLOntologyManagerImpl;
import uk.ac.manchester.cs.owl.owlapi.ParsableOWLOntologyFactory;

import com.obidea.semantika.ontology.IOntology;
import com.obidea.semantika.ontology.OwlOntology;
import com.obidea.semantika.ontology.exception.OntologyCreationException;

public class OntologyLoader 
{
   private OWLOntologyManager mOwlManager;

   static {
      OWLParserFactoryRegistry registry = OWLParserFactoryRegistry.getInstance();
      registry.registerParserFactory(new OWLXMLParserFactory());
      registry.registerParserFactory(new OWLFunctionalSyntaxParserFactory());
   }

   public OntologyLoader()
   {
      mOwlManager = createOWLOntologyManager();
   }

   public static OWLOntologyManager createOWLOntologyManager()
   {
      OWLOntologyManager ontologyManager = new OWLOntologyManagerImpl(new OWLDataFactoryImpl());
      ontologyManager.addOntologyStorer(new OWLXMLOntologyStorer());
      ontologyManager.addOntologyStorer(new OWLFunctionalSyntaxOntologyStorer());
      ontologyManager.addIRIMapper(new NonMappingOntologyIRIMapper());
      ontologyManager.addOntologyFactory(new EmptyInMemOWLOntologyFactory());
      ontologyManager.addOntologyFactory(new ParsableOWLOntologyFactory());
      return ontologyManager;
   }

   public IOntology createEmptyOntology() throws OntologyCreationException
   {
      try {
         return new OwlOntology(mOwlManager.createOntology());
      }
      catch (OWLOntologyCreationException e) {
         throw new OntologyCreationException("Failed to create an empty ontology.", e); //$NON-NLS-1$
      }
   }

   public IOntology loadOntologyFromDocument(File file) throws OntologyCreationException
   {
      try {
         OWLOntology owlOntology = mOwlManager.loadOntologyFromOntologyDocument(file);
         return new OwlOntology(owlOntology);
      }
      catch (OWLOntologyCreationException e) {
         throw new OntologyCreationException("Failed load ontology from file.", e); //$NON-NLS-1$
      }
   }

   public IOntology loadOntologyFromDocument(InputStream inputStream) throws OntologyCreationException
   {
      try {
         OWLOntology owlOntology = mOwlManager.loadOntologyFromOntologyDocument(inputStream);
         return new OwlOntology(owlOntology);
      }
      catch (OWLOntologyCreationException e) {
         throw new OntologyCreationException("Failed load ontology from input stream.", e); //$NON-NLS-1$
      }
   }

   public IOntology loadOntologyFromDocument(URI documentUri) throws OntologyCreationException
   {
      try {
         IRI documentIri = IRI.create(documentUri);
         OWLOntology owlOntology = mOwlManager.loadOntologyFromOntologyDocument(documentIri);
         return new OwlOntology(owlOntology);
      }
      catch (OWLOntologyCreationException e) {
         throw new OntologyCreationException("Failed load ontology from URI.", e); //$NON-NLS-1$
      }
   }
}
