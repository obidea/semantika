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

import com.obidea.semantika.database.DatabaseObjectUtils;
import com.obidea.semantika.database.NamingUtils;
import com.obidea.semantika.util.StringUtils;

public abstract class DatabaseObject implements IDatabaseObject
{
   private static final long serialVersionUID = 629451L;

   protected String mName;

   public DatabaseObject(final String name)
   {
      if (StringUtils.isEmpty(name)) {
         throw new IllegalArgumentException("The object must have a non-empty name."); //$NON-NLS-1$
      }
      mName = name;
   }

   /**
    * Returns <i>unquoted</i> local name of this database object.
    */
   @Override
   public String getLocalName()
   {
      return mName;
   }

   /**
    * Returns <i>unquoted</i> namespace identifier of this database object.
    */
   @Override
   public String getNamespace()
   {
      return getParentObject().getFullName();
   }

   protected boolean hasNamespace()
   {
      return (getNamespace() != null) ? true : false;
   }

   protected abstract IDatabaseObject getParentObject();

   /**
    * Returns <i>unquoted</i> identifier of this database object.
    */
   @Override
   public String getFullName()
   {
      return NamingUtils.constructDatabaseObjectIdentifier(getNamespace(), getLocalName());
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + getFullName().hashCode();
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
      final DatabaseObject other = (DatabaseObject) obj;
      
      return getFullName().equals(other.getFullName());
   }

   @Override
   public String toString()
   {
      return DatabaseObjectUtils.toString(this);
   }
}
