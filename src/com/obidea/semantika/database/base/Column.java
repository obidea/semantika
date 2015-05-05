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

public class Column extends DatabaseObject implements IColumn
{
   private static final long serialVersionUID = 629451L;

   private ITable mTableObject;

   private String mSchemaName;
   private String mTableName;
   private boolean mIsPrimaryKey;

   private int mSqlType = 0; // 0 = null

   public Column(final ITable table, final String name, final int datatype)
   {
      this(table, name, datatype, false);
   }

   public Column(final ITable table, final String name, final int datatype, final boolean isPrimaryKey)
   {
      super(name);
      mTableObject = table;
      mSchemaName = table.getSchemaName();
      mTableName = table.getLocalName();
      mSqlType = datatype;
      mIsPrimaryKey = isPrimaryKey;
   }

   @Override
   public ITable getTableOrigin()
   {
      return mTableObject;
   }

   /**
    * Returns the associated table of this column object.
    */
   @Override
   protected IDatabaseObject getParentObject()
   {
      return getTableOrigin();
   }

   @Override
   public String getSchemaName()
   {
      return mSchemaName;
   }

   @Override
   public String getTableName()
   {
      return mTableName;
   }

   @Override
   public void setPrimaryKey(boolean isPrimaryKey)
   {
      mIsPrimaryKey = isPrimaryKey;
   }

   @Override
   public boolean isPrimaryKey()
   {
      return mIsPrimaryKey;
   }

   @Override
   public int getSqlType()
   {
      return mSqlType;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + getFullName().hashCode();
      result = prime * result + getSqlType();
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
      final Column other = (Column) obj;
      return getFullName().equals(other.getFullName()) && getSqlType() == other.getSqlType();
   }
}
