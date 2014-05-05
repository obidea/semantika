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
package com.obidea.semantika.mapping.parser.termalxml;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FunctionCallUtils
{
   private static final Pattern templateFunctionCall = Pattern.compile("(.*)\\(([^\\)]*)\\)"); //$NON-NLS-1$

   public static String getFunctionName(String functionCall) throws InvalidFunctionCallException
   {
      Matcher m = templateFunctionCall.matcher(functionCall);
      if (m.matches()) {
         return m.group(1).trim();
      }
      return null;
   }

   public static List<String> getFunctionParameters(String functionCall) throws InvalidFunctionCallException
   {
      Matcher m = templateFunctionCall.matcher(functionCall);
      if (m.matches()) {
         List<String> arguments = new ArrayList<String>();
         String[] args = m.group(2).split(","); //$NON-NLS-1$
         for (int i = 0; i < args.length; i++) {
            arguments.add(args[i].trim());
         }
         return arguments;
      }
      return null;
   }
}
