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
package com.obidea.semantika.queryanswer.processor;

public class NameGenerator
{
   private static final String NAME_TEMPLATE = "OBDA_VIEW%d";

   private int mIndex = 0;
   private String mLastNameGenerated = ""; //$NON-NLS-1$

   public String getNextUniqueName()
   {
      mIndex++;
      mLastNameGenerated = String.format(NAME_TEMPLATE, mIndex);
      return getLastNameGenerated();
   }

   public String getLastNameGenerated()
   {
      return mLastNameGenerated;
   }

   public void reset()
   {
      mIndex = 0;
   }
}
