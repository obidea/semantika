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
package com.obidea.semantika.knowledgebase;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.obidea.semantika.util.Namespaces;
import com.obidea.semantika.util.StringUtils;
import com.obidea.semantika.util.XmlUtils;

public class DefaultPrefixManager implements IPrefixManager
{
   private Map<String, String> mPrefixMap = new HashMap<String, String>();

   /**
    * Creates an empty prefix manager without the default namespace.
    */
   public DefaultPrefixManager()
   {
      setupDefaultPrefixes();
   }

   /**
    * Creates an empty prefix manager with the given default namespace. The
    * default prefix is presented as an empty string.
    *
    * @param defaultNamespace
    *           The default namespace.
    */
   public DefaultPrefixManager(String defaultNamespace)
   {
      if (!StringUtils.isEmpty(defaultNamespace)) {
         setDefaultPrefix(defaultNamespace);
      }
      setupDefaultPrefixes();
   }

   /**
    * Creates a new prefix manager where it copies another prefix manager.
    * 
    * @param other
    *           Another prefix manager to copy its content.
    */
   public DefaultPrefixManager(IPrefixManager other)
   {
      for (String prefixName : other.getPrefixNames()) {
         String namespace = other.getNamespace(prefixName);
         if (!StringUtils.isEmpty(namespace)) {
            setPrefix(prefixName, namespace);
         }
      }
   }

   private void setupDefaultPrefixes()
   {
      setPrefix("owl", Namespaces.OWL.toString()); //$NON-NLS-1$
      setPrefix("rdfs", Namespaces.RDFS.toString()); //$NON-NLS-1$
      setPrefix("rdf", Namespaces.RDF.toString()); //$NON-NLS-1$
      setPrefix("xsd", Namespaces.XSD.toString()); //$NON-NLS-1$
   }

   @Override
   public void copy(Map<String, String> mapper)
   {
      for (String prefix : mapper.keySet()) {
         setPrefix(prefix, mapper.get(prefix));
      }
   }

   /**
    * Sets the default namespace.
    * 
    * @param defaultNamespace
    *           The namespace to be used as the default namespace. Note that the
    *           value may be <code>null</code> in order to clear the default
    *           namespace.
    */
   public void setDefaultPrefix(String defaultNamespace)
   {
      setPrefix("", defaultNamespace); //$NON-NLS-1$
   }

   @Override
   public String getDefaultNamespace()
   {
      return mPrefixMap.get(""); //$NON-NLS-1$
   }

   @Override
   public boolean containsPrefixMapping(String prefixName)
   {
      return mPrefixMap.containsKey(prefixName) && mPrefixMap.get(prefixName) != null;
   }

   /**
    * Adds a prefix name - namespace to prefix mapping. If the prefix name
    * already exists, it will get replaced by the new namespace.
    * 
    * @param prefixName
    *           name The prefix name (must not be null)
    * @param namespace
    *           The namespace
    * @throws NullPointerException
    *            if the prefix name or prefix is <code>null</code>.
    * @throws IllegalArgumentException
    *            if the prefix name does not end with a colon.
    */
   public void setPrefix(String prefixName, String namespace)
   {
      if (StringUtils.isEmpty(namespace)) {
         throw new IllegalArgumentException("Namespace must not be null or empty"); //$NON-NLS-1$
      }
      mPrefixMap.put(prefixName, namespace);
   }

   public void setAll(Map<String, String> prefixMap)
   {
      for (String prefixName : prefixMap.keySet()) {
         setPrefix(prefixName, prefixMap.get(prefixName));
      }
   }

   @Override
   public String getNamespace(String prefixName)
   {
      return mPrefixMap.get(prefixName);
   }

   @Override
   public Map<String, String> getPrefixMapper()
   {
      return Collections.unmodifiableMap(mPrefixMap);
   }

   @Override
   public Set<String> getPrefixNames()
   {
      return mPrefixMap.keySet();
   }

   @Override
   public URI expand(String qname)
   {
      int colonPos = qname.indexOf(":"); //$NON-NLS-1$
      if (colonPos != -1) {
         String prefixName = qname.substring(0, colonPos);
         if (!containsPrefixMapping(prefixName)) {
            throw new PrefixNotFoundException(prefixName, this);
         }
         String namespace = getNamespace(prefixName);
         String localName = qname.substring(colonPos + 1);
         return URI.create(namespace + localName);
      }
      else {
         String localName = qname;
         String defaultNamespace = getDefaultNamespace();
         if (StringUtils.isEmpty(defaultNamespace)) {
            return URI.create(localName);
         }
         else {
            return URI.create(defaultNamespace + localName);
         }
      }
   }

   @Override
   public String shorten(URI uri)
   {
      String uriString = uri.toString();
      String namespace = XmlUtils.getNCNamePrefix(uriString);
      for (String prefixName : getPrefixNames()) {
         String candidateNamespace = getNamespace(prefixName);
         if (namespace.equals(candidateNamespace)) {
            return prefixName + ":" + XmlUtils.getNCNameSuffix(uriString);
         }
      }
      throw new NamespaceNotFoundException(namespace, this);
   }

   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("Size: " + getPrefixMapper().size()); //$NON-NLS-1$
      sb.append("\n"); //$NON-NLS-1$
      for (String prefixName : getPrefixNames()) {
         if (prefixName.equals("")) {
            sb.append("[default]");
         }
         else {
            sb.append(prefixName);
         }
         sb.append(" = "); //$NON-NLS-1$
         sb.append(getNamespace(prefixName));
         sb.append("\n"); //$NON-NLS-1$
      }
      return sb.toString();
   }
}
