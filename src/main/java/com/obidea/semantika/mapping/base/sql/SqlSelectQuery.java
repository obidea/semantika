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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.obidea.semantika.database.sql.base.ISqlExpression;
import com.obidea.semantika.database.sql.base.SqlSelectItem;
import com.obidea.semantika.knowledgebase.TermSubstitutionBinding;

public class SqlSelectQuery extends SqlQuery
{
   private static final long serialVersionUID = 629451L;

   private boolean mDistinct = false;

   private List<SqlSelectItem> mSelectItems = new ArrayList<SqlSelectItem>();
   private ISqlExpression mFromExpression;
   private Set<ISqlExpression> mWhereExpressions = new HashSet<ISqlExpression>();

   private transient SqlInternal mInternal = new SqlInternal(this);

   public SqlSelectQuery()
   {
      this(false);
   }

   public SqlSelectQuery(boolean isDistinct)
   {
      super();
      setDistinct(isDistinct);
   }

   @Override
   public void setDistinct(boolean isDistinct)
   {
      mDistinct = isDistinct;
   }

   @Override
   public boolean isDistinct()
   {
      return mDistinct;
   }

   @Override
   public void addSelectItem(SqlSelectItem selectItem)
   {
      if (selectItem != null) {
         mSelectItems.add(selectItem);
         notifySelectItemChanged(selectItem.getExpression());
      }
   }

   @Override
   public List<SqlSelectItem> getSelectItems()
   {
      return mSelectItems;
   }

   @Override
   public void setFromExpression(ISqlExpression expression)
   {
      if (expression != null) {
         mFromExpression = expression;
         notifyFromExpressionChanged(expression);
      }
   }

   @Override
   public ISqlExpression getFromExpression()
   {
      return mFromExpression;
   }

   @Override
   public void addWhereExpression(ISqlExpression expression)
   {
      if (expression != null) {
         mWhereExpressions.add(expression);
         notifyWhereExpressionChanged(expression);
      }
   }

   @Override
   public Set<ISqlExpression> getWhereExpression()
   {
      return Collections.unmodifiableSet(mWhereExpressions);
   }

   @Override
   public boolean hasWhereExpression()
   {
      return mWhereExpressions.isEmpty() ? false : true;
   }

   @Override
   public List<SqlColumn> getAllColumns()
   {
      lazyInit(); // lazy init to reduce footprints
      return mInternal.getColumns();
   }

   @Override
   public List<SqlTable> getAllTables()
   {
      lazyInit();
      return mInternal.getTables();
   }

   @Override
   public ISqlExpression findSelectItemExpression(String selectItemLabel)
   {
      lazyInit();
      return mInternal.findSelectItemExpression(selectItemLabel);
   }

   @Override
   public void changeAllColumnNamespace(String oldNamespace, String newNamespace)
   {
      lazyInit();
      mInternal.changeColumnNamespace("*", oldNamespace, newNamespace); //$NON-NLS-1$ // * = wildcard for all columns
   }

   @Override
   public void changeColumnNamespace(String targetColumn, String oldNamespace, String newNamespace)
   {
      lazyInit();
      mInternal.changeColumnNamespace(targetColumn, oldNamespace, newNamespace);
   }

   @Override
   public void resetFilters()
   {
      mWhereExpressions.clear();
      if (mInternal != null) {
         mInternal.clearAllColumns();
      }
      notifyQueryConstraintAllRemoved();
   }

   @Override
   public void apply(TermSubstitutionBinding binding)
   {
      // TODO Auto-generated method stub
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + getSelectItems().hashCode();
      result = prime * result + getFromExpression().hashCode();
      result = prime * result + getWhereExpression().hashCode();
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
      final SqlSelectQuery other = (SqlSelectQuery) obj;
      return getSelectItems().equals(other.getSelectItems())
            && getFromExpression().equals(other.getFromExpression())
            && getWhereExpression().equals(other.getWhereExpression());
   }

   private void lazyInit()
   {
      if (mInternal == null) {
         mInternal = new SqlInternal(this);
      }
   }
}
