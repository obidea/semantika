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

public class Variable extends AbstractVariable
{
   private static final long serialVersionUID = 629451L;

   /**
    * Creates a new variable with name and datatype. The specified datatype must
    * be written using its full URI based on XSD namespace. {@link http
    * ://www.w3.org/TR/2012/PR-xmlschema11-2-20120119/#built-in-datatypes}
    * 
    * @param name
    *           The variable name
    * @param datatype
    *           The specified datatype.
    */
   public Variable(String name, String datatype)
   {
      super(name, datatype);
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + getName().hashCode();
      result = prime * result + ((!isTyped()) ? 0 : getDatatype().hashCode());
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
      if (getClass() != obj.getClass()) {
         return false;
      }
      final Variable other = (Variable) obj;

      return getName().equals(other.getName()) && getDatatype().equals(other.getDatatype());
   }

   @Override
   public void accept(ITermVisitor visitor)
   {
      visitor.visit(this);
   }

   /*
    * Internal use only for debugging.
    */
   @Override
   public String toString()
   {
      final StringBuilder sb = new StringBuilder(getName());
      if (isTyped()) {
         String type = getDatatype().substring(getDatatype().indexOf("#") + 1); //$NON-NLS-1$ 
         sb.append(":").append(StringUtils.toUpperCase(type)); //$NON-NLS-1$
      }
      return sb.toString();
   }
}
