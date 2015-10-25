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
package com.obidea.semantika.mapping.base.sql;

import com.obidea.semantika.expression.base.AbstractConstant;
import com.obidea.semantika.expression.base.ITermVisitor;
import com.obidea.semantika.mapping.base.IMappingTerm;
import com.obidea.semantika.mapping.base.TermType;

public abstract class ConstantTerm extends AbstractConstant implements IMappingTerm
{
   private static final long serialVersionUID = 629451L;

   private int mTermType = TermType.LITERAL_TYPE; // by default

   public ConstantTerm(String value, String datatype)
   {
      super(value, datatype);
   }

   @Override
   public void setTermType(int type)
   {
      mTermType = type;
   }

   @Override
   public int getTermType()
   {
      return mTermType;
   }

   @Override
   public void accept(ITermVisitor visitor)
   {
      visitor.visit(this);
   }
}
