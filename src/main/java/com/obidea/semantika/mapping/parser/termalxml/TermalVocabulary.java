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

public enum TermalVocabulary
{
   /*
    * XML tags
    */
   PREFIX("prefix"), //$NON-NLS-1$
   PROGRAM("program"), //$NON-NLS-1$
   URI_TEMPLATE("uri-template"), //$NON-NLS-1$
   MAPPING("mapping"), //$NON-NLS-1$
   COMMENT("comment"), //$NON-NLS-1$
   LOGICAL_TABLE("logical-table"), //$NON-NLS-1$
   SUBJECT_MAP("subject-map"), //$NON-NLS-1$
   PREDICATE_OBJECT_MAP("predicate-object-map"), //$NON-NLS-1$
   
   /*
    * XML attributes
    */
   ID("id"), //$NON-NLS-1$
   NAME("name"), //$NON-NLS-1$
   VALUE("value"), //$NON-NLS-1$
   NAMESPACE("ns"); //$NON-NLS-1$

   private String mName;

   TermalVocabulary(String name)
   {
      mName = name;
   }

   public String getLocalName()
   {
      return mName;
   }

   public String getPrefix()
   {
      return "tml"; //$NON-NLS-1$
   }

   public String getNamespace()
   {
      return "http://www.obidea.com/ns/termal#"; //$NON-NLS-1$
   }

   public String getQName()
   {
      return getPrefix() + ":" + getLocalName(); //$NON-NLS-1$
   }
}
