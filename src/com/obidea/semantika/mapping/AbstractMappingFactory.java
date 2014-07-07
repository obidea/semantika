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

public abstract class AbstractMappingFactory implements IMappingFactory
{
   private IMetaModel mMetaModel;

   @Override
   public void setMetaModel(IMetaModel metaModel)
   {
      if (metaModel == null) {
         throw new IllegalArgumentException("Meta-model cannot be null"); //$NON-NLS-1$
      }
      mMetaModel = metaModel;
   }

   protected IMetaModel getMetaModel()
   {
      return mMetaModel;
   }

   public MutableMappingSet createEmptyMappingSet()
   {
      return new MappingSet();
   }
}
