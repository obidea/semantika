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
package com.obidea.semantika.queryanswer.internal;

import java.util.ArrayList;
import java.util.List;

public final class QueryModifiers
{
   public static final int NO_OFFSET = -1;
   public static final int NO_LIMIT = -1;

   private int mOffset = NO_OFFSET;
   private int mLimit = NO_LIMIT;
   private List<String> mAscendingOrderColumns = new ArrayList<String>();
   private List<String> mDescendingOrderColumns = new ArrayList<String>();

   private boolean mIsModifiersSet = false;

   public void setOffset(int offset)
   {
      if (offset >= 0) {
         mIsModifiersSet = true;
         mOffset = offset;
      }
   }

   public int getOffset()
   {
      return mOffset;
   }

   public void setLimit(int limit)
   {
      if (limit >= 0) {
         mIsModifiersSet = true;
         mLimit = limit;
      }
   }

   public int getLimit()
   {
      return mLimit;
   }

   public void setAscendingOrder(String columnName)
   {
      mIsModifiersSet = true;
      mAscendingOrderColumns.add(columnName);
   }

   public List<String> getAscendingOrder()
   {
      return mAscendingOrderColumns;
   }

   public void setDescendingOrder(String columnName)
   {
      mIsModifiersSet = true;
      mDescendingOrderColumns.add(columnName);
   }

   public List<String> getDescendingOrder()
   {
      return mDescendingOrderColumns;
   }

   public boolean isSet()
   {
      return mIsModifiersSet;
   }

   public void clear()
   {
      reset();
      mIsModifiersSet = false;
   }

   private void reset()
   {
      mOffset = NO_OFFSET;
      mLimit = NO_LIMIT;
      mAscendingOrderColumns = new ArrayList<String>();
      mDescendingOrderColumns = new ArrayList<String>();
   }
}
