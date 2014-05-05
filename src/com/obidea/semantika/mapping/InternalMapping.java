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
package com.obidea.semantika.mapping;

import java.net.URI;
import java.util.Set;

import com.obidea.semantika.expression.base.UriReference;
import com.obidea.semantika.mapping.base.IClassMapping;
import com.obidea.semantika.mapping.base.IMapping;
import com.obidea.semantika.mapping.base.IPropertyMapping;
import com.obidea.semantika.mapping.base.MappingVisitorAdapter;
import com.obidea.semantika.mapping.base.TripleAtom;

/* package */class InternalMapping extends AbstractInternalMapping
{
   private static final long serialVersionUID = 629451L;

   private transient AddMappingVisitor mAddMappingVisitor = new AddMappingVisitor();
   private transient RemoveMappingVisitor mRemoveMappingVisitor = new RemoveMappingVisitor();

   /**
    * Notifies the internal about mapping addition.
    * 
    * @param mapping
    *          the mapping that is being added.
    * @return <code>true</code> if the addition was successful.
    */
   public void addMapping(IMapping mapping)
   {
      mapping.accept(mAddMappingVisitor);
   }

   /**
    * Notifies the internal about mapping removal.
    * 
    * @param mapping
    *          the mapping that is being removed.
    * @return <code>false</code> if nothing was removed.
    */
   public void removeMapping(IMapping mapping)
   {
      mapping.accept(mRemoveMappingVisitor);
   }

   /**
    * Checks if the internal keeps mappings with the given <code>signature</code>.
    * <p>
    * A mapping signature is the predicate URI that identifies a class or a
    * property in the ontology.
    * 
    * @param signature
    *           class or property URI
    * @return Returns <code>true</code> if the internal keeps the mappings
    *         with the given signature, or <code>false</code> otherwise.
    */
   public boolean contains(URI signature)
   {
      return containsKey(getMappingBySignature(), signature);
   }

   public Set<URI> getMappingSignatures()
   {
      return getKeyset(getMappingBySignature());
   }

   /**
    * Gets all mappings with the same <code>signature</code>.
    * <p>
    * A mapping signature is the predicate URI that identifies a class or a
    * property in the ontology.
    *
    * @param signature
    *           class or property URI
    * @return A set of mappings with the same signature.
    */
   public Set<IMapping> getMappingsBySignature(URI signature)
   {
      return getValues(getMappingBySignature(), signature);
   }

   /**
    * Gets all mappings with the same <code>type</code>.
    * 
    * @param type
    *           The mapping type. Possible types are:
    *           <ul>
    *           <li><code>MappingType.CLASS_MAPPING</code></li>
    *           <li><code>MappingType.PROPERTY_MAPPING</code></li>
    *           </ul>
    * @return A set of mappings with the same type.
    */
   public Set<IMapping> getMappingsByType(MappingType<?> type)
   {
      return getValues(getMappingByType(), type);
   }

   /**
    * Gets all the mappings.
    *
    * @return a set of mappings, or an empty set if the mapping doesn't exist.
    *         The set is read-only.
    */
   public Set<IMapping> getMappings()
   {
      return getAllValues(getMappingByType());
   }

   /**
    * Counts the mappings known by the internal
    *
    * @return the count number.
    */
   public int getMappingCount()
   {
      return getMappings().size();
   }

   /**
    * Counts the mappings with the same <code>signature</code>.
    *
    * @return the count number.
    */
   public int getMappingCountBySignature(URI signature)
   {
      final Set<IMapping> mappings = getValues(getMappingBySignature(), signature);
      if (mappings.isEmpty()) {
         return 0;
      }
      return mappings.size();
   }

   /**
    * Counts the mappings with the same <code>type</code>.
    *
    * @return the count number.
    */
   public int getMappingCountByType(MappingType<?> type)
   {
      final Set<IMapping> mappings = getValues(getMappingByType(), type);
      if (mappings.isEmpty()) {
         return 0;
      }
      return mappings.size();
   }

   /**
    * Check if the internal has mappings stored.
    * 
    * @return <code>true</code> if there are mappings store, or
    *         <code>false</code> otherwise.
    */
   public boolean isEmpty()
   {
      return getMappingCount() == 0 ? true : false;
   }

   class AddMappingVisitor extends MappingVisitorAdapter
   {
      @Override
      public void visit(IClassMapping mapping)
      {
         add(getMappingBySignature(), getClassMappingSignature(mapping), mapping);
         add(getMappingByType(), MappingType.CLASS_MAPPING, mapping);
      }

      @Override
      public void visit(IPropertyMapping mapping)
      {
         add(getMappingBySignature(), getPropertyMappingSignature(mapping), mapping);
         add(getMappingByType(), MappingType.PROPERTY_MAPPING, mapping);
      }
   }

   class RemoveMappingVisitor extends MappingVisitorAdapter
   {
      @Override
      public void visit(IClassMapping mapping)
      {
         remove(getMappingBySignature(), getClassMappingSignature(mapping), mapping);
         remove(getMappingByType(), MappingType.CLASS_MAPPING, mapping);
      }

      @Override
      public void visit(IPropertyMapping mapping)
      {
         remove(getMappingBySignature(), getPropertyMappingSignature(mapping), mapping);
         remove(getMappingByType(), MappingType.PROPERTY_MAPPING, mapping);
      }
   }

   /*
    * The mapping signature for class mapping is the object term in the mapping head
    * (i.e., the class predicate name). The object term must be a URI reference object.
    */
   private static URI getClassMappingSignature(IClassMapping mapping)
   {
      return UriReference.getUri(TripleAtom.getObject(mapping.getTargetAtom()));
   }

   /*
    * The mapping signature for class mapping is the predicate term in the mapping head
    * (i.e., the property predicate name). The predicate term must be a URI reference object.
    */
   private static URI getPropertyMappingSignature(IPropertyMapping mapping)
   {
      return UriReference.getUri(TripleAtom.getPredicate(mapping.getTargetAtom()));
   }
}
