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
package com.obidea.semantika.database.base;

import java.util.Collections;
import java.util.List;

public class Table extends DatabaseObject implements ITable
{
   private static final long serialVersionUID = 629451L;

   private DatabaseObjectList<IColumn> mColumns = new DatabaseObjectList<IColumn>();

   private ISchema mSchemaObject;

   private IPrimaryKey mPrimaryKey;

   private DatabaseObjectList<IForeignKey> mForeignKeys= new DatabaseObjectList<IForeignKey>();

   public Table(final ISchema schema, final String name)
   {
      super(name);
      mSchemaObject = schema;
   }

   public Table(final ITable copyTable)
   {
      super(copyTable.getLocalName());
      mSchemaObject = new Schema(copyTable.getSchemaName());
   }

   @Override
   public ISchema getSchemaOrigin()
   {
      return mSchemaObject;
   }

   /**
    * Returns the associated schema of this table object.
    */
   @Override
   public IDatabaseObject getParentObject()
   {
      return getSchemaOrigin();
   }

   @Override
   public String getSchemaName()
   {
      return getNamespace();
   }

   @Override
   public void addColumn(IColumn column)
   {
      mColumns.add(column);
   }

   @Override
   public List<IColumn> getColumns()
   {
      return Collections.unmodifiableList(mColumns.values());
   }

   @Override
   public IColumn getColumn(String columnName)
   {
      final String columnNamespace = getFullName(); // column namespace == table full-name
      return mColumns.get(columnNamespace, columnName);
   }

   @Override
   public void setPrimaryKey(IPrimaryKey primaryKey)
   {
      mPrimaryKey = primaryKey;
   }

   @Override
   public IPrimaryKey getPrimaryKey()
   {
      return mPrimaryKey;
   }

   @Override
   public void setForeignKey(IForeignKey foreignKey)
   {
      mForeignKeys.add(foreignKey);
   }

   @Override
   public List<IForeignKey> getForeignKeys()
   {
      return Collections.unmodifiableList(mForeignKeys.values());
   }

   @Override
   public IForeignKey getForeignKey(String name)
   {
      return mForeignKeys.get(null, name);
   }
}
