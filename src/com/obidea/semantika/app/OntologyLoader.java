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
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.coode.owlapi.functionalparser.OWLFunctionalSyntaxParserFactory;
import org.coode.owlapi.functionalrenderer.OWLFunctionalSyntaxOntologyStorer;
import org.coode.owlapi.owlxml.renderer.OWLXMLOntologyStorer;
import org.coode.owlapi.owlxmlparser.OWLXMLParserFactory;
import org.semanticweb.owlapi.io.OWLParserFactoryRegistry;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.DLExpressivityChecker;
import org.semanticweb.owlapi.util.DLExpressivityChecker.Construct;
import org.semanticweb.owlapi.util.NonMappingOntologyIRIMapper;
import org.slf4j.Logger;

import uk.ac.manchester.cs.owl.owlapi.EmptyInMemOWLOntologyFactory;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLOntologyManagerImpl;
import uk.ac.manchester.cs.owl.owlapi.ParsableOWLOntologyFactory;

import com.obidea.semantika.ontology.IOntology;
import com.obidea.semantika.ontology.OwlOntology;
import com.obidea.semantika.ontology.exception.OntologyCreationException;
import com.obidea.semantika.util.LogUtils;

public class OntologyLoader 
{
   private OWLOntologyManager mOwlManager;

   private static final Logger LOG = LogUtils.createLogger("semantika.application"); //$NON-NLS-1$

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
         return createOwlOntology(owlOntology);
      }
      catch (OWLOntologyCreationException e) {
         throw new OntologyCreationException("Failed load ontology from file.", e); //$NON-NLS-1$
      }
   }

   public IOntology loadOntologyFromDocument(URL url) throws OntologyCreationException
   {
      try {
         InputStream in = url.openStream();
         OWLOntology owlOntology = mOwlManager.loadOntologyFromOntologyDocument(in);
         return createOwlOntology(owlOntology);
      }
      catch (IOException e) {
         throw new OntologyCreationException("Failed load ontology from URL resource.", e); //$NON-NLS-1$
      }
      catch (OWLOntologyCreationException e) {
         throw new OntologyCreationException("Failed load ontology from URL resource.", e); //$NON-NLS-1$
      }
   }

   public IOntology loadOntologyFromDocument(URI documentUri) throws OntologyCreationException
   {
      try {
         IRI documentIri = IRI.create(documentUri);
         OWLOntology owlOntology = mOwlManager.loadOntologyFromOntologyDocument(documentIri);
         return createOwlOntology(owlOntology);
      }
      catch (OWLOntologyCreationException e) {
         throw new OntologyCreationException("Failed load ontology from URI resource.", e); //$NON-NLS-1$
      }
   }

   private OwlOntology createOwlOntology(OWLOntology ontology)
   {
      printOntologyMetrics(ontology);
      return new OwlOntology(ontology);
   }

   private void printOntologyMetrics(OWLOntology ontology)
   {
      if (!ontology.isEmpty()) {
         LOG.debug("Parsing OWL ontology (found: {} axioms)", ontology.getAxiomCount()); //$NON-NLS-1$
         LOG.debug("* Logical axiom count = {}", ontology.getLogicalAxiomCount()); //$NON-NLS-1$
         LOG.debug("* Class axiom count = {}", ontology.getClassesInSignature().size()); //$NON-NLS-1$
         LOG.debug("* Object property axiom count = {}", ontology.getObjectPropertiesInSignature().size()); //$NON-NLS-1$
         LOG.debug("* Data property axiom count = {}", ontology.getDataPropertiesInSignature().size()); //$NON-NLS-1$
         LOG.debug("* Individual count = {}", ontology.getIndividualsInSignature().size()); //$NON-NLS-1$
         LOG.debug("* DL Expressivity = {}", getDlExpresivity(ontology)); //$NON-NLS-1$
      }
   }

   /**
    * Returns DL expressivity contained in this OWL ontology.
    */
   private String getDlExpresivity(OWLOntology ontology)
   {
      String expressivity = "N/A"; //$NON-NLS-1$
      try {
         expressivity = printExpressivity(new DLExpressivityChecker(ontology.getImportsClosure()).getConstructs());
      }
      catch (OWLException e) {
         LOG.error("Error while determining DL expressivity."); //$NON-NLS-1$
         LOG.error("Detailed cause: {}", e.getMessage()); //$NON-NLS-1$
      }
      return expressivity;
   }

   private static String printExpressivity(List<Construct> constructs)
   {
      String toReturn = ""; //$NON-NLS-1$
      for (Construct c : constructs) {
         toReturn += c.toString();
      }
      return toReturn;
   }
}
