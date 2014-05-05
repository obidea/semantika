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
package com.obidea.semantika.mapping.sql;

import com.obidea.semantika.database.NamingUtils;
import com.obidea.semantika.database.base.IColumn;
import com.obidea.semantika.database.datatype.SqlTypeToXmlType;
import com.obidea.semantika.expression.base.AbstractVariable;
import com.obidea.semantika.expression.base.ITermVisitor;

public abstract class VariableMediator extends AbstractVariable
{
   private static final long serialVersionUID = 629451L;

   public VariableMediator(IColumn column)
   {
      super(getColumnVariableName(column), getColumnVariableType(column));
   }

   protected void notifyColumnNameChanged(String newName)
   {
      super.setName(newName); // update the variable name.
   }

   protected void notifyColumnTypeChanged(String newDatatype)
   {
      super.setDatatype(newDatatype); // update the variable datatype.
   }

   private static String getColumnVariableName(IColumn c)
   {
      return createName(c.getSchemaName(), c.getTableName(), c.getLocalName());
   }

   private static String getColumnVariableType(IColumn c)
   {
      return SqlTypeToXmlType.get(c.getSqlType());
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
