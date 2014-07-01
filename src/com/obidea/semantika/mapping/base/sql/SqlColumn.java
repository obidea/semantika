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

import java.util.ArrayList;
import java.util.List;

import com.obidea.semantika.database.base.IColumn;
import com.obidea.semantika.database.base.IContainDatabaseObject;
import com.obidea.semantika.database.sql.base.ISqlColumn;
import com.obidea.semantika.database.sql.base.ISqlExpressionVisitor;
import com.obidea.semantika.datatype.XmlTypeToSqlType;
import com.obidea.semantika.datatype.exception.UnsupportedDataTypeException;
import com.obidea.semantika.util.CollectionUtils;
import com.obidea.semantika.util.StringUtils;

public class SqlColumn extends VariableMediator implements ISqlColumn, IContainDatabaseObject<IColumn>
{
   private static final long serialVersionUID = 629451L;

   private IColumn mColumn;

   private String mSchemaName;
   private String mTableName;
   private String mColumnName;
   private String mViewName = ""; //$NON-NLS-1$
   private int mColumnType;

   private String[] mNameFragments;

   private boolean bTypeOverriden = false;

   /**
    * Constructs a SQL column variable with input <code>column</code> as its
    * meta-information.
    *
    * @param column
    *           An instance of database object that stores information about a
    *           table column.
    */
   public SqlColumn(IColumn column)
   {
      super(column);
      mColumn = column;
      mSchemaName = column.getSchemaName();
      mTableName = column.getTableName();
      mColumnName = column.getLocalName();
      mColumnType = column.getSqlType();
   }

   @Override
   public IColumn asDatabaseObject()
   {
      return mColumn;
   }

   @Override
   public String getTableOrigin()
   {
      return mTableName;
   }

   @Override
   public void setViewName(String viewName)
   {
      if (!StringUtils.isEmpty(viewName)) {
         mViewName = viewName;
         mNameFragments = null; // notify to update name fragments
         notifyVariableNameChanged(createName(getNameFragments()));
      }
   }

   @Override
   public String getViewName()
   {
      return mViewName;
   }

   public boolean hasViewName()
   {
      return StringUtils.isEmpty(mViewName) ? false : true;
   }

   @Override
   public String getColumnName()
   {
      return mColumnName;
   }

   @Override
   public int getColumnType()
   {
      return mColumnType;
   }

   @Override
   public String[] getNameFragments()
   {
      if (mNameFragments == null) {
         List<String> fragments = new ArrayList<String>();
         if (!StringUtils.isEmpty(mViewName)) {
            fragments.add(mViewName);
            fragments.add(getColumnName());
         }
         else {
            if (!StringUtils.isEmpty(mSchemaName)) {
               fragments.add(mSchemaName);
            }
            if (!StringUtils.isEmpty(mTableName)) {
               fragments.add(mTableName);
            }
            fragments.add(getColumnName());
         }
         mNameFragments = CollectionUtils.toArray(fragments, String.class);
      }
      return mNameFragments;
   }

   @Override
   public void overrideDatatype(String datatype) throws UnsupportedDataTypeException
   {
      notifyVariableTypeChanged(datatype);
      mColumnType = XmlTypeToSqlType.get(datatype);
      bTypeOverriden = true;
   }

   public boolean isOverriden()
   {
      return bTypeOverriden;
   }

   @Override
   public boolean isTyped()
   {
      return true; // SQL column always typed
   }

   /**
    * Returns <code>true</code> if the other SQL column belongs to the same table
    * schema in the database.
    */
   public boolean isEquivalent(SqlColumn otherColumn)
   {
      return this.asDatabaseObject().equals(otherColumn.asDatabaseObject());
   }

   @Override
   public void accept(ISqlExpressionVisitor visitor)
   {
      visitor.visit(this);
   }

   /*
    * Internal use only for debugging.
    */
   @Override
   public String toString()
   {
      if (hasViewName()) {
         return getViewName() + "." + getColumnName(); //$NON-NLS-1$
      }
      else {
         return getColumnName();
      }
   }
}
