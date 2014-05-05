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
package com.obidea.semantika.database.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrimaryKey extends DatabaseObject implements IPrimaryKey
{
   private static final long serialVersionUID = 629451L;

   private ITable mTableObject;

   private List<IColumn> mColumns;

   public PrimaryKey(final ITable table, final String name)
   {
      super(name);
      mTableObject = table;
      mColumns = new ArrayList<IColumn>();
      table.setPrimaryKey(this);
   }

   /**
    * Returns the associated table of this primary key object.
    */
   @Override
   public ITable getParentObject()
   {
      return mTableObject;
   }

   @Override
   public String getSourceTable()
   {
      return getNamespace();
   }

   @Override
   public void addKey(int keySequence, IColumn pkColumn)
   {
      preventOutOfBoundException(keySequence);
      mColumns.set(keySequence, pkColumn); // use set to replace any existing null value
   }

   /*
    * The insertion through keySequence may go right to the middle of the
    * list and therefore IndexOutOfBoundException can occur because index >
    * size. This method is used to prevent such exception by inserting
    * first null values until reaching the keySequence index.
    */
   private void preventOutOfBoundException(int keySequence)
   {
      int size = mColumns.size();
      if (keySequence >= size) {
         for (int i = size; i <= keySequence; i++) {
            mColumns.add(i, null);
         }
      }
   }

   @Override
   public boolean isCompound()
   {
      return mColumns.size() > 1 ? true : false;
   }

   @Override
   public List<IColumn> getKeys()
   {
      return Collections.unmodifiableList(mColumns);
   }
}
