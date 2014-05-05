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
package com.obidea.semantika.queryanswer.parser;

import com.obidea.semantika.expression.base.IQueryExt;
import com.obidea.semantika.expression.base.QuerySet;

public class SparqlFactory
{
   public static QuerySet<IQueryExt> create(String sparqlString) throws SparqlParserException
   {
      return create(sparqlString, new SparqlParser());
   }

   public static QuerySet<IQueryExt> create(String sparqlString, AbstractSparqlParser parser) throws SparqlParserException
   {
      IQueryExt query = parser.parse(sparqlString);
      QuerySet<IQueryExt> querySet = new QuerySet<IQueryExt>();
      querySet.add(query);
      return querySet;
   }
}
