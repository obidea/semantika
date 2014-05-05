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

public abstract class AbstractVariable extends Term implements IVariable
{
   private static final long serialVersionUID = 629451L;

   public AbstractVariable(String name, String datatype)
   {
      super(name, datatype);
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + getName().hashCode();
      result = prime * result + getDatatype().hashCode();
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (obj instanceof AbstractVariable) {
         final AbstractVariable other = (AbstractVariable) obj;
         return getName().equals(other.getName()) && getDatatype().equals(other.getDatatype());
      }
      return false;
   }
}
