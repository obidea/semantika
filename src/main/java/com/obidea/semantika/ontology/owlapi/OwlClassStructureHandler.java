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
package com.obidea.semantika.ontology.owlapi;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;

/* package */class OwlClassStructureHandler extends OWLAxiomVisitorAdapter implements IOwlStructureHandler<OWLClassExpression>
{
   private OwlNode<OWLClassExpression> mRoot = new OwlNode<OWLClassExpression>();

   private Set<OWLClassExpression> mClassCache = new HashSet<OWLClassExpression>();

   private OwlPropertyStructureHandler mPropertyStructureHandler;
   private OWLDataFactory mOwlDataFactory;

   public OwlClassStructureHandler(AbstractOwlOntology ontology)
   {
      mPropertyStructureHandler = ontology.getPropertyStructureHandler();
      mOwlDataFactory = ontology.getOwlDataFactory();
      OWLOntology ont = ontology.asOwlOntology();
      for (OWLAxiom axiom : ont.getTBoxAxioms(Imports.INCLUDED)) {
         axiom.accept(this);
      }
   }

   @Override
   public void visit(OWLSubClassOfAxiom axiom)
   {
      addSubClassAxiom(axiom);
   }

   @Override
   public void visit(OWLObjectPropertyDomainAxiom axiom)
   {
      addSubClassAxiom(axiom.asOWLSubClassOfAxiom());
      
      OWLObjectPropertyExpression op = axiom.getProperty();
      OwlNodeSet<OWLPropertyExpression> descendants = mPropertyStructureHandler.getDescendants(op, false);
      for (OwlNode<OWLPropertyExpression> node : descendants.getNodes()) {
         op = (OWLObjectPropertyExpression) node.getEntity();
         axiom = mOwlDataFactory.getOWLObjectPropertyDomainAxiom(op, axiom.getDomain());
         addSubClassAxiom(axiom.asOWLSubClassOfAxiom());
      }
   }

   @Override
   public void visit(OWLObjectPropertyRangeAxiom axiom)
   {
      addSubClassAxiom(asSubClassOfAxiom(axiom));
      
      OWLObjectPropertyExpression op = axiom.getProperty();
      OwlNodeSet<OWLPropertyExpression> descendants = mPropertyStructureHandler.getDescendants(op, false);
      for (OwlNode<OWLPropertyExpression> node : descendants.getNodes()) {
         op = (OWLObjectPropertyExpression) node.getEntity();
         axiom = mOwlDataFactory.getOWLObjectPropertyRangeAxiom(op, axiom.getRange());
         addSubClassAxiom(asSubClassOfAxiom(axiom));
      }
   }

   @Override
   public void visit(OWLDataPropertyDomainAxiom axiom)
   {
      addSubClassAxiom(axiom.asOWLSubClassOfAxiom());
      
      OWLDataPropertyExpression op = axiom.getProperty();
      OwlNodeSet<OWLPropertyExpression> descendants = mPropertyStructureHandler.getDescendants(op, false);
      for (OwlNode<OWLPropertyExpression> node : descendants.getNodes()) {
         op = (OWLDataPropertyExpression) node.getEntity();
         axiom = mOwlDataFactory.getOWLDataPropertyDomainAxiom(op, axiom.getDomain());
         addSubClassAxiom(axiom.asOWLSubClassOfAxiom());
      }
   }

   private void addSubClassAxiom(OWLSubClassOfAxiom axiom)
   {
      OWLClassExpression subClass = axiom.getSubClass();
      OWLClassExpression superClass = axiom.getSuperClass();
      
      OwlNode<OWLClassExpression> subClassNode = createNode(subClass);
      OwlNode<OWLClassExpression> superClassNode = createNode(superClass);
      
      if (mClassCache.contains(subClass)) {
         subClassNode = findNode(subClass);
         superClassNode.setParent(subClassNode.getParent());
         subClassNode.setParent(superClassNode);
      }
      else if (mClassCache.contains(superClass)) {
         superClassNode = findNode(superClass);
         subClassNode.setParent(superClassNode);
      }
      else {
         superClassNode.setParent(mRoot);
         subClassNode.setParent(superClassNode);
      }
      mClassCache.add(subClass);
      mClassCache.add(superClass);
   }

   @Override
   public OwlNode<OWLClassExpression> findNode(OWLClassExpression entity)
   {
      return mRoot.findNode(entity);
   }

   @Override
   public OwlNodeSet<OWLClassExpression> getAncestors(OWLClassExpression entity, boolean direct)
   {
      OwlNodeSet<OWLClassExpression> ancestors = new OwlNodeSet<OWLClassExpression>();
      OwlNode<OWLClassExpression> node = mRoot.findNode(entity);
      if (node != null) {
         OwlNode<OWLClassExpression> parent = node.getParent();
         while (!parent.isRoot()) {
            ancestors.addNode(parent);
            if (direct) {
               break;
            }
            parent = parent.getParent();
         }
      }
      return ancestors;
   }

   @Override
   public OwlNodeSet<OWLClassExpression> getDescendants(OWLClassExpression entity, boolean direct)
   {
      OwlNodeSet<OWLClassExpression> descendants = new OwlNodeSet<OWLClassExpression>();
      OwlNode<OWLClassExpression> node = mRoot.findNode(entity);
      if (node != null) {
         OwlNodeSet<OWLClassExpression> children = node.getChildren();
         if (direct) {
            descendants.addNodeSet(children);
         }
         else {
            collectChildren(children, descendants);
         }
      }
      return descendants;
   }

   private static void collectChildren(OwlNodeSet<OWLClassExpression> children, OwlNodeSet<OWLClassExpression> descendants)
   {
      if (children.isEmpty()) {
         return;
      }
      descendants.addNodeSet(children);
      OwlNodeSet<OWLClassExpression> newChildren = new OwlNodeSet<OWLClassExpression>();
      for (OwlNode<OWLClassExpression> node : children.getNodes()) {
         newChildren.addNodeSet(node.getChildren());
      }
      collectChildren(newChildren, descendants);
   }

   private static OwlNode<OWLClassExpression> createNode(OWLClassExpression entity)
   {
      return new OwlNode<OWLClassExpression>(entity);
   }

   private OWLSubClassOfAxiom asSubClassOfAxiom(OWLObjectPropertyRangeAxiom axiom)
   {
      OWLObjectInverseOf inverseExpression = mOwlDataFactory.getOWLObjectInverseOf(axiom.getProperty());
      OWLClassExpression classExpression = axiom.getRange();
      OWLClassExpression sub = mOwlDataFactory.getOWLObjectSomeValuesFrom(inverseExpression, mOwlDataFactory.getOWLThing());
      return mOwlDataFactory.getOWLSubClassOfAxiom(sub, classExpression);
   }
}
