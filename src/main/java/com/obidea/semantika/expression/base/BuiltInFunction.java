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
package com.obidea.semantika.expression.base;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.obidea.semantika.datatype.DataType;
import com.obidea.semantika.exception.SemantikaRuntimeException;
import com.obidea.semantika.expression.ExpressionObjectFactory;

public enum BuiltInFunction
{
   /*
    * Boolean operators
    */

   And(ExpressionConstant.AND, 2, new IFunctionOperation() {
      private static final long serialVersionUID = 629451L;
      @Override
      public ILiteral execute(List<? extends IConstant> args)
      {
         String value1 = args.get(0).getLexicalValue();
         String value2 = args.get(1).getLexicalValue();
         boolean result = Boolean.parseBoolean(value1) && Boolean.parseBoolean(value2);
         return sExpressionFactory.getLiteral(result);
      }
      @Override
      public String getReturnType()
      {
         return DataType.BOOLEAN;
      }
   }),

   Or(ExpressionConstant.OR, 2, new IFunctionOperation() {
      private static final long serialVersionUID = 629451L;
      @Override
      public ILiteral execute(List<? extends IConstant> args)
      {
         String value1 = args.get(0).getLexicalValue();
         String value2 = args.get(1).getLexicalValue();
         boolean result = Boolean.parseBoolean(value1) || Boolean.parseBoolean(value2);
         return sExpressionFactory.getLiteral(result);
      }
      @Override
      public String getReturnType()
      {
         return DataType.BOOLEAN;
      }
   }),

   Not(ExpressionConstant.NOT, 1, new IFunctionOperation() {
      private static final long serialVersionUID = 629451L;
      @Override
      public ILiteral execute(List<? extends IConstant> args)
      {
         String value1 = args.get(0).getLexicalValue();
         boolean result = !Boolean.parseBoolean(value1);
         return sExpressionFactory.getLiteral(result);
      }
      @Override
      public String getReturnType()
      {
         return DataType.BOOLEAN;
      }
   }),

   /*
    * Arithmetic operators
    */

   Add(ExpressionConstant.ADD, 2, new IFunctionOperation() {
      private static final long serialVersionUID = 629451L;
      @Override
      public ILiteral execute(List<? extends IConstant> args)
      {
         String value1 = args.get(0).getLexicalValue();
         String value2 = args.get(1).getLexicalValue();
         BigDecimal result = new BigDecimal(value1).add(new BigDecimal(value2));
         return sExpressionFactory.getLiteral(result);
      }
      @Override
      public String getReturnType()
      {
         return DataType.DECIMAL;
      }
   }),

   Subtract(ExpressionConstant.SUBTRACT, 2, new IFunctionOperation() {
      private static final long serialVersionUID = 629451L;
      @Override
      public ILiteral execute(List<? extends IConstant> args)
      {
         String value1 = args.get(0).getLexicalValue();
         String value2 = args.get(1).getLexicalValue();
         BigDecimal result = new BigDecimal(value1).subtract(new BigDecimal(value2));
         return sExpressionFactory.getLiteral(result);
      }
      @Override
      public String getReturnType()
      {
         return DataType.DECIMAL;
      }
   }),

   Multiply(ExpressionConstant.MULTIPLY, 2, new IFunctionOperation() {
      private static final long serialVersionUID = 629451L;
      @Override
      public ILiteral execute(List<? extends IConstant> args)
      {
         String value1 = args.get(0).getLexicalValue();
         String value2 = args.get(1).getLexicalValue();
         BigDecimal result = new BigDecimal(value1).multiply(new BigDecimal(value2));
         return sExpressionFactory.getLiteral(result);
      }
      @Override
      public String getReturnType()
      {
         return DataType.DECIMAL;
      }
   }),

   Divide(ExpressionConstant.DIVIDE, 2, new IFunctionOperation() {
      private static final long serialVersionUID = 629451L;
      @Override
      public ILiteral execute(List<? extends IConstant> args)
      {
         String value1 = args.get(0).getLexicalValue();
         String value2 = args.get(1).getLexicalValue();
         BigDecimal result = new BigDecimal(value1).divide(new BigDecimal(value2));
         return sExpressionFactory.getLiteral(result);
      }
      @Override
      public String getReturnType()
      {
         return DataType.DECIMAL;
      }
   }),

   /*
    * Object comparison operators
    */

   Equal(ExpressionConstant.EQUAL, 2, new IFunctionOperation() {
      private static final long serialVersionUID = 629451L;
      @Override
      public ILiteral execute(List<? extends IConstant> args)
      {
         String value1 = args.get(0).getLexicalValue();
         String value2 = args.get(1).getLexicalValue();
         boolean result = value1.equals(value2);
         return sExpressionFactory.getLiteral(result);
      }
      @Override
      public String getReturnType()
      {
         return DataType.BOOLEAN;
      }
   }), 

   NotEqual(ExpressionConstant.NOT_EQUAL, 2, new IFunctionOperation() {
      private static final long serialVersionUID = 629451L;
      @Override
      public ILiteral execute(List<? extends IConstant> args)
      {
         String value1 = args.get(0).getLexicalValue();
         String value2 = args.get(1).getLexicalValue();
         boolean result = !value1.equals(value2);
         return sExpressionFactory.getLiteral(result);
     }
      @Override
      public String getReturnType()
      {
         return DataType.BOOLEAN;
      }
   }), 

   /*
    * Number comparison operators
    */

   GreaterThan(ExpressionConstant.GREATER_THAN, 2, new IFunctionOperation() {
      private static final long serialVersionUID = 629451L;
      @Override
      public ILiteral execute(List<? extends IConstant> args)
      {
         String value1 = args.get(0).getLexicalValue();
         String value2 = args.get(1).getLexicalValue();
         int val = new BigDecimal(value1).compareTo(new BigDecimal(value2));
         boolean result = (val == 1 ? new Boolean(true) : new Boolean(false));
         return sExpressionFactory.getLiteral(result);
      }
      @Override
      public String getReturnType()
      {
         return DataType.BOOLEAN;
      }
   }),

   GreaterThanEqual(ExpressionConstant.GREATER_THAN_EQUAL, 2, new IFunctionOperation() {
      private static final long serialVersionUID = 629451L;
      @Override
      public ILiteral execute(List<? extends IConstant> args)
      {
         String value1 = args.get(0).getLexicalValue();
         String value2 = args.get(1).getLexicalValue();
         int val = new BigDecimal(value1).compareTo(new BigDecimal(value2));
         boolean result = (val == 1 || val == 0 ? new Boolean(true) : new Boolean(false));
         return sExpressionFactory.getLiteral(result);
     }
      @Override
      public String getReturnType()
      {
         return DataType.BOOLEAN;
      }
   }),

   LessThan(ExpressionConstant.LESS_THAN, 2, new IFunctionOperation() {
      private static final long serialVersionUID = 629451L;
      @Override
      public ILiteral execute(List<? extends IConstant> args)
      {
         String value1 = args.get(0).getLexicalValue();
         String value2 = args.get(1).getLexicalValue();
         int val = new BigDecimal(value1).compareTo(new BigDecimal(value2));
         boolean result = (val == -1 ? new Boolean(true) : new Boolean(false));
         return sExpressionFactory.getLiteral(result);
      }
      @Override
      public String getReturnType()
      {
         return DataType.BOOLEAN;
      }
   }),

   LessThanEqual(ExpressionConstant.LESS_THEN_EQUAL, 2, new IFunctionOperation() {
      private static final long serialVersionUID = 629451L;
      @Override
      public ILiteral execute(List<? extends IConstant> args)
      {
         String value1 = args.get(0).getLexicalValue();
         String value2 = args.get(1).getLexicalValue();
         int val = new BigDecimal(value1).compareTo(new BigDecimal(value2));
         boolean result = (val == -1 || val == 0 ? new Boolean(true) : new Boolean(false));
         return sExpressionFactory.getLiteral(result);
      }
      @Override
      public String getReturnType()
      {
         return DataType.BOOLEAN;
      }
   }),

   /*
    * Null comparison operators
    */

   IsNull(ExpressionConstant.IS_NULL, 1, new IFunctionOperation() {
      private static final long serialVersionUID = 629451L;
      @Override
      public ILiteral execute(List<? extends IConstant> args)
      {
         IConstant c = args.get(0);
         boolean result = c.equals(new NullValue());
         return sExpressionFactory.getLiteral(result);
      }
      @Override
      public String getReturnType()
      {
         return DataType.BOOLEAN;
      }
   }),

   IsNotNull(ExpressionConstant.IS_NOT_NULL, 1, new IFunctionOperation() {
      private static final long serialVersionUID = 629451L;
      @Override
      public ILiteral execute(List<? extends IConstant> args)
      {
         IConstant c = args.get(0);
         boolean result = !c.equals(new NullValue());
         return sExpressionFactory.getLiteral(result);
      }
      @Override
      public String getReturnType()
      {
         return DataType.BOOLEAN;
      }
   }),

   /*
    * String manipulation operators
    */

   Concat(ExpressionConstant.CONCAT, -1, new IFunctionOperation() { // -1 = N-ary
      private static final long serialVersionUID = 629451L;
      @Override
      public ILiteral execute(List<? extends IConstant> args)
      {
         StringBuilder sb = new StringBuilder();
         sb.append(args.get(0).getLexicalValue());
         sb.append(args.get(1).getLexicalValue());
         String result = sb.toString();
         return sExpressionFactory.getLiteral(result);
      }
      @Override
      public String getReturnType()
      {
         return DataType.STRING;
      }
   }),

   Regex(ExpressionConstant.REGEX, 3, new IFunctionOperation() {
      private static final long serialVersionUID = 629451L;
      @Override
      public ILiteral execute(List<? extends IConstant> args)
      {
         String text = args.get(0).getLexicalValue();
         String pattern = args.get(1).getLexicalValue();
         int flag = flag(args.get(2).getLexicalValue());
         
         Pattern p = Pattern.compile(pattern, flag);
         Matcher m = p.matcher(text);
         boolean result = m.find();
         return sExpressionFactory.getLiteral(result);
      }
      @Override
      public String getReturnType()
      {
         return DataType.BOOLEAN;
      }
      
      private int flag(String flagCode)
      {
         // Reference: http://www.w3.org/TR/xpath-functions/#regex-syntax
         if (flagCode.equals("s")) { //$NON-NLS-1$
            return Pattern.DOTALL;
         }
         else if (flagCode.equalsIgnoreCase("m")) { //$NON-NLS-1$
            return Pattern.MULTILINE;
         }
         else if (flagCode.equalsIgnoreCase("i")) { //$NON-NLS-1$
            return Pattern.CASE_INSENSITIVE;
         }
         else if (flagCode.equalsIgnoreCase("x")) { //$NON-NLS-1$
            return Pattern.COMMENTS;
         }
         throw new SemantikaRuntimeException("Unknown regular expression flag: " + flagCode); //$NON-NLS-1$
      }
   }),

   Lang(ExpressionConstant.LANG, 1, new IFunctionOperation() {
      private static final long serialVersionUID = 629451L;
      @Override
      public ILiteral execute(List<? extends IConstant> args)
      {
         String text = args.get(0).getLexicalValue();
         String lang = ""; //$NON-NLS-1$ // by default
         if (text.contains("@")) { //$NON-NLS-1$
            lang = text.substring(text.lastIndexOf('@') + 1); //$NON-NLS-1$
         }
         return sExpressionFactory.getLiteral(lang);
      }
      @Override
      public String getReturnType()
      {
         return DataType.STRING;
      }
   }),

   Str(ExpressionConstant.STR, 1, new IFunctionOperation() {
      private static final long serialVersionUID = 629451L;
      @Override
      public ILiteral execute(List<? extends IConstant> args)
      {
         String text = args.get(0).getLexicalValue();
         return sExpressionFactory.getLiteral(text);
      }
      @Override
      public String getReturnType()
      {
         return DataType.STRING;
      }
   });

   private static ExpressionObjectFactory sExpressionFactory = ExpressionObjectFactory.getInstance();

   private final String mName;
   private final int mArity;
   private final IFunctionOperation mOperation;

   BuiltInFunction(String name, int arity, IFunctionOperation op)
   {
      mName = name;
      mArity = arity;
      mOperation = op;
   }

   public String getName()
   {
      return mName;
   }

   public FunctionSymbol getFunctionSymbol()
   {
      return new FunctionSymbol(mName, mOperation);
   }

   public int getArity()
   {
      return mArity;
   }
}
