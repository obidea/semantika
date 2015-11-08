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
package com.obidea.semantika.mapping.base.sql;

import com.obidea.semantika.database.NamingUtils;
import com.obidea.semantika.database.base.IColumn;
import com.obidea.semantika.database.datatype.SqlTypeToXmlType;
import com.obidea.semantika.expression.base.AbstractVariable;
import com.obidea.semantika.expression.base.ITermVisitor;
import com.obidea.semantika.mapping.base.IMappingTerm;
import com.obidea.semantika.mapping.base.TermType;

public abstract class ColumnTerm extends AbstractVariable implements IMappingTerm
{
   private static final long serialVersionUID = 629451L;

   private int mTermType = TermType.LITERAL_TYPE; // by default

   public ColumnTerm(IColumn column)
   {
      super(getColumnVariableName(column), getColumnVariableType(column));
   }

   private static String getColumnVariableName(IColumn c)
   {
      return createName(c.getSchemaName(), c.getTableName(), c.getLocalName());
   }

   private static String getColumnVariableType(IColumn c)
   {
      return SqlTypeToXmlType.get(c.getSqlType());
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

   /**
    * Set this mapping variable term to a given <code>datatype</code> input to 
    * replace the existing one. This method should implement a type checking
    * before overriding the old type.
    *
    * @param datatype
    *           The new datatype to override.
    */
   public abstract void overrideDatatype(String datatype);

   protected void notifyVariableNameChanged(String newName)
   {
      super.setName(newName); // update the variable name.
   }

   protected void notifyVariableTypeChanged(String newDatatype)
   {
      super.setDatatype(newDatatype); // update the variable datatype.
   }

   protected static String createName(String... nameFragments)
   {
      return NamingUtils.constructExpressionObjectLabel(nameFragments);
   }

   @Override
   public void accept(ITermVisitor visitor)
   {
      visitor.visit(this);
   }
}
