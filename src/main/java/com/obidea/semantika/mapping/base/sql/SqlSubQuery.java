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

import com.obidea.semantika.database.sql.base.ISqlExpressionVisitor;
import com.obidea.semantika.database.sql.base.ISqlQuery;
import com.obidea.semantika.database.sql.base.ISqlSubQuery;

public class SqlSubQuery extends SyntacticSugarMediator implements ISqlSubQuery
{
   private static final long serialVersionUID = 629451L;

   private ISqlQuery mSelectQuery;
   private String mViewName = ""; //$NON-NLS-1$

   public SqlSubQuery(ISqlQuery query, String viewName)
   {
      mSelectQuery = query;
      mViewName = viewName;
   }

   @Override
   public ISqlQuery getQuery()
   {
      return mSelectQuery;
   }

   @Override
   public void setViewName(String viewName)
   {
      mViewName = viewName;
   }

   @Override
   public String getViewName()
   {
      return mViewName;
   }

   @Override
   public void accept(ISqlExpressionVisitor visitor)
   {
      visitor.visit(this);
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + getQuery().hashCode();
      result = prime * result + getViewName().hashCode();
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
      final SqlSubQuery other = (SqlSubQuery) obj;
      return getQuery().equals(other.getQuery())
            && getViewName().equals(other.getViewName());
   }

   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("SQ"); //$NON-NLS-1$
      sb.append("("); //$NON-NLS-1$
      sb.append("..."); //$NON-NLS-1$
      sb.append(")"); //$NON-NLS-1$
      sb.append("["); //$NON-NLS-1$
      sb.append(getViewName());
      sb.append("]"); //$NON-NLS-1$
      return sb.toString();
   }
}
