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
package com.obidea.semantika.mapping.base.sql;

import java.util.HashSet;
import java.util.Set;

import com.obidea.semantika.database.sql.base.ISqlColumn;
import com.obidea.semantika.database.sql.base.ISqlExpression;
import com.obidea.semantika.database.sql.base.ISqlExpressionVisitor;
import com.obidea.semantika.database.sql.base.ISqlJoin;
import com.obidea.semantika.database.sql.base.SqlJoinCondition;
import com.obidea.semantika.knowledgebase.TermSubstitutionBinding;

public class SqlJoin extends SyntacticSugarMediator implements ISqlJoin
{
   private static final long serialVersionUID = 629451L;

   private boolean mIsInnerJoin = false;
   private boolean mIsLeftJoin = false;

   private ISqlExpression mLeftExpression;
   private ISqlExpression mRightExpression;

   private Set<SqlJoinCondition> mJoinConditions = new HashSet<SqlJoinCondition>();
   private Set<ISqlExpression> mFilters = new HashSet<ISqlExpression>();

   public SqlJoin()
   {
      super();
   }

   @Override
   public String getName()
   {
      if (isInnerJoin()) {
         return "JOIN"; //$NON-NLS-1$
      }
      else if (isLeftJoin()) {
         return "LEFTJOIN"; //$NON-NLS-1$
      }
      else {
         return "<undefined>"; //$NON-NLS-1$
      }
   }

   @Override
   public void setInnerJoin(boolean value)
   {
      mIsInnerJoin = value;
   }

   @Override
   public boolean isInnerJoin()
   {
      return mIsInnerJoin;
   }

   @Override
   public void setLeftJoin(boolean value)
   {
      mIsLeftJoin = value;
   }

   @Override
   public boolean isLeftJoin()
   {
      return mIsLeftJoin;
   }

   public void setLeftExpression(ISqlExpression expression)
   {
      mLeftExpression = expression;
   }

   @Override
   public ISqlExpression getLeftExpression()
   {
      return mLeftExpression;
   }

   public void setRightExpression(ISqlExpression expression)
   {
      mRightExpression = expression;
   }

   @Override
   public ISqlExpression getRightExpression()
   {
      return mRightExpression;
   }

   @Override
   public void addJoinCondition(ISqlColumn leftColumn, ISqlColumn rightColumn)
   {
      mJoinConditions.add(new SqlJoinCondition(leftColumn, rightColumn));
   }

   public void addJoinConditions(Set<SqlJoinCondition> joinConditions)
   {
      mJoinConditions.addAll(joinConditions);
   }

   @Override
   public Set<SqlJoinCondition> getJoinConditions()
   {
      return mJoinConditions;
   }

   @Override
   public boolean hasJoinConditions()
   {
      return mJoinConditions.isEmpty() ? false : true;
   }

   @Override
   public void addFilter(ISqlExpression filter)
   {
      mFilters.add(filter);
   }

   public void addFilters(Set<ISqlExpression> filters)
   {
      mFilters.addAll(filters);
   }

   @Override
   public Set<ISqlExpression> getFilters()
   {
      return mFilters;
   }

   @Override
   public boolean hasFilters()
   {
      return mFilters.isEmpty() ? false : true;
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

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + getName().hashCode();
      result = prime * result + getLeftExpression().hashCode();
      result = prime * result + getRightExpression().hashCode();
      result = prime * result + getJoinConditions().hashCode();
      result = prime * result + getFilters().hashCode();
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
      final SqlJoin other = (SqlJoin) obj;
      return getName().equals(other.getName())
            && getLeftExpression().equals(other.getLeftExpression())
            && getRightExpression().equals(other.getRightExpression())
            && getJoinConditions().equals(other.getJoinConditions())
            && getFilters().equals(other.getFilters());
   }

   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append(getName());
      sb.append("("); //$NON-NLS-1$
      sb.append(mLeftExpression);
      sb.append(", "); //$NON-NLS-1$
      sb.append(mRightExpression);
      sb.append(", "); //$NON-NLS-1$
      sb.append(mJoinConditions);
      sb.append(", "); //$NON-NLS-1$
      sb.append(mFilters);
      sb.append(")"); //$NON-NLS-1$
      return sb.toString();
   }
}
