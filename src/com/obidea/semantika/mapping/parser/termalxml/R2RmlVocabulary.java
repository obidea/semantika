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

public enum R2RmlVocabulary
{
   LOGICAL_TABLE("logicalTable"), //$NON-NLS-1$
   SQL_QUERY("sqlQuery"), //$NON-NLS-1$
   SQL_VERSION("sqlVersion"), //$NON-NLS-1$
   SQL2008("SQL2008"), //$NON-NLS-1$
   TABLE_NAME("tableName"), //$NON-NLS-1$
   SUBJECT_MAP("subjectMap"), //$NON-NLS-1$
   CONSTANT("constant"), //$NON-NLS-1$
   TEMPLATE("template"), //$NON-NLS-1$
   GRAPH("graph"), //$NON-NLS-1$
   GRAPH_MAP("graphMap"), //$NON-NLS-1$
   TERM_TYPE("termType"), //$NON-NLS-1$
   IRI("IRI"), //$NON-NLS-1$
   BLANK_NODE("BlankNode"), //$NON-NLS-1$
   LITERAL("Literal"), //$NON-NLS-1$
   CLASS("class"), //$NON-NLS-1$
   PREDICATE_OBJECT_MAP("predicateObjectMap"), //$NON-NLS-1$
   SUBJECT("subject"), //$NON-NLS-1$
   PREDICATE("predicate"), //$NON-NLS-1$
   OBJECT("object"), //$NON-NLS-1$
   OBJECT_MAP("objectMap"), //$NON-NLS-1$
   COLUMN("column"), //$NON-NLS-1$
   DATAYPE("datatype"), //$NON-NLS-1$
   LANGUAGE("language"), //$NON-NLS-1$
   INVERSE_EXPRESSION("inverseExpression"), //$NON-NLS-1$
   PARENT_TRIPLES_MAP("parentTriplesMap"), //$NON-NLS-1$
   JOIN_CONDITION("joinCondition"), //$NON-NLS-1$
   CHILD("child"), //$NON-NLS-1$
   PARENT("parent"); //$NON-NLS-1$


   private String mName;

   R2RmlVocabulary(String name)
   {
      mName = name;
   }

   public String getLocalName()
   {
      return mName;
   }

   public String getPrefix()
   {
      return "rr"; //$NON-NLS-1$
   }

   public String getNamespace()
   {
      return "http://www.w3.org/ns/r2rml#"; //$NON-NLS-1$
   }

   public String getQName()
   {
      return getPrefix() + ":" + getLocalName(); //$NON-NLS-1$
   }

   public String getUri()
   {
      return getNamespace() + getLocalName();
   }

   @Override
   public String toString()
   {
      return getUri();
   }
}
