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
package com.obidea.semantika.database.sql.deparser;

public abstract class TextFormatter
{
   private static final String TAB_STRING = "   ";

   protected int mTabCounter = 0;

   private StringBuilder mStringBuilder;

   protected void initStringBuilder()
   {
      mStringBuilder = new StringBuilder();
   }

   protected String flushStringBuilder()
   {
      return mStringBuilder.toString().trim();
   }

   protected void append(String value)
   {
      mStringBuilder.append(value);
   }

   protected void space()
   {
      mStringBuilder.append(" "); //$NON-NLS-1$
   }

   protected void tab()
   {
      mStringBuilder.append(TAB_STRING);
   }

   protected void untab()
   {
      int lastIndex = mStringBuilder.length();
      int startIndex = lastIndex - TAB_STRING.length();
      String testString = mStringBuilder.substring(startIndex);
      if (testString.equals(TAB_STRING)) {
         mStringBuilder.delete(startIndex, lastIndex);
      }
   }

   protected void newline()
   {
      mStringBuilder.append("\n"); //$NON-NLS-1$
      for (int i = 0; i < mTabCounter; i++) {
         tab();
      }
   }

   protected void shiftRight()
   {
      tab();
      mTabCounter++;
   }

   protected void shiftLeft()
   {
      untab();
      mTabCounter--;
   }

   protected int getTabulator()
   {
      return mTabCounter;
   }
}
