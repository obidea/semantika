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
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;

/* package */class OwlPropertyStructureHandler extends OWLAxiomVisitorAdapter implements IOwlStructureHandler<OWLPropertyExpression>
{
   private OwlNode<OWLPropertyExpression> mRoot = new OwlNode<OWLPropertyExpression>();

   private Set<OWLPropertyExpression> mPropertyCache = new HashSet<OWLPropertyExpression>();

   public OwlPropertyStructureHandler(AbstractOwlOntology ontology)
   {
      OWLOntology ont = ontology.asOwlOntology();
      for (OWLAxiom axiom : ont.getRBoxAxioms(Imports.INCLUDED)) {
         axiom.accept(this);
      }
   }

   @Override
   public void visit(OWLSubObjectPropertyOfAxiom axiom)
   {
      addSubPropertyAxiom(axiom);
   }

   @Override
   public void visit(OWLSubDataPropertyOfAxiom axiom)
   {
      addSubPropertyAxiom(axiom);
   }

   private void addSubPropertyAxiom(OWLSubPropertyAxiom<?> axiom)
   {
      OWLPropertyExpression subProperty = axiom.getSubProperty();
      OWLPropertyExpression superProperty = axiom.getSuperProperty();
      
      OwlNode<OWLPropertyExpression> subClassNode = createNode(subProperty);
      OwlNode<OWLPropertyExpression> superClassNode = createNode(superProperty);
      
      if (mPropertyCache.contains(subProperty)) {
         subClassNode = findNode(subProperty);
         superClassNode.setParent(subClassNode.getParent());
         subClassNode.setParent(superClassNode);
      }
      else if (mPropertyCache.contains(superProperty)) {
         superClassNode = findNode(superProperty);
         subClassNode.setParent(superClassNode);
      }
      else {
         superClassNode.setParent(mRoot);
         subClassNode.setParent(superClassNode);
      }
      mPropertyCache.add(subProperty);
      mPropertyCache.add(superProperty);
   }

   @Override
   public OwlNode<OWLPropertyExpression> findNode(OWLPropertyExpression entity)
   {
      return mRoot.findNode(entity);
   }

   @Override
   public OwlNodeSet<OWLPropertyExpression> getAncestors(OWLPropertyExpression entity, boolean direct)
   {
      OwlNodeSet<OWLPropertyExpression> ancestors = new OwlNodeSet<OWLPropertyExpression>();
      OwlNode<OWLPropertyExpression> node = mRoot.findNode(entity);
      if (node != null) {
         OwlNode<OWLPropertyExpression> parent = node.getParent();
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
   public OwlNodeSet<OWLPropertyExpression> getDescendants(OWLPropertyExpression entity, boolean direct)
   {
      OwlNodeSet<OWLPropertyExpression> descendants = new OwlNodeSet<OWLPropertyExpression>();
      OwlNode<OWLPropertyExpression> node = mRoot.findNode(entity);
      if (node != null) {
         OwlNodeSet<OWLPropertyExpression> children = node.getChildren();
         if (direct) {
            descendants.addNodeSet(children);
         }
         else {
            collectChildren(children, descendants);
         }
      }
      return descendants;
   }

   private static void collectChildren(OwlNodeSet<OWLPropertyExpression> children, OwlNodeSet<OWLPropertyExpression> descendants)
   {
      if (children.isEmpty()) {
         return;
      }
      descendants.addNodeSet(children);
      OwlNodeSet<OWLPropertyExpression> newChildren = new OwlNodeSet<OWLPropertyExpression>();
      for (OwlNode<OWLPropertyExpression> node : children.getNodes()) {
         newChildren.addNodeSet(node.getChildren());
      }
      collectChildren(newChildren, descendants);
   }

   private static OwlNode<OWLPropertyExpression> createNode(OWLPropertyExpression entity)
   {
      return new OwlNode<OWLPropertyExpression>(entity);
   }
}
