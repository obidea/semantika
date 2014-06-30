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
package com.obidea.semantika.database.sql.dialect;

import java.util.List;

public interface IDialect
{
   void setQuoteString(String quoteString);
   String getQuoteString();

   void setSeparator(String separator);
   String getSeparator();

   String identifier(String... nameFragments);

   String alias(String value);
   String view(String value);

   String literal(String value);

   String add(String leftExpr, String rightExpr);
   String subtract(String leftExpr, String rightExpr);
   String multiply(String leftExpr, String rightExpr);
   String divide(String leftExpr, String rightExpr);
   String equals(String leftExpr, String rightExpr);
   String notEquals(String leftExpr, String rightExpr);
   String greaterThan(String leftExpr, String rightExpr);
   String greaterThanEquals(String leftExpr, String rightExpr);
   String lessThan(String leftExpr, String rightExpr);
   String lessThanEquals(String leftExpr, String rightExpr);

   String or(String leftExpr, String rightExpr);
   String and(String leftExpr, String rightExpr);

   String isNull(String expr);
   String isNotNull(String expr);

   String concat(List<String> exprs);
   String regex(String text, String pattern, String flag);
   String lang(String text);

   String cast(String text, int targetDatatype);
}
