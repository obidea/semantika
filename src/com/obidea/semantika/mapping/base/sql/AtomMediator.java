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
package com.obidea.semantika.mapping.base.sql;

import java.util.LinkedList;
import java.util.List;

import com.obidea.semantika.database.NamingUtils;
import com.obidea.semantika.database.base.IColumn;
import com.obidea.semantika.database.base.ITable;
import com.obidea.semantika.database.datatype.SqlTypeToXmlType;
import com.obidea.semantika.expression.base.AbstractAtom;
import com.obidea.semantika.expression.base.IAtomVisitor;
import com.obidea.semantika.expression.base.Predicate;
import com.obidea.semantika.expression.base.Variable;

public abstract class AtomMediator extends AbstractAtom
{
   private static final long serialVersionUID = 629451L;

   public AtomMediator(ITable table)
   {
      super(getPredicateFromTable(table), getVariablesFromTable(table));
   }

   @Override
   public boolean isGround()
   {
      return false; // Table atom cannot be ground
   }

   private static Predicate getPredicateFromTable(ITable table)
   {
      String tableSchema = table.getSchemaName();
      String tableName = table.getLocalName();
      return new Predicate(NamingUtils.constructExpressionObjectLabel(tableSchema, tableName));
   }

   private static List<Variable> getVariablesFromTable(ITable table)
   {
      List<Variable> vars = new LinkedList<Variable>();
      for (IColumn c : table.getColumns()) {
         vars.add(new Variable(getNameFromColumn(c), getTypeFromColumn(c)));
      }
      return vars;
   }

   private static String getNameFromColumn(IColumn c)
   {
      String schema = c.getSchemaName();
      String table = c.getTableName();
      String name = c.getLocalName();
      return NamingUtils.constructExpressionObjectLabel(schema, table, name);
   }

   private static String getTypeFromColumn(IColumn c)
   {
      return SqlTypeToXmlType.get(c.getSqlType());
   }

   @Override
   public void accept(IAtomVisitor visitor)
   {
      visitor.visit(this);
   }
}
