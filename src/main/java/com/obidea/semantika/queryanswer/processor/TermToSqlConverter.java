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
package com.obidea.semantika.queryanswer.processor;

import java.util.ArrayList;
import java.util.List;

import com.obidea.semantika.database.sql.base.ISqlExpression;
import com.obidea.semantika.exception.SemantikaRuntimeException;
import com.obidea.semantika.expression.base.ExpressionConstant;
import com.obidea.semantika.expression.base.IFunction;
import com.obidea.semantika.expression.base.ILiteral;
import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.expression.base.IUriReference;
import com.obidea.semantika.expression.base.IVariable;
import com.obidea.semantika.expression.base.TermVisitorAdapter;
import com.obidea.semantika.knowledgebase.TermSubstitutionBinding;
import com.obidea.semantika.knowledgebase.UnificationException;
import com.obidea.semantika.knowledgebase.Unifier;
import com.obidea.semantika.mapping.IUriTemplate;
import com.obidea.semantika.mapping.base.sql.SqlAddition;
import com.obidea.semantika.mapping.base.sql.SqlAnd;
import com.obidea.semantika.mapping.base.sql.SqlColumn;
import com.obidea.semantika.mapping.base.sql.SqlDivide;
import com.obidea.semantika.mapping.base.sql.SqlEqualsTo;
import com.obidea.semantika.mapping.base.sql.SqlFunction;
import com.obidea.semantika.mapping.base.sql.SqlGreaterThan;
import com.obidea.semantika.mapping.base.sql.SqlGreaterThanEquals;
import com.obidea.semantika.mapping.base.sql.SqlIsNotNull;
import com.obidea.semantika.mapping.base.sql.SqlIsNull;
import com.obidea.semantika.mapping.base.sql.SqlLessThan;
import com.obidea.semantika.mapping.base.sql.SqlLessThanEquals;
import com.obidea.semantika.mapping.base.sql.SqlMappingFactory;
import com.obidea.semantika.mapping.base.sql.SqlMultiply;
import com.obidea.semantika.mapping.base.sql.SqlNotEqualsTo;
import com.obidea.semantika.mapping.base.sql.SqlOr;
import com.obidea.semantika.mapping.base.sql.SqlSubtract;
import com.obidea.semantika.mapping.base.sql.SqlUriConcat;
import com.obidea.semantika.mapping.base.sql.SqlUriValue;
import com.obidea.semantika.mapping.base.sql.SqlValue;
import com.obidea.semantika.util.Serializer;

public class TermToSqlConverter extends TermVisitorAdapter // XXX: Fix this, maybe SqlMappingVisitorAdapter
{
   private ISqlExpression mReturnExpression = null;

   private static SqlMappingFactory sSqlFactory = SqlMappingFactory.getInstance();

   private void reset()
   {
      mReturnExpression = null;
   }

   public ISqlExpression toSqlExpression(ITerm term)
   {
      reset();
      term.accept(this);
      if (mReturnExpression == null) {
         throw new SemantikaRuntimeException("Unable to convert term (class: " + term.getClass().toString() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
      }
      return mReturnExpression;
   }

   public SqlValue toSqlValue(ILiteral literal)
   {
      return sSqlFactory.createValueExpression(literal.getLexicalValue(), literal.getDatatype());
   }

   public SqlUriValue toSqlUriValue(IUriReference uriReference)
   {
      return sSqlFactory.createUriValueExpression(uriReference.toUri());
   }

   public SqlAnd toSqlAnd(IFunction function)
   {
      ISqlExpression e1 = getExpression(function.getParameter(0));
      ISqlExpression e2 = getExpression(function.getParameter(1));
      return sSqlFactory.createAndExpression(e1, e2);
   }

   public SqlOr toSqlOr(IFunction function)
   {
      ISqlExpression e1 = getExpression(function.getParameter(0));
      ISqlExpression e2 = getExpression(function.getParameter(1));
      return sSqlFactory.createOrExpression(e1, e2);
   }

   public SqlEqualsTo toSqlEqualsTo(IFunction function)
   {
      ISqlExpression e1 = getExpression(function.getParameter(0));
      ISqlExpression e2 = getExpression(function.getParameter(1));
      return sSqlFactory.createEqualsToExpression(e1, e2);
   }

   public SqlNotEqualsTo toSqlNotEqualsTo(IFunction function)
   {
      ISqlExpression e1 = getExpression(function.getParameter(0));
      ISqlExpression e2 = getExpression(function.getParameter(1));
      return sSqlFactory.createNotEqualsToExpression(e1, e2);
   }

   public SqlGreaterThan toSqlGreaterThan(IFunction function)
   {
      ISqlExpression e1 = getExpression(function.getParameter(0));
      ISqlExpression e2 = getExpression(function.getParameter(1));
      return sSqlFactory.createGreaterThanExpression(e1, e2);
   }

   public SqlGreaterThanEquals toSqlGreaterThanEqual(IFunction function)
   {
      ISqlExpression e1 = getExpression(function.getParameter(0));
      ISqlExpression e2 = getExpression(function.getParameter(1));
      return sSqlFactory.createGreaterThanEqualsExpression(e1, e2);
   }

   public SqlLessThan toSqlLessThan(IFunction function)
   {
      ISqlExpression e1 = getExpression(function.getParameter(0));
      ISqlExpression e2 = getExpression(function.getParameter(1));
      return sSqlFactory.createLessThanExpression(e1, e2);
   }

   public SqlLessThanEquals toSqlLessThanEqual(IFunction function)
   {
      ISqlExpression e1 = getExpression(function.getParameter(0));
      ISqlExpression e2 = getExpression(function.getParameter(1));
      return sSqlFactory.createLessThanEqualsExpression(e1, e2);
   }

   public SqlAddition toSqlAddition(IFunction function)
   {
      ISqlExpression e1 = getExpression(function.getParameter(0));
      ISqlExpression e2 = getExpression(function.getParameter(1));
      return sSqlFactory.createAdditionExpression(e1, e2);
   }

   public SqlSubtract toSqlSubtract(IFunction function)
   {
      ISqlExpression e1 = getExpression(function.getParameter(0));
      ISqlExpression e2 = getExpression(function.getParameter(1));
      return sSqlFactory.createSubstractExpression(e1, e2);
   }

   public SqlMultiply toSqlMultiply(IFunction function)
   {
      ISqlExpression e1 = getExpression(function.getParameter(0));
      ISqlExpression e2 = getExpression(function.getParameter(1));
      return sSqlFactory.createMultiplyExpression(e1, e2);
   }

   public SqlDivide toSqlDivide(IFunction function)
   {
      ISqlExpression e1 = getExpression(function.getParameter(0));
      ISqlExpression e2 = getExpression(function.getParameter(1));
      return sSqlFactory.createDivideExpression(e1, e2);
   }

   public SqlIsNull toSqlIsNull(IFunction function)
   {
      ISqlExpression e = getExpression(function.getParameter(0));
      return sSqlFactory.createIsNullExpression(e);
   }

   public SqlIsNotNull toSqlIsNotNull(IFunction function)
   {
      ISqlExpression e = getExpression(function.getParameter(0));
      return sSqlFactory.createIsNotNullExpression(e);
   }

   public SqlUriConcat toSqlUriConcat(IUriTemplate uriTemplate)
   {
      String templateString = uriTemplate.getTemplateString();
      SqlValue templateValue = sSqlFactory.createStringValueExpression(templateString);
      
      List<ISqlExpression> parameters = new ArrayList<ISqlExpression>();
      parameters.add(templateValue);
      for (ITerm templateParameter : uriTemplate.getParameters()) {
         parameters.add(getExpression(templateParameter));
      }
      return sSqlFactory.createUriConcatExpression(parameters);
   }

   private ISqlExpression toSqlRegex(IFunction function)
   {
      ISqlExpression e1 = getExpression(function.getParameter(0));
      ISqlExpression e2 = getExpression(function.getParameter(1));
      ISqlExpression e3 = getExpression(function.getParameter(2));
      return sSqlFactory.createRegexExpression(e1, e2, e3);
   }

   private ISqlExpression toSqlLang(IFunction function)
   {
      ISqlExpression e = getExpression(function.getParameter(0));
      return sSqlFactory.createLangExpression(e);
   }

   private ISqlExpression toSqlStr(IFunction function)
   {
      ISqlExpression e = getExpression(function.getParameter(0));
      return sSqlFactory.createStrExpression(e);
   }

   /*
    * Implementation of visitor methods.
    */

   @Override
   public void visit(IVariable variable)
   {
      mReturnExpression = (variable instanceof SqlColumn)
            ? (SqlColumn) variable
            : null;
   }

   @Override
   public void visit(ILiteral literal)
   {
      mReturnExpression = (literal instanceof SqlValue)
            ? (SqlValue) literal
            : toSqlValue(literal);
   }

   @Override
   public void visit(IUriReference uriReference)
   {
      mReturnExpression = toSqlUriValue(uriReference);
   }

   @Override
   public void visit(IFunction function)
   {
      mReturnExpression = (function instanceof SqlFunction)
            ? (SqlFunction) function
            : visitFunction(function);
   }

   private ISqlExpression visitFunction(IFunction function)
   {
      return (function instanceof IUriTemplate)
            ? visitUriTemplateFunction((IUriTemplate) function)
            : visitBuildInFunction(function);
   }

   private ISqlExpression visitUriTemplateFunction(IUriTemplate uriTemplate)
   {
      return toSqlUriConcat(uriTemplate);
   }

   private ISqlExpression visitBuildInFunction(IFunction function)
   {
      String functionName = function.getName();
      if (functionName.equals(ExpressionConstant.AND)) {
         return toSqlAnd(function);
      }
      else if (functionName.equals(ExpressionConstant.OR)) {
         return toSqlOr(function);
      }
      else if (functionName.equals(ExpressionConstant.EQUAL)) {
         ITerm t1 = function.getParameter(0);
         ITerm t2 = function.getParameter(1);
         if (t1 instanceof IUriTemplate && t2 instanceof IUriReference) {
            mReturnExpression = getExtendedEqualExpression(t1, t2);
         }
         else if (t2 instanceof IUriTemplate && t1 instanceof IUriReference) {
            mReturnExpression = getExtendedEqualExpression(t2, t1);
         }
         else if (t1 instanceof IUriTemplate && t2 instanceof IUriTemplate) {
            mReturnExpression = getExtendedEqualExpression(t2, t1);
         }
         else {
            return toSqlEqualsTo(function);
         }
      }
      else if (functionName.equals(ExpressionConstant.NOT_EQUAL)) {
         ITerm t1 = function.getParameter(0);
         ITerm t2 = function.getParameter(1);
         if (t1 instanceof IUriTemplate && t2 instanceof IUriReference) {
            mReturnExpression = getExtendedNotEqualExpression(t1, t2);
         }
         else if (t2 instanceof IUriTemplate && t1 instanceof IUriReference) {
            mReturnExpression = getExtendedNotEqualExpression(t1, t2);
         }
         else if (t1 instanceof IUriTemplate && t2 instanceof IUriTemplate) {
            mReturnExpression = getExtendedNotEqualExpression(t1, t2);
         }
         else {
            mReturnExpression = toSqlNotEqualsTo(function);
         }
      }
      else if (functionName.equals(ExpressionConstant.GREATER_THAN)) {
         mReturnExpression = toSqlGreaterThan(function);
      }
      else if (functionName.equals(ExpressionConstant.GREATER_THAN_EQUAL)) {
         mReturnExpression = toSqlGreaterThanEqual(function);
      }
      else if (functionName.equals(ExpressionConstant.LESS_THAN)) {
         mReturnExpression = toSqlLessThan(function);
      }
      else if (functionName.equals(ExpressionConstant.LESS_THEN_EQUAL)) {
         mReturnExpression = toSqlLessThanEqual(function);
      }
      else if (functionName.equals(ExpressionConstant.ADD)) {
         mReturnExpression = toSqlAddition(function);
      }
      else if (functionName.equals(ExpressionConstant.SUBTRACT)) {
         mReturnExpression = toSqlSubtract(function);
      }
      else if (functionName.equals(ExpressionConstant.MULTIPLY)) {
         mReturnExpression = toSqlMultiply(function);
      }
      else if (functionName.equals(ExpressionConstant.DIVIDE)) {
         mReturnExpression = toSqlDivide(function);
      }
      else if (functionName.equals(ExpressionConstant.IS_NULL)) {
         mReturnExpression = toSqlIsNull(function);
      }
      else if (functionName.equals(ExpressionConstant.IS_NOT_NULL)) {
         mReturnExpression = toSqlIsNotNull(function);
      }
      else if (functionName.equals(ExpressionConstant.REGEX)) {
         mReturnExpression = toSqlRegex(function);
      }
      else if (functionName.equals(ExpressionConstant.LANG)) {
         mReturnExpression = toSqlLang(function);
      }
      else if (functionName.equals(ExpressionConstant.STR)) {
         mReturnExpression = toSqlStr(function);
      }
      else {
         throw new SemantikaRuntimeException("Unknown function name: " + functionName); //$NON-NLS-1$
      }
      return mReturnExpression;
   }

   /*
    * Other private utility methods
    */

   private ISqlExpression getExtendedEqualExpression(ITerm t1, ITerm t2)
   {
      ISqlExpression toReturn = null;
      try {
         TermSubstitutionBinding binding = Unifier.findSubstitution(t1, t2);
         for (IVariable variable : binding.getVariables()) {
            ISqlExpression e1 = getExpression(variable);
            ISqlExpression e2 = getExpression(binding.getTerm(variable));
            if (toReturn == null) {
               toReturn = sSqlFactory.createEqualsToExpression(e1, e2);
            }
            else {
               toReturn = sSqlFactory.createAndExpression(toReturn, sSqlFactory.createEqualsToExpression(e1, e2));
            }
         }
         return toReturn;
      }
      catch (UnificationException e) {
         throw new SemantikaRuntimeException("Unexpected exception when processing UriTemplate filters", e); //$NON-NLS-1$
      }
   }

   private ISqlExpression getExtendedNotEqualExpression(ITerm t1, ITerm t2)
   {
      ISqlExpression toReturn = null;
      try {
         TermSubstitutionBinding binding = Unifier.findSubstitution(t1, t2);
         for (IVariable variable : binding.getVariables()) {
            ISqlExpression e1 = getExpression(variable);
            ISqlExpression e2 = getExpression(binding.getTerm(variable));
            if (toReturn == null) {
               toReturn = sSqlFactory.createNotEqualsToExpression(e1, e2);
            }
            else {
               toReturn = sSqlFactory.createAndExpression(toReturn, sSqlFactory.createNotEqualsToExpression(e1, e2));
            }
         }
         return toReturn;
      }
      catch (UnificationException e) {
         throw new SemantikaRuntimeException("Unexpected exception when processing UriTemplate filters", e); //$NON-NLS-1$
      }
   }

   private ISqlExpression getExpression(ITerm term)
   {
      term.accept(this);
      return copy(mReturnExpression);
   }

   private static ISqlExpression copy(ISqlExpression expression)
   {
      return (ISqlExpression) Serializer.copy(expression);
   }
}
