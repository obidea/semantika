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
package com.obidea.semantika.database.internal;

import java.util.List;

import com.obidea.semantika.database.NamingUtils;
import com.obidea.semantika.database.base.Column;
import com.obidea.semantika.database.base.ForeignKey;
import com.obidea.semantika.database.base.IColumn;
import com.obidea.semantika.database.base.IForeignKey;
import com.obidea.semantika.database.base.IPrimaryKey;
import com.obidea.semantika.database.base.ISchema;
import com.obidea.semantika.database.base.ITable;
import com.obidea.semantika.database.base.PrimaryKey;
import com.obidea.semantika.exception.IllegalOperationException;

/* package */class TemporaryTable implements ITable
{
   private static final long serialVersionUID = 629451L;

   private String mSchemaName;
   private String mTableName;

   public TemporaryTable(String schemaName, String tableName)
   {
      mSchemaName = schemaName;
      mTableName = tableName;
   }

   @Override
   public ISchema getSchemaOrigin()
   {
      return null;
   }

   @Override
   public String getNamespace()
   {
      return mSchemaName;
   }

   @Override
   public String getSchemaName()
   {
      return mSchemaName;
   }

   @Override
   public String getLocalName()
   {
      return mTableName;
   }

   @Override
   public String getFullName()
   {
      return NamingUtils.constructDatabaseObjectIdentifier(getNamespace(), getLocalName());
   }

   @Override
   public void addColumn(IColumn column)
   {
      // NO-OP
   }
   
   @Override
   public List<Column> getColumns()
   {
      throw new IllegalOperationException("Cannot get columns from temporary table"); //$NON-NLS-1$
   }

   @Override
   public Column getColumn(String name)
   {
      throw new IllegalOperationException("Cannot get a column from temporary table"); //$NON-NLS-1$
   }

   @Override
   public void setPrimaryKey(IPrimaryKey primaryKey)
   {
      // NO-OP
   }

   @Override
   public PrimaryKey getPrimaryKey()
   {
      throw new IllegalOperationException("Cannot get primary key from temporary table"); //$NON-NLS-1$
   }

   @Override
   public void setForeignKey(IForeignKey foreignKey)
   {
      // NO-OP
   }

   @Override
   public List<ForeignKey> getForeignKeys()
   {
      throw new IllegalOperationException("Cannot get foreign keys from temporary table"); //$NON-NLS-1$
   }

   @Override
   public ForeignKey getForeignKey(String name)
   {
      throw new IllegalOperationException("Cannot get foreign key from temporary table"); //$NON-NLS-1$
   }
}
