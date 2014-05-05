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
package com.obidea.semantika.database;

import com.obidea.semantika.database.base.Column;
import com.obidea.semantika.database.base.DatabaseObject;
import com.obidea.semantika.database.base.ForeignKey;
import com.obidea.semantika.database.base.PrimaryKey;
import com.obidea.semantika.database.base.Schema;
import com.obidea.semantika.database.base.Table;
import com.obidea.semantika.util.StringUtils;

public class DatabaseObjectUtils
{
   /**
    * Creates a new Schema object. If the name equals to <code>null</code> or
    * empty string, the method gives a default name as "default".
    * 
    * @param name
    *           the schema name.
    * @return Returns a new Schema object.
    */
   public static Schema makeSchema(String name)
   {
      if (StringUtils.isEmpty(name)) {
         return new Schema("default"); // $NON-NLS-1$
      }
      return new Schema(name);
   }

   /**
    * Creates a new Table object. If the schema name equals to <code>null</code>
    * or empty string, the method gives a default name as "default".
    * 
    * @param schemaName
    *           the schema name, can be <code>null</code> or an empty string.
    * @param name
    *           the table name (mandatory input).
    * @return Returns a new Table object.
    */
   public static Table makeTable(String schemaName, String name)
   {
      return new Table(makeSchema(schemaName), name);
   }

   /**
    * Creates a new Column object. If the schema name equals to
    * <code>null</code> or empty string, the method gives a default name as
    * "default".
    * 
    * @param schemaName
    *           the schema name, can be <code>null</code> or an empty string.
    * @param tableName
    *           the table name associated to the column (mandatory input).
    * @param name
    *           the column name (mandatory input).
    * @param datatype
    *           the column datatype, using JDBC Type constants (mandatory
    *           input).
    * @return Returns a new Column object.
    */
   public static Column makeColumn(String schemaName, String tableName, String name, int datatype)
   {
      return new Column(makeTable(schemaName, tableName), name, datatype);
   }

   /**
    * Creates a new Primary Key object. If the schema name equals to
    * <code>null</code> or empty string, the method gives a default name as
    * "default".
    * 
    * @param schemaName
    *           the schema name, can be <code>null</code> or an empty string.
    * @param tableName
    *           the table name associated to the PK (mandatory input).
    * @param name
    *           the primary key name (mandatory input).
    * @return Returns a new Primary Key object.
    */
   public static PrimaryKey makePrimaryKey(String schemaName, String tableName, String name)
   {
      return new PrimaryKey(makeTable(schemaName, tableName), name);
   }

   /**
    * Creates a new Foreign Key object. If the schema name equals to
    * <code>null</code> or empty string, the method gives a default name as
    * "default".
    * 
    * @param schemaName
    *           the schema name, can be <code>null</code> or an empty string.
    * @param tableName
    *           the table name associated to the FK (mandatory input).
    * @param name
    *           the foreign key name (mandatory input).
    * @return Returns a new Foreign Key object.
    */
   public static ForeignKey makeForeignKey(String schemaName, String tableName, String name)
   {
      return new ForeignKey(makeTable(schemaName, tableName), name);
   }

   public static String toString(DatabaseObject dbo)
   {
      if (dbo == null) {
         return "<null>"; //$NON-NLS-1$
      }
      else {
         StringBuilder sb = new StringBuilder();
         toString(dbo, sb);
         return sb.toString();
      }
   }

   public static void toString(DatabaseObject dbo, StringBuilder sb)
   {
      if (dbo instanceof Schema) {
         Schema schema = (Schema) dbo;
         sb.append(schema.getFullName());
      }
      else if (dbo instanceof Table) {
         Table table = (Table) dbo;
         sb.append(table.getFullName());
      }
      else if (dbo instanceof Column) {
         Column column = (Column) dbo;
         sb.append(column.getFullName());
         sb.append(":"); //$NON-NLS-1$
         sb.append(column.getSqlType());
      }
      else if (dbo instanceof PrimaryKey) {
         PrimaryKey pk = (PrimaryKey) dbo;
         sb.append(pk.getLocalName());
         sb.append(pk.getKeys());
      }
      else if (dbo instanceof ForeignKey) {
         ForeignKey fk = (ForeignKey) dbo;
         sb.append(fk.getLocalName());
         sb.append(fk.getReferences());
      }
   }
}
