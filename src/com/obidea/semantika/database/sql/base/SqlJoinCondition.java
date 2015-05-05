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
package com.obidea.semantika.database.sql.base;

public class SqlJoinCondition implements ISqlObject
{
   private static final long serialVersionUID = 629451L;

   private ISqlColumn mLeftColumn;
   private ISqlColumn mRighColumn;

   public SqlJoinCondition(ISqlColumn leftColumn, ISqlColumn rightColumn)
   {
      mLeftColumn = leftColumn;
      mRighColumn = rightColumn;
   }

   public ISqlColumn getLeftColumn()
   {
      return mLeftColumn;
   }

   public ISqlColumn getRightColumn()
   {
      return mRighColumn;
   }

   @Override
   public String toString()
   {
      return mLeftColumn + "=" + mRighColumn; //$NON-NLS-1$
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + getLeftColumn().hashCode();
      result = prime * result + getRightColumn().hashCode();
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
      final SqlJoinCondition other = (SqlJoinCondition) obj;
      return getLeftColumn().equals(other.getLeftColumn())
            && getRightColumn().equals(other.getRightColumn());
   }
}
