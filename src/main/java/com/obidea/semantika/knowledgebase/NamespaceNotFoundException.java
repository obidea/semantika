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
package com.obidea.semantika.knowledgebase;

import com.obidea.semantika.exception.NotFoundException;

public class NamespaceNotFoundException extends NotFoundException
{
   private static final long serialVersionUID = 629451L;

   private IPrefixManager mPrefixManager;

   public NamespaceNotFoundException(String namespace, IPrefixManager prefixManager)
   {
      super(namespace);
      mPrefixManager = prefixManager;
   }

   @Override
   public String getMessage()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("Known namespaces:"); //$NON-NLS-1$
      sb.append("\n"); //$NON-NLS-1$
      for (String prefixName : mPrefixManager.getPrefixNames()) {
         sb.append(mPrefixManager.getNamespace(prefixName));
         sb.append(" = "); //$NON-NLS-1$
         sb.append(prefixName);
         sb.append("\n"); //$NON-NLS-1$
      }
      return super.getMessage() + "\n" + sb.toString(); //$NON-NLS-1$
   }
}
