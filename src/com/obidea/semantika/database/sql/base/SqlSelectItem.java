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
package com.obidea.semantika.database.sql.base;

import com.obidea.semantika.exception.SemantikaRuntimeException;
import com.obidea.semantika.util.StringUtils;

public class SqlSelectItem implements ISqlObject
{
   private static final long serialVersionUID = 629451L;

   private ISqlExpression mExpression;
   private String mAliasName = ""; //$NON-NLS-1$

   public SqlSelectItem(ISqlExpression expression)
   {
      mExpression = expression;
   }

   public ISqlExpression getExpression()
   {
      return mExpression;
   }

   public void setAliasName(String alias)
   {
      if (!StringUtils.isEmpty(alias)) {
         mAliasName = alias;
      }
   }

   public void removeAliasName()
   {
      mAliasName = ""; //$NON-NLS-1$
   }

   public String getAliasName()
   {
      return mAliasName;
   }

   public boolean hasAliasName()
   {
      return StringUtils.isEmpty(mAliasName) ? false : true;
   }

   public String getLabelName()
   {
      String label = getAliasName();
      if (StringUtils.isEmpty(label)) {
         try {
            label = ((ISqlColumn) getExpression()).getColumnName();
         }
         catch (ClassCastException e) {
            throw new SemantikaRuntimeException("Unable to retrieve select item's label", e); //$NON-NLS-1$
         }
      }
      return label;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + getExpression().hashCode();
      result = prime * result + getAliasName().hashCode();
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
      final SqlSelectItem other = (SqlSelectItem) obj;
      return getExpression().equals(other.getExpression())
            && getAliasName().equals(other.getAliasName());
   }
}

