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
package com.obidea.semantika.ontology.owlapi;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;
import org.semanticweb.owlapi.util.DLExpressivityChecker;
import org.semanticweb.owlapi.util.DLExpressivityChecker.Construct;
import org.slf4j.Logger;

import com.obidea.semantika.ontology.IOntology;
import com.obidea.semantika.util.LogUtils;

public abstract class AbstractOwlOntology implements IOntology
{
   protected OWLOntology mOwlOntology;

   private OWLDataFactory mOwlDataFactory;

   private OwlClassStructureHandler mClassStructureHandler;
   private OwlPropertyStructureHandler mPropertyStructureHandler;

   private static final Logger LOG = LogUtils.createLogger("semantika.ontology"); //$NON-NLS-1$

   public AbstractOwlOntology(OWLOntology rootOntology)
   {
      mOwlOntology = rootOntology;
      mOwlDataFactory = rootOntology.getOWLOntologyManager().getOWLDataFactory();
      
      /*
       * Constructing the property structure must be the first priority and the continue
       * constructing the class structure.
       */
      mPropertyStructureHandler = new OwlPropertyStructureHandler(this);
      mClassStructureHandler = new OwlClassStructureHandler(this);
      
      printOntologyMetrics();
   }

   /**
    * Gets the OWL data factory to construct OWL-related objects.
    */
   /* package */OWLDataFactory getOwlDataFactory()
   {
      return mOwlDataFactory;
   }

   /**
    * Gets the class structure handler that construct the class hierarchy.
    */
   /* package */OwlClassStructureHandler getClassStructureHandler()
   {
      return mClassStructureHandler;
   }

   /**
    * Gets the property structure handler that construct the property hierarchy.
    */
   /* package */OwlPropertyStructureHandler getPropertyStructureHandler()
   {
      return mPropertyStructureHandler;
   }

   /**
    * Gets the set of classes that are the strict (potentially direct) subclasses of the specified
    * class expression.
    * 
    * @param entity
    *           The class expression whose strict (direct) subclasses are to be retrieved.
    * @param direct
    *           Specifies if the direct subclasses should be retrieved (<code>true</code>) or if the
    *           all subclasses (descendant) classes should be retrieved (<code>false</code>).
    * @return Returns a set of subclasses, or an empty set if the input entity doesn't exist.
    */
   public Set<OWLClassExpression> getSubClasses(OWLClassExpression entity, boolean direct)
   {
      Set<OWLClassExpression> toReturn = new LinkedHashSet<OWLClassExpression>();
      OwlNodeSet<OWLClassExpression> descendants = mClassStructureHandler.getDescendants(entity, direct);
      for (OwlNode<OWLClassExpression> node : descendants.getNodes()) {
         toReturn.add(node.getEntity());
      }
      return toReturn;
   }

   /**
    * Gets the set of classes that are the strict (potentially direct) super classes of the
    * specified class expression.
    * 
    * @param entity
    *           The class expression whose strict (direct) super classes are to be retrieved.
    * @param direct
    *           Specifies if the direct super classes should be retrieved (<code>true</code>) or if
    *           the all super classes (ancestors) classes should be retrieved (<code>false</code>).
    * @return Returns a set of super classes, or an empty set if the input entity doesn't exist.
    */
   public Set<OWLClassExpression> getSuperClasses(OWLClassExpression entity, boolean direct)
   {
      Set<OWLClassExpression> toReturn = new LinkedHashSet<OWLClassExpression>();
      OwlNodeSet<OWLClassExpression> ancestors = mClassStructureHandler.getAncestors(entity, direct);
      for (OwlNode<OWLClassExpression> node : ancestors.getNodes()) {
         toReturn.add(node.getEntity());
      }
      return toReturn;
   }

   /**
    * Gets the set of properties that are the strict (potentially direct) sub properties of the
    * specified property expression.
    * 
    * @param entity
    *           The property expression whose strict (direct) sub properties are to be retrieved.
    * @param direct
    *           Specifies if the direct sub properties should be retrieved (<code>true</code>) or if
    *           the all sub properties (descendant) should be retrieved (<code>false</code>).
    * @return Returns a set of sub properties, or an empty set if the input entity doesn't exist.
    */
   public Set<OWLPropertyExpression<?,?>> getSubProperties(OWLPropertyExpression<?,?> entity, boolean direct)
   {
      Set<OWLPropertyExpression<?,?>> toReturn = new LinkedHashSet<OWLPropertyExpression<?,?>>();
      OwlNodeSet<OWLPropertyExpression<?,?>> descendants = mPropertyStructureHandler.getDescendants(entity, direct);
      for (OwlNode<OWLPropertyExpression<?,?>> node : descendants.getNodes()) {
         toReturn.add(node.getEntity());
      }
      return toReturn;
   }

   /**
    * Gets the set of properties that are the strict (potentially direct) super properties of the
    * specified property expression.
    * 
    * @param entity
    *           The property expression whose strict (direct) super properties are to be retrieved.
    * @param direct
    *           Specifies if the direct super properties should be retrieved (<code>true</code>) or
    *           if the all super properties (ancestors) should be retrieved (<code>false</code>).
    * @return Returns a set of super properties, or an empty set if the input entity doesn't exist.
    */
   public Set<OWLPropertyExpression<?,?>> getSuperProperties(OWLPropertyExpression<?,?> entity, boolean direct)
   {
      Set<OWLPropertyExpression<?,?>> toReturn = new LinkedHashSet<OWLPropertyExpression<?,?>>();
      OwlNodeSet<OWLPropertyExpression<?,?>> ancestors = mPropertyStructureHandler.getAncestors(entity, direct);
      for (OwlNode<OWLPropertyExpression<?,?>> node : ancestors.getNodes()) {
         toReturn.add(node.getEntity());
      }
      return toReturn;
   }

   /**
    * Gets the set of subclass axioms that build the hierarchy structure from specified class
    * expression down to its all descendants.
    * 
    * @param entity
    *           The class expression whose subclasses are to be traced.
    * @param includeSelf
    *           Specifies if the given class expression is included in the returned set.
    * @return Returns a set of subclass axioms.
    */
   public Set<OWLSubClassOfAxiom> traceDescendants(OWLClassExpression entity, boolean includeSelf)
   {
      Set<OWLSubClassOfAxiom> toReturn = new LinkedHashSet<OWLSubClassOfAxiom>();
      
      OwlNodeSet<OWLClassExpression> descendants = new OwlNodeSet<OWLClassExpression>();
      if (includeSelf) {
         descendants.addNode(mClassStructureHandler.findNode(entity));
      }
      descendants.addNodeSet(mClassStructureHandler.getDescendants(entity, false));
      for (OwlNode<OWLClassExpression> node : descendants.getNodes()) {
         if (!node.getParent().isRoot()) {
            OWLClassExpression subClass = node.getEntity();
            OWLClassExpression superClass = node.getParent().getEntity();
            OWLSubClassOfAxiom ax = mOwlDataFactory.getOWLSubClassOfAxiom(subClass, superClass);
            toReturn.add(ax);
         }
      }
      return toReturn;
   }

   /**
    * Gets the set of sub property axioms that build the hierarchy structure from specified property
    * expression down to its all descendants.
    * 
    * @param entity
    *           The property expression whose sub properties are to be traced.
    * @param includeSelf
    *           Specifies if the given property expression is included in the returned set.
    * @return Returns a set of sub property axioms.
    */
   public Set<OWLSubPropertyAxiom<?>> traceDescendants(OWLPropertyExpression<?,?> entity, boolean includeSelf)
   {
      Set<OWLSubPropertyAxiom<?>> toReturn = new LinkedHashSet<OWLSubPropertyAxiom<?>>();
      
      OwlNodeSet<OWLPropertyExpression<?,?>> descendants = new OwlNodeSet<OWLPropertyExpression<?,?>>();
      if (includeSelf) {
         descendants.addNode(mPropertyStructureHandler.findNode(entity));
      }
      descendants.addNodeSet(mPropertyStructureHandler.getDescendants(entity, false));
      for (OwlNode<OWLPropertyExpression<?,?>> node : descendants.getNodes()) {
         if (!node.getParent().isRoot()) {
            if (entity.isObjectPropertyExpression()) {
               OWLObjectPropertyExpression subProperty = (OWLObjectPropertyExpression) node.getEntity();
               OWLObjectPropertyExpression superProperty = (OWLObjectPropertyExpression) node.getParent().getEntity();
               OWLSubObjectPropertyOfAxiom ax = mOwlDataFactory.getOWLSubObjectPropertyOfAxiom(subProperty, superProperty);
               toReturn.add(ax);
            }
            else { // else is data property expression
               OWLDataPropertyExpression subProperty = (OWLDataPropertyExpression) node.getEntity();
               OWLDataPropertyExpression superProperty = (OWLDataPropertyExpression) node.getParent().getEntity();
               OWLSubDataPropertyOfAxiom ax = mOwlDataFactory.getOWLSubDataPropertyOfAxiom(subProperty, superProperty);
               toReturn.add(ax);
            }
         }
      }
      return toReturn;
   }

   /**
    * Gets the set of subclass axioms that build the hierarchy structure from specified class
    * expression up to its all ancestors.
    * 
    * @param entity
    *           The class expression whose super classes are to be traced.
    * @param includeSelf
    *           Specifies if the given class expression is included in the returned set.
    * @return Returns a set of subclass axioms.
    */
   public Set<OWLSubClassOfAxiom> traceAncestors(OWLClassExpression entity, boolean includeSelf)
   {
      Set<OWLSubClassOfAxiom> toReturn = new LinkedHashSet<OWLSubClassOfAxiom>();
      
      OwlNodeSet<OWLClassExpression> ancestors = new OwlNodeSet<OWLClassExpression>();
      if (includeSelf) {
         ancestors.addNode(mClassStructureHandler.findNode(entity));
      }
      ancestors.addNodeSet(mClassStructureHandler.getAncestors(entity, false));
      for (OwlNode<OWLClassExpression> node : ancestors.getNodes()) {
         if (!node.getParent().isRoot()) {
            OWLClassExpression subClass = node.getEntity();
            OWLClassExpression superClass = node.getParent().getEntity();
            OWLSubClassOfAxiom ax = mOwlDataFactory.getOWLSubClassOfAxiom(subClass, superClass);
            toReturn.add(ax);
         }
      }
      return toReturn;
   }

   /**
    * Gets the set of sub property axioms that build the hierarchy structure from specified property
    * expression up to its all ancestors.
    * 
    * @param entity
    *           The property expression whose super properties are to be traced.
    * @param includeSelf
    *           Specifies if the given property expression is included in the returned set.
    * @return Returns a set of sub property axioms.
    */
   public Set<OWLSubPropertyAxiom<?>> traceAncestors(OWLPropertyExpression<?,?> entity, boolean includeSelf)
   {
      Set<OWLSubPropertyAxiom<?>> toReturn = new LinkedHashSet<OWLSubPropertyAxiom<?>>();
      
      OwlNodeSet<OWLPropertyExpression<?,?>> ancestors = new OwlNodeSet<OWLPropertyExpression<?,?>>();
      if (includeSelf) {
         ancestors.addNode(mPropertyStructureHandler.findNode(entity));
      }
      ancestors.addNodeSet(mPropertyStructureHandler.getAncestors(entity, false));
      for (OwlNode<OWLPropertyExpression<?,?>> node : ancestors.getNodes()) {
         if (!node.getParent().isRoot()) {
            if (entity.isObjectPropertyExpression()) {
               OWLObjectPropertyExpression subProperty = (OWLObjectPropertyExpression) node.getEntity();
               OWLObjectPropertyExpression superProperty = (OWLObjectPropertyExpression) node.getParent().getEntity();
               OWLSubObjectPropertyOfAxiom ax = mOwlDataFactory.getOWLSubObjectPropertyOfAxiom(subProperty, superProperty);
               toReturn.add(ax);
            }
            else { // else is data property expression
               OWLDataPropertyExpression subProperty = (OWLDataPropertyExpression) node.getEntity();
               OWLDataPropertyExpression superProperty = (OWLDataPropertyExpression) node.getParent().getEntity();
               OWLSubDataPropertyOfAxiom ax = mOwlDataFactory.getOWLSubDataPropertyOfAxiom(subProperty, superProperty);
               toReturn.add(ax);
            }
         }
      }
      return toReturn;
   }

   /**
    * Returns this ontology as an OWL ontology object.
    */
   public OWLOntology asOwlOntology()
   {
      return mOwlOntology;
   }

   /**
    * A utility method to convert URI to IRI object.
    * 
    * @param uri
    *           A <code>java.net.URI</code> object to convert.
    * @return A <code>org.semanticweb.owlapi.model.IRI</code> object.
    */
   protected static IRI toIri(URI uri)
   {
      return IRI.create(uri);
   }

   /**
    * Returns DL expressivity contained in this OWL ontology.
    */
   public String getDlExpresivity()
   {
      String expressivity = "N/A"; //$NON-NLS-1$
      try {
         expressivity = printExpressivity(new DLExpressivityChecker(mOwlOntology.getImportsClosure()).getConstructs());
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

   private void printOntologyMetrics()
   {
      if (!mOwlOntology.isEmpty()) {
         LOG.debug("Parsing OWL ontology (found: {} axioms)", getAxiomCount()); //$NON-NLS-1$
         LOG.debug("* Logical axiom count = {}", mOwlOntology.getLogicalAxiomCount()); //$NON-NLS-1$
         LOG.debug("* Class axiom count = {}", mOwlOntology.getClassesInSignature().size()); //$NON-NLS-1$
         LOG.debug("* Object property axiom count = {}", mOwlOntology.getObjectPropertiesInSignature().size()); //$NON-NLS-1$
         LOG.debug("* Data property axiom count = {}", mOwlOntology.getDataPropertiesInSignature().size()); //$NON-NLS-1$
         LOG.debug("* Individual count = {}", mOwlOntology.getIndividualsInSignature().size()); //$NON-NLS-1$
         LOG.debug("* DL Expressivity = {}", getDlExpresivity()); //$NON-NLS-1$
      }
   }
}
