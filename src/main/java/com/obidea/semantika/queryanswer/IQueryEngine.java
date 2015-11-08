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
package com.obidea.semantika.queryanswer;

import com.obidea.semantika.knowledgebase.model.IKnowledgeBase;
import com.obidea.semantika.queryanswer.exception.QueryAnswerException;
import com.obidea.semantika.queryanswer.result.IQueryResult;

public interface IQueryEngine
{
   /**
    * Returns the knowledge base object used to process the input query.
    */
   IKnowledgeBase getKnowledgeBase();

   /**
    * Starts the query engine. Required to be called initially to open the query answer service.
    */
   void start() throws QueryEngineException;

   /**
    * Stops the query engine and performs some house cleaning activities.
    */
   void stop() throws QueryEngineException;

   /**
    * Checks if the engine is started already.
    * 
    * @return Returns <code>true</code> if it has been started, or <code>false</code> otherwise.
    */
   boolean isStarted();

   /**
    * Evaluates the given input SPARQL query and returns the answer result.
    * 
    * @param sparql
    *           The input query in SPARQL language.
    * @return Returns the answer result.
    */
   IQueryResult evaluate(String sparql) throws QueryAnswerException;

   /**
    * Returns the corresponding SQL query from the given input SPARQL query with respect to the
    * given knowledge base.
    * 
    * @param sparql
    *           The input query in SPARQL language.
    * @return Returns SQL string.
    */
   String translate(String sparql) throws QueryAnswerException;
}
