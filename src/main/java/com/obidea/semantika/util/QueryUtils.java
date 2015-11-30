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
package com.obidea.semantika.util;

import org.openrdf.query.parser.QueryParserUtil;

import com.obidea.semantika.exception.SemantikaRuntimeException;
import com.obidea.semantika.queryanswer.QueryForm;

/**
 * @author Josef Hardi <josef.hardi@gmail.com>
 * @since 1.8
 */
public class QueryUtils
{
   public static QueryForm determineQueryForm(String sparql)
   {
      String strippedQuery = QueryParserUtil.removeSPARQLQueryProlog(sparql).toUpperCase();
      if (strippedQuery.startsWith("SELECT")) { //$NON-NLS-1$
         return QueryForm.SELECT;
      }
      else if (strippedQuery.startsWith("CONSTRUCT")) { //$NON-NLS-1$
         return QueryForm.CONSTRUCT;
      }
      else if (strippedQuery.startsWith("ASK")) { //$NON-NLS-1$
         return QueryForm.ASK;
      }
      else if (strippedQuery.startsWith("DESCRIBE")) { //$NON-NLS-1$
         return QueryForm.DESCRIBE;
      }
      throw new SemantikaRuntimeException("Unknown SPARQL query: " + sparql);
   }

   public static boolean isSelectQuery(String sparql)
   {
      return determineQueryForm(sparql) == QueryForm.SELECT;
   }

   public static boolean isConstructQuery(String sparql)
   {
      return determineQueryForm(sparql) == QueryForm.CONSTRUCT;
   }

   public static boolean isAskQuery(String sparql)
   {
      return determineQueryForm(sparql) == QueryForm.ASK;
   }

   public static boolean isDescribeQuery(String sparql)
   {
      return determineQueryForm(sparql) == QueryForm.DESCRIBE;
   }
}
