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

import com.obidea.semantika.mapping.base.IClassMapping;
import com.obidea.semantika.mapping.base.IMapping;
import com.obidea.semantika.mapping.base.IPropertyMapping;

public class MappingType<E extends IMapping> implements Serializable
{
   private static final long serialVersionUID = 629451L;

   private String mName;

   private MappingType(String name)
   {
      mName = name;
   }

   public String getName()
   {
      return mName;
   }

   private static <E extends IMapping> MappingType<E> getInstance(String name)
   {
      return new MappingType<E>(name);
   }

   public static final MappingType<IClassMapping> CLASS_MAPPING = getInstance("ClassMapping");

   public static final MappingType<IPropertyMapping> PROPERTY_MAPPING = getInstance("PropertyMapping");
}
