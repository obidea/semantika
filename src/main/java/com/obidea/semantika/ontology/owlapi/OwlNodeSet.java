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

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLObject;

public class OwlNodeSet<E extends OWLObject> implements Iterable<OwlNode<E>>
{
   private final Set<OwlNode<E>> mNodes = new LinkedHashSet<OwlNode<E>>();

   public OwlNodeSet()
   {
      // NO-OP
   }

   public void addEntity(E entity)
   {
      addNode(getNode(entity));
   }

   public void addNode(OwlNode<E> node)
   {
      mNodes.add(node);
   }

   public void addAllNodes(Set<OwlNode<E>> nodes)
   {
      mNodes.addAll(nodes);
   }

   public void addNodeSet(OwlNodeSet<E> nodeSet)
   {
      if (nodeSet != null && !nodeSet.isEmpty()) {
         mNodes.addAll(nodeSet.getNodes());
      }
   }

   public OwlNode<E> getNode(E entity)
   {
      return new OwlNode<E>(entity);
   }

   public Set<OwlNode<E>> getNodes()
   {
      return new LinkedHashSet<OwlNode<E>>(mNodes);
   }

   public boolean isEmpty()
   {
      return mNodes.isEmpty();
   }

   @Override
   public Iterator<OwlNode<E>> iterator()
   {
      return mNodes.iterator();
   }
}
