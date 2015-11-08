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
package com.obidea.semantika.mapping.parser.termalxml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.obidea.semantika.exception.SemantikaException;

public class TermalTemplate
{
   private static final Pattern templateFunctionCall = Pattern.compile("(.*)\\(([^\\)]*)\\)"); //$NON-NLS-1$

   private String mTemplateFunction;
   private Map<String, String> mTemplateMapper;

   public TermalTemplate(String templateFunction, Map<String, String> templateMapper)
   {
      mTemplateFunction = templateFunction;
      mTemplateMapper = templateMapper;
   }

   public String getTemplateString() throws SemantikaException
   {
      String templateName = getFunctionName();
      return findTemplateString(templateName); // Find the corresponding template string
   }

   private String getFunctionName() throws SemantikaException
   {
      Matcher m = templateFunctionCall.matcher(mTemplateFunction);
      if (m.matches()) {
         return m.group(1).trim();
      }
      throw new SemantikaException("Invalid template call expression"); //$NON-NLS-1$
   }

   public List<String> getColumnNames() throws SemantikaException
   {
      Matcher m = templateFunctionCall.matcher(mTemplateFunction);
      if (m.matches()) {
         List<String> arguments = new ArrayList<String>();
         String[] args = m.group(2).split(","); //$NON-NLS-1$
         for (int i = 0; i < args.length; i++) {
            arguments.add(args[i].trim());
         }
         return arguments;
      }
      throw new SemantikaException("Invalid template call expression"); //$NON-NLS-1$
   }

   private String findTemplateString(String templateName) throws SemantikaException
   {
      if (mTemplateMapper.containsKey(templateName)) {
         return mTemplateMapper.get(templateName);
      }
      throw new SemantikaException("Template name not found \"" + templateName + "\""); //$NON-NLS-1$ //$NON-NLS-2$
   }
}
