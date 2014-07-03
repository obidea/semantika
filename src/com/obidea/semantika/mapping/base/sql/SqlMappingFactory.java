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
package com.obidea.semantika.mapping.base.sql;

import java.net.URI;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import com.obidea.semantika.database.sql.base.ISqlExpression;
import com.obidea.semantika.datatype.DataType;

public class SqlMappingFactory
{
   private static SqlMappingFactory sInstance;

   public static SqlMappingFactory getInstance()
   {
      if (sInstance == null) {
         sInstance = new SqlMappingFactory();
      }
      return sInstance;
   }

   public SqlValue createValueExpression(String value, String datatype)
   {
      return new SqlValue(value, datatype);
   }

   public SqlValue createStringValueExpression(String value)
   {
      return new SqlValue(value, DataType.STRING);
   }

   public SqlValue createNumericValueExpression(double value)
   {
      return new SqlValue(String.valueOf(value), DataType.DOUBLE);
   }

   public SqlValue createNumericValueExpression(long value)
   {
      return new SqlValue(String.valueOf(value), DataType.LONG);
   }

   public SqlValue createDateTimeValueExpression(Date value)
   {
      return new SqlValue(String.valueOf(value), DataType.DATE);
   }

   public SqlValue createDateTimeValueExpression(Time value)
   {
      return new SqlValue(String.valueOf(value), DataType.TIME);
   }

   public SqlValue createDateTimeValueExpression(Timestamp value)
   {
      return new SqlValue(String.valueOf(value), DataType.DATE_TIME);
   }

   public SqlValue createBooleanValueExpression(boolean value)
   {
      return new SqlValue(String.valueOf(value), DataType.BOOLEAN);
   }

   public SqlUriValue createUriValueExpression(URI value)
   {
      return new SqlUriValue(String.valueOf(value));
   }

   public SqlAddition createAdditionExpression(ISqlExpression leftParameter, ISqlExpression rightParameter)
   {
      return new SqlAddition(leftParameter, rightParameter);
   }

   public SqlSubtract createSubstractExpression(ISqlExpression leftParameter, ISqlExpression rightParameter)
   {
      return new SqlSubtract(leftParameter, rightParameter);
   }

   public SqlMultiply createMultiplyExpression(ISqlExpression leftParameter, ISqlExpression rightParameter)
   {
      return new SqlMultiply(leftParameter, rightParameter);
   }

   public SqlDivide createDivideExpression(ISqlExpression leftParameter, ISqlExpression rightParameter)
   {
      return new SqlDivide(leftParameter, rightParameter);
   }

   public SqlEqualsTo createEqualsToExpression(ISqlExpression leftParameter, ISqlExpression rightParameter)
   {
      return new SqlEqualsTo(leftParameter, rightParameter);
   }

   public SqlNotEqualsTo createNotEqualsToExpression(ISqlExpression leftParameter, ISqlExpression rightParameter)
   {
      return new SqlNotEqualsTo(leftParameter, rightParameter);
   }

   public SqlGreaterThan createGreaterThanExpression(ISqlExpression leftParameter, ISqlExpression rightParameter)
   {
      return new SqlGreaterThan(leftParameter, rightParameter);
   }

   public SqlGreaterThanEquals createGreaterThanEqualsExpression(ISqlExpression leftParameter, ISqlExpression rightParameter)
   {
      return new SqlGreaterThanEquals(leftParameter, rightParameter);
   }

   public SqlLessThan createLessThanExpression(ISqlExpression leftParameter, ISqlExpression rightParameter)
   {
      return new SqlLessThan(leftParameter, rightParameter);
   }

   public SqlLessThanEquals createLessThanEqualsExpression(ISqlExpression leftParameter, ISqlExpression rightParameter)
   {
      return new SqlLessThanEquals(leftParameter, rightParameter);
   }

   public SqlOr createOrExpression(ISqlExpression leftExpression, ISqlExpression rightExpression)
   {
      return new SqlOr(leftExpression, rightExpression);
   }

   public SqlAnd createAndExpression(ISqlExpression leftExpression, ISqlExpression rightExpression)
   {
      return new SqlAnd(leftExpression, rightExpression);
   }

   public SqlIsNull createIsNullExpression(ISqlExpression parameter)
   {
      return new SqlIsNull(parameter);
   }

   public SqlIsNotNull createIsNotNullExpression(ISqlExpression parameter)
   {
      return new SqlIsNotNull(parameter);
   }

   public SqlConcat createConcatExpression(ISqlExpression... parameters)
   {
      return new SqlConcat(parameters);
   }

   public SqlConcat createConcatExpression(List<ISqlExpression> parameters)
   {
      return new SqlConcat(parameters);
   }

   public SqlUriConcat createUriConcatExpression(ISqlExpression... parameters)
   {
      return new SqlUriConcat(parameters);
   }

   public SqlUriConcat createUriConcatExpression(List<ISqlExpression> parameters)
   {
      return new SqlUriConcat(parameters);
   }

   public SqlRegex createRegexExpression(ISqlExpression text, ISqlExpression pattern, ISqlExpression flag)
   {
      return new SqlRegex(text, pattern, flag);
   }

   public SqlLang createLangExpression(ISqlExpression text)
   {
      return new SqlLang(text);
   }

   public SqlStr createStrExpression(ISqlExpression parameter)
   {
      return new SqlStr(parameter);
   }
}
