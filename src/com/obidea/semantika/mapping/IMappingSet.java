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

import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.Set;

import com.obidea.semantika.mapping.base.IClassMapping;
import com.obidea.semantika.mapping.base.IMapping;
import com.obidea.semantika.mapping.base.IPropertyMapping;

/**
 * Represent a collection of mappings. 
 */
public interface IMappingSet extends Serializable
{
   /**
    * Gets all mappings from other set and copies them to this mapping set.
    * 
    * @param otherSet
    *           the other set to copy
    */
   void copy(IMappingSet otherSet);

   /**
    * Adds a mapping to this set.
    * 
    * @param mapping
    *           the mapping to add
    */
   void add(IMapping mapping);

   /**
    * Adds a list of mappings to this set
    * 
    * @param mapping
    *           a list of mappings to add
    */
   void addAll(List<IMapping> mapping);

   /**
    * Removes a mapping from this set.
    * 
    * @param mapping
    *           the mapping to remove.
    */
   void remove(IMapping mapping);

   /**
    * Removes all mappings in the list from this set.
    * 
    * @param mapping
    *           a list of mappings to remove
    */
   void removeAll(List<IMapping> mapping);

   /**
    * Checks if the mapping set contains mappings with the given signature. A
    * mapping signature is the class or property URI identifier.
    * 
    * @param signature
    *           the mapping signature.
    * @return Returns <code>true</code> if such signature exists, or
    *         <code>false</code> otherwise.
    */
   boolean contains (URI signature);

   /**
    * Gets mappings given the mapping signature. A mapping signature is the
    * class or property URI identifier.
    * 
    * @param signature
    *           the mapping signature.
    * @return A set of mappings with the given signature, or an empty set if the
    *         signature doesn't exist. The set is read-only.
    */
   Set<IMapping> get(URI signature);

   /**
    * Gets all mapping signatures stored in this mapping set. A mapping
    * signature is the class or property URI identifier.
    * 
    * @return A set of mapping signatures.
    */
   Set<URI> getMappingSignatures();

   /**
    * Gets all mappings that assert concept entity.
    * 
    * @return A set of class mappings, or an empty set if such mapping doesn't
    *         exist.
    */
   Set<IClassMapping> getClassMappings();

   /**
    * Gets all mappings that assert role/attribute entity.
    * 
    * @return A set of property mappings, or an empty set if such mapping
    *         doesn't exist.
    */
   Set<IPropertyMapping> getPropertyMappings();

   /**
    * Gets all mapping assertions in this mapping set object.
    *
    * @return A set of mapping assertions.
    */
   Set<IMapping> getAll();

   /**
    * Returns the total count of mapping assertions in this set.
    */
   int size();
}
