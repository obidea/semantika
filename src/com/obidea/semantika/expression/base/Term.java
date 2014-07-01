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
package com.obidea.semantika.expression.base;

import com.obidea.semantika.util.StringUtils;

public abstract class Term implements ITerm
{
   private static final long serialVersionUID = 629451L;

   private String mName;
   private String mDatatype;
   private String mAlias;

   public Term(String name, String datatype)
   {
      setName(name);
      setDatatype(datatype);
   }

   protected void setName(String name)
   {
      mName = name;
   }

   @Override
   public String getName()
   {
      return mName;
   }

   protected void setDatatype(String datatype)
   {
      mDatatype = datatype;
   }

   @Override
   public String getDatatype()
   {
      return mDatatype;
   }

   @Override
   public boolean isTyped()
   {
      return StringUtils.isEmpty(mDatatype) ? false : true;
   }

   public void setAlias(String alias)
   {
      mAlias = alias;
   }

   @Override
   public String getAlias()
   {
      return mAlias;
   }

   public boolean hasAlias()
   {
      return StringUtils.isEmpty(mAlias) ? false : true;
   }
}
