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

import org.semanticweb.owlapi.model.OWLObject;

/**
 * Helper class to construct the OWL ontology structure.
 */
public class OwlNode<E extends OWLObject>
{
   private E mEntity;
   private OwlNode<E> mParent;
   private OwlNodeSet<E> mChildren = new OwlNodeSet<E>();

   public OwlNode()
   {
      // NO-OP
   }

   public OwlNode(E entity)
   {
      mEntity = entity;
   }

   E getEntity()
   {
      return mEntity;
   }

   void setParent(OwlNode<E> parent)
   {
      mParent = parent;
      parent.setChild(this);
   }

   OwlNode<E> getParent()
   {
      return mParent;
   }

   void setChild(OwlNode<E> child)
   {
      mChildren.addNode(child);
   }

   OwlNodeSet<E> getChildren()
   {
      return mChildren;
   }

   OwlNode<E> findNode(E entity)
   {
      if (entity.equals(getEntity())) {
         return this;
      }
      OwlNode<E> toReturn = null;
      Iterator<OwlNode<E>> iter = getChildren().iterator();
      while (iter.hasNext()) {
         OwlNode<E> node = iter.next();
         toReturn = node.findNode(entity);
         if (toReturn != null) {
            return toReturn;
         }
      }
      return toReturn;
   }

   boolean isRoot()
   {
      return mParent == null;
   }

   boolean isLeaf()
   {
      return mChildren.isEmpty();
   }

   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("Parent: ").append(mParent.getEntity().toString());
      sb.append("\n");
      sb.append("Entity: ").append(getEntity().toString());
      return sb.toString();
   }
}
