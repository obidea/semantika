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

import java.util.List;

import org.slf4j.Logger;

import com.obidea.semantika.mapping.base.IMapping;
import com.obidea.semantika.util.LogUtils;

public abstract class ImmutableMappingSet implements IMappingSet
{
   private static final long serialVersionUID = 629451L;

   private static final Logger LOG = LogUtils.createLogger("semantika.mapping"); //$NON-NLS-1$

   @Override
   public void copy(IMappingSet otherSet)
   {
      LOG.warn("Unable to copy mappings to an immutable set"); //$NON-NLS-1$
   }

   @Override
   public void add(IMapping mapping)
   {
      LOG.warn("Unable to add a mapping to an immutable set"); //$NON-NLS-1$
   }

   @Override
   public void addAll(List<IMapping> mapping)
   {
      LOG.warn("Unable to add mappings to an immutable set"); //$NON-NLS-1$
   }

   @Override
   public void remove(IMapping mapping)
   {
      LOG.warn("Unable to remove a mapping to an immutable set"); //$NON-NLS-1$
   }

   @Override
   public void removeAll(List<IMapping> mapping)
   {
      LOG.warn("Unable to remove mappings to an immutable set"); //$NON-NLS-1$
   }
}
