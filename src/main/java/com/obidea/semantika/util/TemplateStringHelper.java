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
package com.obidea.semantika.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TemplateStringHelper
{
   private static final Pattern uriTemplateResult = Pattern.compile("([^\\s]*) :|\"([^\"]*)\""); //$NON-NLS-1$

   public static String getTemplateString(String input)
   {
      Matcher m = uriTemplateResult.matcher(input);
      if (m.find()) {
         return m.group(1);
      }
      return null; // should never return this
   }

   public static List<String> getTemplateArguments(String input)
   {
      Matcher m = uriTemplateResult.matcher(input);
      List<String> arguments = new ArrayList<String>();
      while (m.find()) {
         String arg = m.group(2);
         if (!StringUtils.isEmpty(arg)) {
            arguments.add(arg);
         }
      }
      return arguments;
   }

   public static String buildUri(String input)
   {
      String templateString = getTemplateString(input);
      List<String> arguments = getTemplateArguments(input);
      
      String uriString = templateString; // the URI string is based on the template
      for (int i = 0; i < arguments.size(); i++) {
         String value = arguments.get(i);
         String str = StringUtils.useUnderscore(value);
         uriString = uriString.replace(holder(i+1), str);
      }
      return uriString;
   }

   private static String holder(int index)
   {
      return String.format("{%d}", index); //$NON-NLS-1$
   }
}
