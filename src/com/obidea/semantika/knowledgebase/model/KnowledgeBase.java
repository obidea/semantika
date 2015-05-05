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
package com.obidea.semantika.knowledgebase.model;

import com.obidea.semantika.database.IDatabase;
import com.obidea.semantika.mapping.IMappingSet;
import com.obidea.semantika.mapping.ImmutableMappingSet;
import com.obidea.semantika.mapping.MutableMappingSet;
import com.obidea.semantika.ontology.IOntology;

public class KnowledgeBase implements IKnowledgeBase
{
   private IOntology mOntology;

   private IDatabase mDatabase;

   private IMappingSet mMappingSet;

   public KnowledgeBase(IOntology ontology, IDatabase database, IMappingSet mappingSet)
   {
      mOntology = ontology;
      mDatabase = database;
      mMappingSet = mappingSet;
   }

   public KnowledgeBase(IKnowledgeBase otherKb)
   {
      mOntology = otherKb.getOntology();
      mDatabase = otherKb.getDatabase();
      mMappingSet = otherKb.getMappingSet();
   }

   @Override
   public IOntology getOntology()
   {
      return mOntology;
   }

   @Override
   public IDatabase getDatabase()
   {
      return mDatabase;
   }

   @Override
   public ImmutableMappingSet getMappingSet()
   {
      if (mMappingSet instanceof ImmutableMappingSet) {
         return (ImmutableMappingSet) mMappingSet;
      }
      else if (mMappingSet instanceof MutableMappingSet) {
         return ((MutableMappingSet) mMappingSet).getImmutableSet();
      }
      return null;
   }
}
