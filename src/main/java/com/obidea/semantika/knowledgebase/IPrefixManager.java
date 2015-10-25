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

import java.net.URI;
import java.util.Map;
import java.util.Set;

public interface IPrefixManager
{
   /**
    * Copies all pairs of prefix-namespace to this prefix manager.
    *
    * @param mapper
    *          the prefix mapper.
    */
   void copy(Map<String, String> mapper);

   /**
    * Gets the default namespace. The default namespace is denoted by the prefix
    * name ":".
    * 
    * @return Returns the default prefix or <code>null</code> if there is no default
    *         prefix.
    */
   String getDefaultNamespace();

   /**
    * Determines if this manager knows about a given prefix name and it contains
    * a (non-null) mapping for the prefix.
    * 
    * @param prefixName
    *           The prefix name to be tested for.
    * @return Retuns <code>true</code> if the manager knows about this prefix and there
    *         is a non-null mapping for this prefix.
    */
   boolean containsPrefixMapping(String prefixName);

   /**
    * Gets the namespace that is bound to a particular prefix name. Note that
    * specifying ":" corresponds to requesting the default prefix and will
    * return the same result as a call to the <code>getDefaultNamespace()</code>
    * method.
    * 
    * @param prefixName
    *           The prefix name. A string that represents a prefix name of the
    *           prefix to be retrieved.
    * @return Returns the prefix or <code>null</code> if there is no namespace bound
    *         to this prefix name or the prefix name doesn't exist.
    */
   String getNamespace(String prefixName);

   /**
    * Gets a map that maps prefix names to namespaces.
    * 
    * @return Returns a map of prefix names to namespaces.
    */
   Map<String, String> getPrefixMapper();

   /**
    * Gets the prefix names that are contained in this prefix manager
    * 
    * @return Returns the a set of prefix names.
    */
   Set<String> getPrefixNames();

   /**
    * Gets the URI from the given qualified name. The name must use a prefix
    * that is registered in this manager, or a runtime exception will be thrown.
    * 
    * @param qname
    *           the qualified name to expand to a full URI.
    * @return Returns a URI object.
    */
   URI expand(String qname);

   /**
    * Gets the qualified name (i.e., prefixName:localName) for a given URI. The
    * name must contains a namespace suffix that is registered with this
    * manager, or a runtime exception will be thrown.
    * 
    * @param uri
    *           the URI to shorten.
    * @return Returns a qualified name string.
    */
   String shorten(URI uri);
}
