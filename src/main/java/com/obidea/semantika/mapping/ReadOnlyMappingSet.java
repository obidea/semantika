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
package com.obidea.semantika.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.obidea.semantika.expression.base.Iri;
import com.obidea.semantika.mapping.base.IClassMapping;
import com.obidea.semantika.mapping.base.IMapping;
import com.obidea.semantika.mapping.base.IPropertyMapping;
import com.obidea.semantika.util.Serializer;

/**
 * This class implements an immutable mapping set. An instance of
 * <code>ImmutableMappingSet</code> contains its own private data and will never
 * change. The object is convenient for public sharing resources without
 * worrying its content integrity.
 */
public final class ReadOnlyMappingSet extends ImmutableMappingSet
{
   private static final long serialVersionUID = 629451L;

   private InternalMapping mInternal = new InternalMapping();

   public ReadOnlyMappingSet(MutableMappingSet mutableProgram)
   {
      MappingSet copySet = (MappingSet) Serializer.copy(mutableProgram);
      for (IMapping mapping : copySet.getAll()) {
         mInternal.addMapping(mapping);
      }
   }

   @Override
   public boolean contains(Iri signature)
   {
      return mInternal.contains(signature);
   }

   @Override
   public Set<IMapping> get(Iri signature)
   {
      return mInternal.getValues(mInternal.getMappingBySignature(), signature);
   }

   @Override
   public Set<Iri> getMappingSignatures()
   {
      return mInternal.getMappingSignatures();
   }

   @Override
   public Set<IClassMapping> getClassMappings()
   {
      Set<IMapping> mappings = mInternal.getValues(mInternal.getMappingByType(), MappingType.CLASS_MAPPING);
      Set<IClassMapping> toReturn = new HashSet<IClassMapping>();
      for (IMapping mapping : mappings) {
         toReturn.add((IClassMapping) mapping);
      }
      return Collections.unmodifiableSet(toReturn);
   }

   @Override
   public Set<IPropertyMapping> getPropertyMappings()
   {
      Set<IMapping> mappings =  mInternal.getValues(mInternal.getMappingByType(), MappingType.PROPERTY_MAPPING);
      Set<IPropertyMapping> toReturn = new HashSet<IPropertyMapping>();
      for (IMapping mapping : mappings) {
         toReturn.add((IPropertyMapping) mapping);
      }
      return Collections.unmodifiableSet(toReturn);
   }

   @Override
   public Set<IMapping> getAll()
   {
      return mInternal.getMappings();
   }

   @Override
   public int size()
   {
      return mInternal.getMappingCount();
   }

   /*
    * Internal use only for debugging.
    */
   @Override
   public String toString()
   {
      final StringBuilder sb = new StringBuilder();
      sb.append("Size: ").append(size()); //$NON-NLS-1$
      sb.append("\n"); //$NON-NLS-1$
      for (IMapping mapping : getSortedMappings()) {
         sb.append(mapping).append("\n"); //$NON-NLS-1$
      }
      return sb.toString();
   }

   private List<IMapping> getSortedMappings()
   {
      List<IMapping> mappingList = new ArrayList<IMapping>(getAll());
      Collections.sort(mappingList, new Comparator<IMapping>() {
         @Override
         public int compare(IMapping o1, IMapping o2)
         {
            String signature1 = o1.getHeadSymbol().getName();
            String signature2 = o2.getHeadSymbol().getName();
            return signature1.compareTo(signature2);
         }
      });
      return mappingList;
   }
}
