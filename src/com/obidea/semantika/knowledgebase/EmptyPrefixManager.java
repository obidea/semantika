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
package com.obidea.semantika.knowledgebase;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EmptyPrefixManager implements IPrefixManager
{
   @Override
   public void copy(IPrefixManager otherManager)
   {
      // NO-OP
   }

   @Override
   public String getDefaultNamespace()
   {
      return ""; //$NON-NLS-1$
   }

   @Override
   public boolean containsPrefixMapping(String prefixName)
   {
      return false;
   }

   @Override
   public String getNamespace(String prefixName)
   {
      return ""; //$NON-NLS-1$
   }

   @Override
   public Map<String, String> getPrefixMapper()
   {
      return new HashMap<String, String>(); // returns an empty map
   }

   @Override
   public Set<String> getPrefixNames()
   {
      return new HashSet<String>(); // returns an empty set
   }

   @Override
   public URI expand(String qname)
   {
      return null;
   }

   @Override
   public String shorten(URI uri)
   {
      return uri.toString();
   }

}
