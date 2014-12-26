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

import com.obidea.semantika.database.IDatabase;
import com.obidea.semantika.database.IDatabaseMetadata;
import com.obidea.semantika.mapping.IMetaModel;
import com.obidea.semantika.ontology.IOntology;

public abstract class MetaModel implements IMetaModel
{
   protected static MetaModel getInstance(Settings settings)
   {
      IDatabase database = settings.getDatabase();
      if (database == null) {
         throw new IllegalArgumentException("Missing database"); //$NON-NLS-1$
      }
      return new Delegate(database, settings);
   }

   private static class Delegate extends MetaModel
   {
      private IDatabaseMetadata mDatabaseMetadata;
      private IOntology mOntology;

      private Delegate(IDatabase database, Settings settings)
      {
         IDatabaseMetadata metadata = database.getMetadata();
         if (metadata == null) {
            throw new IllegalArgumentException("Missing database metadata"); //$NON-NLS-1$
         }
         mDatabaseMetadata = metadata;
         mOntology = settings.getOntology();
      }

      @Override
      public IDatabaseMetadata getDatabaseMetadata()
      {
         return mDatabaseMetadata;
      }

      @Override
      public IOntology getOntology()
      {
         return mOntology;
      }
   }
}
