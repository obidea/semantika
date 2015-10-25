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
package com.obidea.semantika.mapping.parser.r2rml;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.obidea.semantika.util.StringUtils;

public class R2RmlTemplate
{
   private static final Pattern columnInCurlyBraces = Pattern.compile("\\{([^\\}]+)\\}");

   private int mIndex = 1;

   private String mTemplateString;
   private List<String> mColumnNames = new ArrayList<String>();

   public R2RmlTemplate(String templateString)
   {
      mTemplateString = templateString;
      process(templateString);
   }

   public String getTemplateString()
   {
      return mTemplateString;
   }

   public List<String> getColumnNames()
   {
      return mColumnNames;
   }

   private void process(String templateString)
   {
      Matcher m = columnInCurlyBraces.matcher(templateString);
      while (m.find()) {
         String arg = m.group(1);
         if (!StringUtils.isEmpty(arg)) {
            mTemplateString = mTemplateString.replace("{" + arg + "}", "{" + mIndex + "}");
            mColumnNames.add(arg);
            mIndex++;
         }
      }
   }
}
