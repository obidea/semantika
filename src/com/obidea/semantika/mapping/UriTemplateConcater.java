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
package com.obidea.semantika.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingFormatArgumentException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.obidea.semantika.expression.base.IVariable;

public final class UriTemplateConcater
{
   // {argument_index}
   private static final String FORMAT_SPECIFIER = "\\{(\\d+)\\}"; //$NON-NLS-1$
   private static Pattern sPattern = Pattern.compile(FORMAT_SPECIFIER);

   public String format(String templateString, List<? extends IVariable> variables) throws MissingFormatArgumentException
   {
      StringBuilder sb = new StringBuilder();
      
      // Index of last argument referenced
      int last = -1;
      FormatString[] fsa = parse(templateString);
      for (int i = 0; i < fsa.length; i++) {
         FormatString fs = fsa[i];
         int index = fs.index();
         switch (index) {
            case -1: // fixed string
               fs.add(null, sb);
               break;
            default: // explicit index
               last = index - 1;
               if (variables != null && last > variables.size() - 1) {
                  throw new MissingFormatArgumentException(fs.toString());
               }
               fs.add(variables.get(last).getName(), sb);
               break;
         }
      }
      return sb.toString();
   }

   private FormatString[] parse(String format)
   {
      ArrayList<FormatString> al = new ArrayList<FormatString>();
      Matcher m = sPattern.matcher(format);
      int i = 0;
      while (i < format.length()) {
         if (m.find(i)) {
            // Anything between the start of the string and the beginning
            // of the format specifier is fixed text.
            if (m.start() != i) {
               al.add(new FixedString(format.substring(i, m.start())));
            }
            // Add the argument index
            String index = m.group();
            al.add(new FormatSpecifier(index));
            i = m.end();
         }
         else {
            // Add the last fixed string, if any
            al.add(new FixedString(format.substring(i)));
            break;
         }
      }
      return (FormatString[]) al.toArray();
   }

   private interface FormatString
   {
      int index();
      void add(String s, StringBuilder sb);
      String toString();
   }

   private class FixedString implements FormatString
   {
      private String mStr;
      FixedString(String str) { mStr = str; }
      @Override public int index() { return -1; }
      @Override public void add(String s, StringBuilder sb) { sb.append(mStr); }
      @Override public String toString() { return mStr; }
   }

   private class FormatSpecifier implements FormatString
   {
      private int mIndex;
      FormatSpecifier(String str) { index(str); }
      private void index(String str) { mIndex = Integer.parseInt(str.substring(1, str.length())); }
      @Override public int index() { return mIndex; }
      @Override public void add(String s, StringBuilder sb) { sb.append("{" + s + "}"); } //$NON-NLS-1$ //$NON-NLS-2$
      @Override public String toString() { return "{" + index() + "}"; } //$NON-NLS-1$ //$NON-NLS-2$
   }
}
