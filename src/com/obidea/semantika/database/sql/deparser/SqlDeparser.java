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
package com.obidea.semantika.database.sql.deparser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.obidea.semantika.database.sql.base.ISqlBinaryFunction;
import com.obidea.semantika.database.sql.base.ISqlColumn;
import com.obidea.semantika.database.sql.base.ISqlExpression;
import com.obidea.semantika.database.sql.base.ISqlExpressionVisitor;
import com.obidea.semantika.database.sql.base.ISqlFunction;
import com.obidea.semantika.database.sql.base.ISqlJoin;
import com.obidea.semantika.database.sql.base.ISqlQuery;
import com.obidea.semantika.database.sql.base.ISqlSubQuery;
import com.obidea.semantika.database.sql.base.ISqlTable;
import com.obidea.semantika.database.sql.base.ISqlUnaryFunction;
import com.obidea.semantika.database.sql.base.ISqlValue;
import com.obidea.semantika.database.sql.base.SqlJoinCondition;
import com.obidea.semantika.database.sql.base.SqlSelectItem;
import com.obidea.semantika.database.sql.dialect.IDialect;
import com.obidea.semantika.database.sql.parser.SqlException;
import com.obidea.semantika.datatype.DataType;
import com.obidea.semantika.expression.base.QuerySet;
import com.obidea.semantika.mapping.base.sql.SqlAddition;
import com.obidea.semantika.mapping.base.sql.SqlAnd;
import com.obidea.semantika.mapping.base.sql.SqlConcat;
import com.obidea.semantika.mapping.base.sql.SqlDivide;
import com.obidea.semantika.mapping.base.sql.SqlEqualsTo;
import com.obidea.semantika.mapping.base.sql.SqlGreaterThan;
import com.obidea.semantika.mapping.base.sql.SqlGreaterThanEquals;
import com.obidea.semantika.mapping.base.sql.SqlIsNotNull;
import com.obidea.semantika.mapping.base.sql.SqlIsNull;
import com.obidea.semantika.mapping.base.sql.SqlLang;
import com.obidea.semantika.mapping.base.sql.SqlLessThan;
import com.obidea.semantika.mapping.base.sql.SqlLessThanEquals;
import com.obidea.semantika.mapping.base.sql.SqlMultiply;
import com.obidea.semantika.mapping.base.sql.SqlNotEqualsTo;
import com.obidea.semantika.mapping.base.sql.SqlOr;
import com.obidea.semantika.mapping.base.sql.SqlRegex;
import com.obidea.semantika.mapping.base.sql.SqlSubtract;
import com.obidea.semantika.mapping.base.sql.SqlUriConcat;
import com.obidea.semantika.mapping.base.sql.SqlUserQuery;

public class SqlDeparser extends TextFormatter implements ISqlDeparser, ISqlExpressionVisitor
{
   private IDialect mDialect;

   private String mExpressionString = ""; //$NON-NLS-1$

   public SqlDeparser(IDialect dialect)
   {
      mDialect = dialect;
   }

   private SqlDeparser(SqlDeparser parent)
   {
      mDialect = parent.mDialect;
      mTabCounter = parent.mTabCounter;
   }

   @Override
   public String deparse(QuerySet<? extends ISqlQuery> querySet)
   {
      StringBuilder unions = new StringBuilder();
      boolean needUnion = false;
      for (ISqlQuery query : querySet.getAll()) {
         if (needUnion) {
            unions.append("\n");
            unions.append(Sql99.UNION);
            unions.append("\n");
         }
         unions.append(deparse(query));
         needUnion = true;
      }
      return unions.toString();
   }

   @Override
   public String deparse(ISqlQuery query)
   {
      initStringBuilder();
      visitSelect(query.getSelectItems(), query.isDistinct());
      visitFrom(query.getFromExpression());
      if (query.hasWhereExpression()) {
         visitWhere(query.getWhereExpression());
      }
      return flushStringBuilder();
   }

   private void visitSelect(List<SqlSelectItem> selectItemList, boolean isDistinct)
   {
      append(Sql99.SELECT);
      space();
      if (isDistinct) {
         append(Sql99.DISTINCT);
         space();
      }
      boolean needComma = false;
      boolean needShift = true;
      for (SqlSelectItem selectItem : selectItemList) {
         if (needComma) {
            append(","); //$NON-NLS-1$
            newline();
            if (needShift) {
               shiftRight();
            }
            needShift = false;
         }
         append(str(selectItem.getExpression()));
         if (selectItem.hasAliasName()) {
            space();
            append(mDialect.alias(selectItem.getAliasName()));
         }
         needComma = true;
      }
      newline();
      shiftLeft();
   }

   private void visitFrom(ISqlExpression fromExpression)
   {
      append(Sql99.FROM);
      space();
      fromExpression.accept(this);
      newline();
   }

   private void visitWhere(Set<ISqlExpression> whereExpressions)
   {
      append(Sql99.WHERE);
      space();
      boolean needAnd = false;
      boolean needShift = true;
      for (ISqlExpression whereExpression : whereExpressions) {
         if (needAnd) {
            space();
            append(Sql99.AND);
            newline();
            if (needShift) {
               shiftRight();
            }
            needShift = false;
         }
         append(str(whereExpression));
         needAnd = true;
      }
      newline();
      shiftLeft();
   }

   private String str(ISqlExpression expression)
   {
      expression.accept(this);
      return mExpressionString;
   }

   @Override
   public void visit(ISqlTable table)
   {
      append(mDialect.identifier(table.getNameFragments()));
      if (table.hasAliasName()) {
         space();
         append(mDialect.alias(table.getAliasName()));
      }
   }

   @Override
   public void visit(ISqlColumn column)
   {
      mExpressionString = mDialect.identifier(column.getNameFragments());
   }

   @Override
   public void visit(ISqlFunction function)
   {
      if (function instanceof ISqlUnaryFunction) {
         visitSqlUnaryFunctionExpression((ISqlUnaryFunction) function);
      }
      else if (function instanceof ISqlBinaryFunction) {
         visitSqlBinaryFunctionExpression((ISqlBinaryFunction) function);
      }
      else {
         visitSqlNaryFunctionExpression(function);
      }
   }

   private void visitSqlUnaryFunctionExpression(ISqlUnaryFunction unaryFunction)
   {
      String argument = str(unaryFunction.getParameterExpression());
      if (unaryFunction instanceof SqlIsNull) {
         mExpressionString = mDialect.isNull(argument);
      }
      else if (unaryFunction instanceof SqlIsNotNull) {
         mExpressionString =  mDialect.isNotNull(argument);
      }
      else if (unaryFunction instanceof SqlLang) {
         mExpressionString =  mDialect.lang(argument);
      }
      else {
         throw unknownSqlExpressionException(unaryFunction);
      }
   }

   private void visitSqlBinaryFunctionExpression(ISqlBinaryFunction binaryFunction)
   {
      String leftArgument = str(binaryFunction.getLeftParameterExpression());
      String rightArgument = str(binaryFunction.getRightParameterExpression());
      if (binaryFunction instanceof SqlAddition) {
         mExpressionString = mDialect.add(leftArgument, rightArgument);
      }
      else if (binaryFunction instanceof SqlSubtract) {
         mExpressionString = mDialect.subtract(leftArgument, rightArgument);
      }
      else if (binaryFunction instanceof SqlMultiply) {
         mExpressionString = mDialect.multiply(leftArgument, rightArgument);
      }
      else if (binaryFunction instanceof SqlDivide) {
         mExpressionString = mDialect.divide(leftArgument, rightArgument);
      }
      else if (binaryFunction instanceof SqlEqualsTo) {
         mExpressionString = mDialect.equals(leftArgument, rightArgument);
      }
      else if (binaryFunction instanceof SqlNotEqualsTo) {
         mExpressionString = mDialect.notEquals(leftArgument, rightArgument);
      }
      else if (binaryFunction instanceof SqlGreaterThan) {
         mExpressionString = mDialect.greaterThan(leftArgument, rightArgument);
      }
      else if (binaryFunction instanceof SqlGreaterThanEquals) {
         mExpressionString = mDialect.greaterThanEquals(leftArgument, rightArgument);
      }
      else if (binaryFunction instanceof SqlLessThan) {
         mExpressionString = mDialect.lessThan(leftArgument, rightArgument);
      }
      else if (binaryFunction instanceof SqlLessThanEquals) {
         mExpressionString = mDialect.lessThanEquals(leftArgument, rightArgument);
      }
      else if (binaryFunction instanceof SqlAnd) {
         mExpressionString = mDialect.and(leftArgument, rightArgument);
      }
      else if (binaryFunction instanceof SqlOr) {
         mExpressionString = mDialect.or(leftArgument, rightArgument);
      }
      else {
         throw unknownSqlExpressionException(binaryFunction);
      }
   }

   private void visitSqlNaryFunctionExpression(ISqlFunction naryFunction)
   {
      if (naryFunction instanceof SqlConcat) {
         visitSqlConcat((SqlConcat) naryFunction);
      }
      else if (naryFunction instanceof SqlUriConcat) {
         visitSqlUriConcat((SqlUriConcat) naryFunction);
      }
      else if (naryFunction instanceof SqlRegex) {
         visitSqlRegex((SqlRegex) naryFunction);
      }
      else {
         throw unknownSqlExpressionException(naryFunction);
      }
   }

   private void visitSqlConcat(SqlConcat sqlConcat)
   {
      List<String> arguments = new ArrayList<String>();
      for (ISqlExpression expression : sqlConcat.getParameterExpressions()) {
         arguments.add(str(expression));
      }
      mExpressionString = mDialect.concat(arguments);
   }

   private void visitSqlUriConcat(SqlUriConcat sqlUriConcat)
   {
      List<String> arguments = new ArrayList<String>();
      
      Iterator<ISqlExpression> iter = sqlUriConcat.getParameterExpressions().iterator();
      ISqlValue stringTemplateValue = (ISqlValue) iter.next(); // must be a value
      arguments.add(mDialect.literal(stringTemplateValue.getValue()));
      arguments.add("' : '"); //$NON-NLS-1$
      arguments.add("'\"'"); //$NON-NLS-1$
      boolean needSeparator = false;
      while (iter.hasNext()) {
         if (needSeparator) {
            arguments.add("'\" \"'"); //$NON-NLS-1$
         }
         ISqlExpression expression = iter.next();
         arguments.add(str(expression));
         needSeparator = true;
      }
      arguments.add("'\"'"); //$NON-NLS-1$
      mExpressionString = mDialect.concat(arguments);
   }

   private void visitSqlRegex(SqlRegex sqlRegex)
   {
      String[] arguments = new String[] { "", "", "" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      
      int i = 0;
      for (ISqlExpression expression : sqlRegex.getParameterExpressions()) {
         arguments[i++] = str(expression);
      }
      mExpressionString = mDialect.regex(arguments[0], arguments[1], arguments[2]);
   }

   @Override
   public void visit(ISqlValue value)
   {
      String lexicalValue = value.getValue();
      String datatype = value.getDatatype();
      if (DataType.NUMERIC_TYPES.contains(datatype)) {
         mExpressionString = lexicalValue;
      }
      else {
         /*
          * Any sequence of characters delimited by single quotes. If the single
          * quote character is included in the sequence it must be written twice.
          */
         lexicalValue = lexicalValue.replaceAll("'", "''"); //$NON-NLS-1$ //$NON-NLS-2%
         mExpressionString = mDialect.literal(lexicalValue);
      }
   }

   @Override
   public void visit(ISqlJoin joinExpression)
   {
      /*
       * Join left expression
       */
      joinExpression.getLeftExpression().accept(this);
      newline();
      
      /*
       * Print the JOIN type keyword
       */
      if (joinExpression.isInnerJoin()) {
         append(Sql99.INNER_JOIN);
      }
      else if (joinExpression.isLeftJoin()) {
         append(Sql99.LEFT_JOIN);
      }
      space();
      
      /*
       * Join right expression. If the right expression is a join expression then
       * it will be printed as an inner join expression.
       */
      if (hasInnerJoin(joinExpression)) {
         append("("); //$NON-NLS-1$
         newline();
         shiftRight(); // tab
         joinExpression.getRightExpression().accept(this);
         newline();
         shiftLeft(); // restore tab
         append(")"); //$NON-NLS-1$
      }
      else {
         joinExpression.getRightExpression().accept(this);
      }
      
      /*
       * Print the join conditions. 
       */
      space();
      append(Sql99.ON);
      space();
      
      boolean hasOnExpression = false;
      boolean needAnd = false;
      if (joinExpression.hasJoinConditions()) {
         hasOnExpression = true;
         for (SqlJoinCondition joinCondition : joinExpression.getJoinConditions()) {
            if (needAnd) {
               space();
               append(Sql99.AND);
               space();
            }
            append(str(joinCondition.getLeftColumn()));
            space();
            append(Sql99.EQ);
            space();
            append(str(joinCondition.getRightColumn()));
            needAnd = true;
         }
      }
      if (joinExpression.hasFilters()) {
         hasOnExpression = true;
         for (ISqlExpression filter : joinExpression.getFilters()) {
            if (needAnd) {
               space();
               append(Sql99.AND);
               space();
            }
            append(str(filter));
            needAnd = true;
         }
      }
      
      if (!hasOnExpression) {
         append(Sql99.TRUE);
      }
   }

   private boolean hasInnerJoin(ISqlJoin expression)
   {
      boolean toReturn = false;
      if (expression.getRightExpression() instanceof ISqlJoin) {
         toReturn = true;
      }
      return toReturn;
   }

   @Override
   public void visit(ISqlSubQuery subQueryExpression)
   {
      append(Sql99.LPAREN);
      newline();
      shiftRight(); // tab
      if (subQueryExpression instanceof SqlUserQuery) {
         SqlUserQuery userQuery = (SqlUserQuery) subQueryExpression;
         append(userQuery.getSqlString());
      }
      else {
         SqlDeparser innerDeparser = new SqlDeparser(this);
         append(innerDeparser.deparse(subQueryExpression.getQuery()));
      }
      newline();
      shiftLeft(); // restore tab
      append(Sql99.RPAREN);
      space();
      append(Sql99.AS);
      space();
      append(mDialect.view(subQueryExpression.getViewName()));
   }

   private SqlException unknownSqlExpressionException(ISqlFunction sqlFunction)
   {
      return new SqlException("Unable to produce SQL string from expression: " + sqlFunction); //$NON-NLS-1$
   }
}
