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
package com.obidea.semantika.util;

import java.net.URI;

public enum RdfVocabulary
{
   /*
    * Source: http://www.w3.org/TR/rdf-schema
    */
   LANGSTRING("langString"), //$NON-NLS-1$
   HTML("HTML"), //$NON-NLS-1$
   XMLLITERAL("XMLLiteral"), //$NON-NLS-1$
   PROPERTY("Property"), //$NON-NLS-1$
   TYPE("type"), //$NON-NLS-1$
   BAG("Bag"), //$NON-NLS-1$
   SEQ("Seq"), //$NON-NLS-1$
   ALT("Alt"), //$NON-NLS-1$
   LIST("List"), //$NON-NLS-1$
   FIRST("first"), //$NON-NLS-1$
   REST("rest"), //$NON-NLS-1$
   NIL("nil"), //$NON-NLS-1$
   STATEMENT("Statement"), //$NON-NLS-1$
   SUBJECT("subject"), //$NON-NLS-1$
   PREDICATE("predicate"), //$NON-NLS-1$
   OBJECT("object"), //$NON-NLS-1$
   VALUE("value"); //$NON-NLS-1$
   
   private String mName;

   RdfVocabulary(String name)
   {
      mName = name;
   }

   public String getLocalName()
   {
      return mName;
   }

   public String getPrefix()
   {
      return "rdf"; //$NON-NLS-1$
   }

   public String getNamespace()
   {
      return Namespaces.RDF;
   }

   public String getQName()
   {
      return getPrefix() + ":" + getLocalName(); //$NON-NLS-1$
   }

   public URI getUri()
   {
      return URI.create(getNamespace() + getLocalName());
   }
}
