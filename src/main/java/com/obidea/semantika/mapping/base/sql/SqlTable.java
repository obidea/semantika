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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.obidea.semantika.database.base.IColumn;
import com.obidea.semantika.database.base.IContainDatabaseObject;
import com.obidea.semantika.database.base.ITable;
import com.obidea.semantika.database.sql.base.ISqlExpressionVisitor;
import com.obidea.semantika.database.sql.base.ISqlTable;
import com.obidea.semantika.knowledgebase.TermSubstitutionBinding;
import com.obidea.semantika.util.CollectionUtils;
import com.obidea.semantika.util.StringUtils;

public class SqlTable extends LogicalTable implements ISqlTable, IContainDatabaseObject<ITable>
{
   private static final long serialVersionUID = 629451L;

   private ITable mTable;
   private String mSchemaName;
   private String mTableName;
   private String mAliasName;

   private String[] mNameFragments;

   private List<SqlColumn> mTableColumns = new ArrayList<SqlColumn>();

   /**
    * Constructs a SQL table atom with input <code>table</code> as its meta-information.
    * 
    * @param table
    *           An instance of database object that stores information about a database table.
    */
   public SqlTable(ITable table)
   {
      this(table, ""); //$NON-NLS-1$
   }

   /**
    * Constructs a SQL table atom with input <code>table</code> as its meta-information and an alias
    * name.
    * 
    * @param table
    *           An instance of database object that stores information about a database table.
    * @param alias
    *           An alias name.
    */
   public SqlTable(ITable table, String alias)
   {
      super(table);
      mTable = table;
      mSchemaName = table.getSchemaName();
      mTableName = table.getLocalName();
      mAliasName = alias;
      for (IColumn c : table.getColumns()) {
         mTableColumns.add(new SqlColumn(c));
      }
   }

   @Override
   public ITable asDatabaseObject()
   {
      return mTable;
   }

   @Override
   public String getTableName()
   {
      return mTableName;
   }

   @Override
   public String[] getNameFragments()
   {
      if (mNameFragments == null) {
         List<String> fragments = new ArrayList<String>();
         if (!StringUtils.isEmpty(mSchemaName)) {
            fragments.add(mSchemaName);
         }
         fragments.add(getTableName());
         mNameFragments = CollectionUtils.toArray(fragments, String.class);
      }
      return mNameFragments;
   }

   @Override
   public void setAliasName(String alias)
   {
      if (!StringUtils.isEmpty(alias)) {
         mAliasName = alias;
      }
   }

   @Override
   public String getAliasName()
   {
      return mAliasName;
   }

   @Override
   public boolean hasAliasName()
   {
      return StringUtils.isEmpty(mAliasName) ? false : true;
   }

   @Override
   public List<SqlColumn> getColumns()
   {
      return Collections.unmodifiableList(mTableColumns);
   }

   public SqlColumn getColumn(int index)
   {
      return mTableColumns.get(index);
   }

   public int getColumnSize()
   {
      return mTableColumns.size();
   }

   @Override
   public void apply(TermSubstitutionBinding binding)
   {
      // NO-OP
   }

   @Override
   public void accept(ISqlExpressionVisitor visitor)
   {
      visitor.visit(this);
   }

   /**
    * Use only for debugging purposes.
    */
   @Override
   public String toString()
   {
      if (hasAliasName()) {
         return String.format("%s[%s]", getTableName(), getAliasName()); //$NON-NLS-1$
      }
      else {
         return String.format("%s", getTableName()); //$NON-NLS-1$
      }
   }
}
