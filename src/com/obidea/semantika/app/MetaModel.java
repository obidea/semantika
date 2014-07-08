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
package com.obidea.semantika.app;

import com.obidea.semantika.database.IDatabaseMetadata;
import com.obidea.semantika.mapping.IMetaModel;
import com.obidea.semantika.ontology.IOntology;

public final class MetaModel implements IMetaModel
{
   private IDatabaseMetadata mDatabaseMetadata;
   private IOntology mOntology;

   public MetaModel(IDatabaseMetadata metadata, IOntology ontology)
   {
      if (metadata == null) {
         throw new IllegalArgumentException("Database metadata cannot be null"); //$NON-NLS-1$
      }
      mDatabaseMetadata = metadata;
      
      // Ontology can be null (optional resource)
      mOntology = ontology;
   }

   public IDatabaseMetadata getDatabaseMetadata()
   {
      return mDatabaseMetadata;
   }

   public IOntology getOntology()
   {
      return mOntology;
   }
}
