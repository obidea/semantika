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
package com.obidea.semantika.database.sql.dialect;

import java.util.List;

import com.obidea.semantika.database.sql.deparser.Sql99;

public abstract class Sql99Dialect implements IDialect
{
   private String mQuoteString = "\""; //$NON-NLS-1$ // by default
   private String mSeparator = "."; //$NON-NLS-1$ // by default

   public Sql99Dialect()
   {
      // NO-OP
   }

   @Override
   public void setQuoteString(String quoteString)
   {
      mQuoteString = quoteString;
   }

   @Override
   public String getQuoteString()
   {
      return mQuoteString;
   }

   @Override
   public void setSeparator(String catalogSeparator)
   {
      mSeparator = catalogSeparator;
   }

   @Override
   public String getSeparator()
   {
      return mSeparator;
   }

   @Override
   public String identifier(String... nameFragments)
   {
      String identifier = ""; //$NON-NLS-1$
      
      boolean needSeparator = false;
      for (int i = 0; i < nameFragments.length; i++) {
         if (needSeparator) {
            identifier += getSeparator();
         }
         identifier += getQuoteString() + nameFragments[i] + getQuoteString();
         needSeparator = true;
      }
      return identifier;
   }

   @Override
   public String alias(String aliasName)
   {
      return Sql99.AS + " " + getQuoteString() + aliasName + getQuoteString(); //$NON-NLS-1$
   }

   @Override
   public String view(String viewName)
   {
      return getQuoteString() + viewName + getQuoteString();
   }

   @Override
   public String literal(String value)
   {
      return "'" + value + "'"; //$NON-NLS-1$ //$NON-NLS-2$
   }

   @Override
   public String add(String leftExpr, String rightExpr)
   {
      return parenthesis(leftExpr + " " + Sql99.PLUS + " " + rightExpr); //$NON-NLS-1$ //$NON-NLS-1$
   }

   @Override
   public String subtract(String leftExpr, String rightExpr)
   {
      return parenthesis(leftExpr + " " + Sql99.MINUS + " " + rightExpr); //$NON-NLS-1$ //$NON-NLS-1$
   }

   @Override
   public String multiply(String leftExpr, String rightExpr)
   {
      return leftExpr + " " + Sql99.ASTERISK + " " + rightExpr; //$NON-NLS-1$ //$NON-NLS-1$
   }

   @Override
   public String divide(String leftExpr, String rightExpr)
   {
      return leftExpr + " " + Sql99.SOLIDUS + " " + rightExpr; //$NON-NLS-1$ //$NON-NLS-1$
   }

   @Override
   public String equals(String leftExpr, String rightExpr)
   {
      return leftExpr + " " + Sql99.EQ + " " + rightExpr; //$NON-NLS-1$ //$NON-NLS-1$
   }

   @Override
   public String notEquals(String leftExpr, String rightExpr)
   {
      return leftExpr + " " + Sql99.NEQ + " " + rightExpr; //$NON-NLS-1$ //$NON-NLS-1$
   }

   @Override
   public String greaterThan(String leftExpr, String rightExpr)
   {
      return leftExpr + " " + Sql99.GT + " " + rightExpr; //$NON-NLS-1$ //$NON-NLS-1$
   }

   @Override
   public String greaterThanEquals(String leftExpr, String rightExpr)
   {
      return leftExpr + " " + Sql99.GTE + " " + rightExpr; //$NON-NLS-1$ //$NON-NLS-1$
   }

   @Override
   public String lessThan(String leftExpr, String rightExpr)
   {
      return leftExpr + " " + Sql99.LT + " " + rightExpr; //$NON-NLS-1$ //$NON-NLS-1$
   }

   @Override
   public String lessThanEquals(String leftExpr, String rightExpr)
   {
      return leftExpr + " " + Sql99.LTE + " " + rightExpr; //$NON-NLS-1$ //$NON-NLS-1$
   }

   @Override
   public String or(String leftExpr, String rightExpr)
   {
      return parenthesis(leftExpr + " " + Sql99.OR + " " + rightExpr); //$NON-NLS-1$ //$NON-NLS-1$
   }

   @Override
   public String and(String leftExpr, String rightExpr)
   {
      return leftExpr + " " + Sql99.AND + " " + rightExpr; //$NON-NLS-1$ //$NON-NLS-1$
   }

   @Override
   public String isNull(String column)
   {
      return column + " IS NULL"; //$NON-NLS-1$
   }

   @Override
   public String isNotNull(String column)
   {
      return column + " IS NOT NULL"; //$NON-NLS-1$
   }

   @Override
   public String concat(List<String> parameters)
   {
      String concat = ""; //$NON-NLS-1$
      boolean needConcat = false;
      for (String parameter : parameters) {
         if (needConcat) {
            concat += "||"; //$NON-NLS-1$
         }
         concat += parameter;
         needConcat = true;
      }
      return parenthesis(concat);
   }

   @Override
   public String regex(String column, String pattern, String flag)
   {
      return column + " LIKE " + pattern; //$NON-NLS-1$
   }

   private String parenthesis(String expr)
   {
      return Sql99.LPAREN + expr + Sql99.RPAREN;
   }
}
