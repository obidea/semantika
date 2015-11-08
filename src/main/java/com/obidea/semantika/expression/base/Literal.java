/*
 * Copyright (c) 2013-2015 Obidea Technology
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

import com.obidea.semantika.exception.SemantikaRuntimeException;
import com.obidea.semantika.util.StringUtils;

public class Literal extends AbstractConstant implements ILiteral
{
   private static final long serialVersionUID = 629451L;

   private String mLang;

   public Literal(String value, String lang, String datatype)
   {
      super(value, datatype);
      mLang = lang;
   }

   @Override
   public Object getValue()
   {
      try {
         return LiteralUtils.getValue(this);
      }
      catch (Exception e) {
         throw new SemantikaRuntimeException("Unable to get the value object", e); //$NON-NLS-1$
      }
   }

   @Override
   public String getLanguageTag()
   {
      return mLang;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + getValue().hashCode();
      result = prime * result + getLanguageTag().hashCode();
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
      if (getClass() != obj.getClass()) {
         return false;
      }
      final Literal other = (Literal) obj;
      
      return getValue().equals(other.getValue()) && getLanguageTag().equals(other.getLanguageTag()) 
            && getDatatype().equals(other.getDatatype());
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
      final StringBuilder sb = new StringBuilder();
      sb.append("\"").append(getValue()).append("\""); //$NON-NLS-1$ //$NON-NLS-2$
      if (!StringUtils.isEmpty(getLanguageTag())) {
         sb.append(":").append(getLanguageTag()); //$NON-NLS-1$
      }
      if (isTyped()) {
         String type = getDatatype().substring(getDatatype().indexOf("#") + 1); // $NON-NLS-1$
         sb.append(":").append(StringUtils.toUpperCase(type)); //$NON-NLS-1$
      }
      return sb.toString();
   }
}
