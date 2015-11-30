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
package com.obidea.semantika.queryanswer.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.obidea.semantika.exception.SemantikaRuntimeException;

/**
 * @author Josef Hardi <josef.hardi@gmail.com>
 * @since 1.8
 */
public class GraphResult implements IQueryResult, Serializable
{
   private static final long serialVersionUID = 629451L;

   private final List<String> mSelectNames;
   private final Iterator<? extends IValueArray> mValueArraysIter;

   private IValueArray mValueArray;

   GraphResult(List<String> selectNames, Iterator<? extends IValueArray> valueArraysIter)
   {
      mSelectNames = selectNames;
      mValueArraysIter = valueArraysIter;
   }

   @Override
   public List<String> getSelectNames()
   {
      return mSelectNames;
   }

   @Override
   public IValueArray getValueArray()
   {
      return mValueArray;
   }

   @Override
   public boolean next()
   {
      boolean hasNext = mValueArraysIter.hasNext();
      if (hasNext) {
         mValueArray = mValueArraysIter.next();
      }
      return hasNext;
   }

   public static class Builder
   {
      private List<String> mSelectNames = new ArrayList<>();
      private List<IValueArray> mValueArrays = new ArrayList<>();

      public Builder()
      {
         // NO-OP
      }

      public void add(IValueArray valueArray)
      {
         if (valueArray.size() != 3) {
            throw new SemantikaRuntimeException("Graph result is not in triples (size: " + valueArray.size() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
         }
         if (mSelectNames.isEmpty()) {
            mSelectNames.addAll(valueArray.getSelectNames());
         }
         mValueArrays.add(valueArray);
      }

      public GraphResult build()
      {
         return new GraphResult(mSelectNames, mValueArrays.iterator());
      }
   }
}
