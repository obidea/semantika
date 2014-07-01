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
package com.obidea.semantika.materializer;

import java.util.ArrayList;
import java.util.List;

import com.obidea.semantika.database.sql.base.ISqlExpression;
import com.obidea.semantika.database.sql.base.SqlSelectItem;
import com.obidea.semantika.exception.SemantikaRuntimeException;
import com.obidea.semantika.mapping.base.sql.SqlColumn;
import com.obidea.semantika.mapping.base.sql.SqlQuery;
import com.obidea.semantika.mapping.base.sql.SqlUriConcat;
import com.obidea.semantika.mapping.base.sql.SqlUriValue;

public class TriplesProjection
{
   /**
    * A constant to indicate the projected data as an object identifier.
    */
   static final int DATA_URI = 0;

   /**
    * A constant to indicate the projected data as a literal value.
    */
   static final int DATA_LITERAL = 1;

   private List<SqlSelectItem> mSelectItemList;

   /**
    * The sole constructor.
    */
   public TriplesProjection(SqlQuery query)
   {
      mSelectItemList = new ArrayList<SqlSelectItem>(query.getSelectItems());
   }

   /**
    * Retrieves the label of the designated column position in this projection.
    *
    * @param position
    *           The first index position is 1, the second is 2, etc.
    * @return the projection label.
    */
   public String getLabel(int position)
   {
      return getSelectItem(position).getLabelName();
   }

   /**
    * Retrieves the data category of the designated column position in this projection. The
    * constants defined in the interface <code>IProjection</code> are the possible projection types.
    * 
    * @param position
    *           The first index position is 1, the second is 2, etc.
    * @return the data category, which will be one of the following constants:
    *         <code>IProjection.DATA_URI</code>,
    *         <code>IProjection.DATA_LITERAL</code>.
    */
   public int getDataCategory(int position)
   {
      ISqlExpression expression = getSelectItem(position).getExpression();
      if (expression instanceof SqlUriConcat) {
         return DATA_URI;
      }
      else if (expression instanceof SqlUriValue) {
         return DATA_URI;
      }
      else if (expression instanceof SqlColumn) {
         return DATA_LITERAL;
      }
      throw new SemantikaRuntimeException("Expression " + expression + " is not supported in query projection"); //$NON-NLS-1$ //$NON-NLS-2$
   }

   /**
    * Retrieves the datatype of the designated column position in this projection.
    * 
    * @param position
    *           The first index position is 1, the second is 2, etc.
    * @return the datatype string according to XML types.
    */
   public String getDatatype(int position)
   {
      ISqlExpression expression = getSelectItem(position).getExpression();
      if (expression instanceof SqlUriConcat) {
         return ((SqlUriConcat) expression).getDatatype();
      }
      else if (expression instanceof SqlColumn) {
         return ((SqlColumn) expression).getDatatype();
      }
      else if (expression instanceof SqlUriValue) {
         return ((SqlUriValue) expression).getDatatype();
      }
      throw new SemantikaRuntimeException("Expression " + expression + " is not supported in query projection"); //$NON-NLS-1$ //$NON-NLS-2$
   }

   private SqlSelectItem getSelectItem(int position)
   {
      return mSelectItemList.get(position-1);
   }
}
