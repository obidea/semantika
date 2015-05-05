/*
 * Copyright (c) 2013-2015 Josef Hardi <josef.hardi@gmail.com>
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
package com.obidea.semantika.knowledgebase.processor;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.util.OWLObjectVisitorAdapter;

import com.obidea.semantika.mapping.MutableMappingSet;
import com.obidea.semantika.mapping.base.IMapping;
import com.obidea.semantika.util.Serializer;

public abstract class OwlObjectHandler extends OWLObjectVisitorAdapter
{
   protected MutableMappingSet mMappingSet;

   protected URI mSignature;

   protected boolean mIsInverse = false;

   protected Set<IMapping> getMappingsForClassExpression()
   {
      return getMappingsForAnyExpression();
   }

   protected Set<IMapping> getMappingsForPropertyExpression()
   {
      return getMappingsForAnyExpression();
   }

   private Set<IMapping> getMappingsForAnyExpression()
   {
      Set<IMapping> toReturn = new HashSet<IMapping>();
      if (mMappingSet.contains(mSignature)) {
         for (IMapping mapping : mMappingSet.get(mSignature)) {
            toReturn.add((IMapping) Serializer.copy(mapping));
         }
      }
      return toReturn;
   }

   protected void addInferredMapping(IMapping mapping)
   {
      mMappingSet.add(mapping);
   }

   protected void resetIsInverse()
   {
      mIsInverse = false;
   }

   /*
    * Implementation of class and property expression visitor
    */

   @Override
   public void visit(OWLClass expr)
   {
      mSignature = expr.getIRI().toURI();
   }

   @Override
   public void visit(OWLDataProperty expr)
   {
      mSignature = expr.getIRI().toURI();
   }

   @Override
   public void visit(OWLObjectProperty expr)
   {
      mSignature = expr.getIRI().toURI();
   }

   @Override
   public void visit(OWLObjectInverseOf expr)
   {
      mIsInverse = true;
      mSignature = expr.getInverse().asOWLObjectProperty().getIRI().toURI();
   }

   @Override
   public void visit(OWLDataSomeValuesFrom expr)
   {
      expr.getProperty().accept(this);
   }

   @Override
   public void visit(OWLObjectSomeValuesFrom expr)
   {
      expr.getProperty().accept(this);
   }
}
