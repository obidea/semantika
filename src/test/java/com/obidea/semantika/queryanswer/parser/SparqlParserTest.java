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
package com.obidea.semantika.queryanswer.parser;

import org.junit.Before;
import org.junit.Test;

import com.obidea.semantika.expression.base.IQueryExt;

public class SparqlParserTest
{
   private SparqlParser parser;

   @Before
   public void init() {
      parser = new SparqlParser();
   }

   @Test
   public void testSyntaxBasic01() throws SparqlParserException
   {
      String sparqlString = "SELECT * WHERE { ?x ?y ?z }";
      IQueryExt query = parser.parse(sparqlString);
      
   }

   @Test
   public void testSyntaxFormConstruct01() throws SparqlParserException
   {
      String sparqlString = "CONSTRUCT { ?s <http://example/p1> <http://example/o> . ?s <http://example/p2> ?o } WHERE {?s ?p ?o}";
      IQueryExt query = parser.parse(sparqlString);
   }
}
