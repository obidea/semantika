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
package com.obidea.semantika.mapping.base.sql.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.obidea.semantika.database.sql.base.SqlSelectItem;

public class SelectItemList implements Iterator<SqlSelectItem>, Iterable<SqlSelectItem>
{
   private List<SqlSelectItem> mSelectItems = new ArrayList<SqlSelectItem>();

   private int mSize = 0;
   private int mCurrent = 0;

   public void add(SqlSelectItem selectItem)
   {
      mSelectItems.add(selectItem);
      mSize++; // increment the size each time new table is added
   }

   public SqlSelectItem get(int index)
   {
      return mSelectItems.get(index);
   }

   @Override
   public boolean hasNext()
   {
      return mCurrent < mSize;
   }

   @Override
   public SqlSelectItem next()
   {
      if (hasNext()) {
         return mSelectItems.get(mCurrent++);
      }
      throw new NoSuchElementException();
   }

   @Override
   public void remove()
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public Iterator<SqlSelectItem> iterator()
   {
      mCurrent = 0; // clear the current count every time client code asks for an iterator
      return this;
   }
}
