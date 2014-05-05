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
package com.obidea.semantika.mapping.sql.parser;

import java.sql.Types;

import com.obidea.semantika.database.base.IColumn;
import com.obidea.semantika.database.base.ITable;

public class MockColumn implements IColumn
{
   private static final long serialVersionUID = 629451L;

   public static final String MOCK_NAMESPACE = "UserQuery"; //$NON-NLS-1$

   private String mColumnName;

   public MockColumn(String name)
   {
      mColumnName = name;
   }

   @Override
   public String getLocalName()
   {
      return mColumnName;
   }

   @Override
   public String getNamespace()
   {
      return MOCK_NAMESPACE;
   }

   @Override
   public String getFullName()
   {
      return mColumnName;
   }

   @Override
   public ITable getTableOrigin()
   {
      return null;
   }

   @Override
   public String getSchemaName()
   {
      return ""; //$NON-NLS-1$ // empty schema
   }

   @Override
   public String getTableName()
   {
      return MOCK_NAMESPACE;
   }

   @Override
   public int getSqlType()
   {
      return Types.VARCHAR; // default type
   }

   @Override
   public void setPrimaryKey(boolean isPrimaryKey)
   {
      // NO-OP
   }

   @Override
   public boolean isPrimaryKey()
   {
      return false;
   }
}
