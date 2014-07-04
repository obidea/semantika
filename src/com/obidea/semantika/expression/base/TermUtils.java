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
package com.obidea.semantika.expression.base;

import java.util.List;

import com.obidea.semantika.datatype.DataType;
import com.obidea.semantika.util.Serializer;
import com.obidea.semantika.util.StringUtils;

/**
 * A utility class to assign the term its specification. Internal usage only.
 * 
 * @noextend This class is not intended to be extended by clients.
 */
public final class TermUtils
{
   private static final String EMPTY = "";

   public static IVariable makeVariable(String name)
   {
      return new Variable(name, DataType.PLAIN_LITERAL);
   }

   public static IVariable makeTypedVariable(String name, String datatype)
   {
      return new Variable(name, datatype);
   }

   public static ILiteral makePlainLiteral(String value)
   {
      return new Literal(value, EMPTY, DataType.PLAIN_LITERAL);
   }

   public static ILiteral makePlainLiteral(String value, String lang)
   {
      return new Literal(value, lang, DataType.PLAIN_LITERAL);
   }
   
   public static ILiteral makeTypedLiteral(String value, String datatype)
   {
      return new Literal(value, EMPTY, datatype);
   }

   public static IUriReference makeUriReference(String value)
   {
      return new UriReference(value);
   }

   public static IFunction makeFunction(String name, List<? extends ITerm> parameters)
   {
      return new Function(name, parameters);
   }

   public static IFunction makeFunction(FunctionSymbol symbol, List<? extends ITerm> parameters)
   {
      return new Function(symbol, parameters);
   }

   public static IFunction makeAnd(ITerm t1, ITerm t2)
   {
      return Function.createBuiltInFunction(BuiltInFunction.And, t1, t2);
   }

   public static IFunction makeOr(ITerm t1, ITerm t2)
   {
      return Function.createBuiltInFunction(BuiltInFunction.Or, t1, t2);
   }

   public static IFunction makeNot(ITerm t)
   {
      return Function.createBuiltInFunction(BuiltInFunction.Not, t);
   }

   public static IFunction makeEqual(ITerm t1, ITerm t2)
   {
      return Function.createBuiltInFunction(BuiltInFunction.Equal, t1, t2);
   }

   public static IFunction makeNotEqual(ITerm t1, ITerm t2)
   {
      return Function.createBuiltInFunction(BuiltInFunction.NotEqual, t1, t2);
   }

   public static IFunction makeGreaterThan(ITerm t1, ITerm t2)
   {
      return Function.createBuiltInFunction(BuiltInFunction.GreaterThan, t1, t2);
   }

   public static IFunction makeGreaterThanAndEqualTo(ITerm t1, ITerm t2)
   {
      return Function.createBuiltInFunction(BuiltInFunction.GreaterThanEqual, t1, t2);
   }

   public static IFunction makeLessThan(ITerm t1, ITerm t2)
   {
      return Function.createBuiltInFunction(BuiltInFunction.LessThan, t1, t2);
   }

   public static IFunction makeLessThanAndEqualTo(ITerm t1, ITerm t2)
   {
      return Function.createBuiltInFunction(BuiltInFunction.LessThanEqual, t1, t2);
   }

   public static IFunction makeIsNull(ITerm t)
   {
      return Function.createBuiltInFunction(BuiltInFunction.IsNull, t);
   }

   public static IFunction makeIsNotNull(ITerm t)
   {
      return Function.createBuiltInFunction(BuiltInFunction.IsNotNull, t);
   }

   public static IFunction makeAdd(ITerm t1, ITerm t2)
   {
      return Function.createBuiltInFunction(BuiltInFunction.Add, t1, t2);
   }

   public static IFunction makeSubstract(ITerm t1, ITerm t2)
   {
      return Function.createBuiltInFunction(BuiltInFunction.Subtract, t1, t2);
   }

   public static IFunction makeMultiply(ITerm t1, ITerm t2)
   {
      return Function.createBuiltInFunction(BuiltInFunction.Multiply, t1, t2);
   }

   public static IFunction makeDivide(ITerm t1, ITerm t2)
   {
      return Function.createBuiltInFunction(BuiltInFunction.Divide, t1, t2);
   }

   public static IFunction makeConcat(ITerm t1, ITerm t2)
   {
      return Function.createBuiltInFunction(BuiltInFunction.Concat, t1, t2);
   }

   public static IFunction makeRegex(ITerm text, ITerm pattern, ITerm flag)
   {
      return Function.createBuiltInFunction(BuiltInFunction.Regex, text, pattern, flag);
   }

   public static IFunction makeLang(ITerm t)
   {
      return Function.createBuiltInFunction(BuiltInFunction.Lang, t);
   }

   public static IFunction makeStr(ITerm t)
   {
      return Function.createBuiltInFunction(BuiltInFunction.Str, t);
   }

   /*
    * Public utility methods
    */

   public static boolean isVariable(ITerm term)
   {
      return (term instanceof IVariable) ? true : false;
   }

   public static boolean isLiteral(ITerm term)
   {
      return (term instanceof ILiteral) ? true : false;
   }

   public static boolean isUriReference(ITerm term)
   {
      return (term instanceof IUriReference) ? true : false;
   }

   public static boolean isFunction(ITerm term)
   {
      return (term instanceof IFunction) ? true : false;
   }

   public static ITerm copy(ITerm term)
   {
      return (ITerm) Serializer.copy(term);
   }

   public static IVariable copy(IVariable variable)
   {
      return (IVariable) Serializer.copy(variable);
   }

   public static ILiteral copy(ILiteral literal)
   {
      return (ILiteral) Serializer.copy(literal);
   }

   public static IUriReference copy(IUriReference uriReference)
   {
      return (IUriReference) Serializer.copy(uriReference);
   }

   public static IFunction copy(IFunction function)
   {
      return (IFunction) Serializer.copy(function);
   }

   public static IBNode copy(IBNode bnode)
   {
      return (IBNode) Serializer.copy(bnode);
   }

   public static IVariable asVariable(ITerm term)
   {
      return (IVariable) term;
   }

   public static IConstant asConstant(ITerm term)
   {
      return (IConstant) term;
   }

   public static ILiteral asLiteral(ITerm term)
   {
      return (ILiteral) term;
   }

   public static IUriReference asUriReference(ITerm term)
   {
      return (IUriReference) term;
   }

   public static IFunction asFunction(ITerm term)
   {
      return (IFunction) term;
   }

   /**
    * Prints out the string representation of the given <code>term</code>.
    * 
    * @param term
    *           the input term
    * @return the syntactic representation of the input term.
    */
   public static String toString(ITerm term)
   {
      if (term == null) {
         return ""; //$NON-NLS-1$
      }
      else {
         StringBuilder sb = new StringBuilder();
         toString(term, sb);
         return sb.toString();
      }
   }

   /**
    * Writes the string representation of the given <code>term</code>
    * to the <code>stringCache</code>.
    * 
    * @param term
    *           the input term
    * @param stringCache
    *           the string cache
    */
   public static void toString(ITerm term, StringBuilder stringCache)
   {
      if (term instanceof IVariable) {
         IVariable var = (IVariable) term;
         stringCache.append(var.getName()); //$NON-NLS-1$
      }
      else if (term instanceof IConstant) {
         IConstant constant = (IConstant) term;
         if (constant instanceof ILiteral) {
            ILiteral literal = (ILiteral) constant;
            stringCache.append("\"").append(literal.getValue()); //$NON-NLS-1$
            if (!StringUtils.isEmpty(literal.getLanguageTag())) {
               stringCache.append("@"); //$NON-NLS-1$
               stringCache.append(literal.getLanguageTag());
            }
            stringCache.append("\""); //$NON-NLS-1$
         }
         else if (constant instanceof IUriReference) {
            IUriReference uriRef = (IUriReference) constant;
            stringCache.append(uriRef.toUri());
         }
         else if (constant instanceof NullValue) {
            NullValue nullValue = (NullValue) constant;
            stringCache.append(nullValue.getLexicalValue());
         }
         else {
            stringCache.append(constant.getLexicalValue());
         }
      }
      else if (term instanceof IFunction) {
         IFunction fun = (IFunction) term;
         stringCache.append(fun.getName());
         stringCache.append("("); //$NON-NLS-1$
         boolean needComma = false;
         for (ITerm arg : fun.getParameters()) {
            if (needComma) {
               stringCache.append(","); //$NON-NLS-1$
            }
            toString(arg, stringCache);
            needComma = true;
         }
         stringCache.append(")"); //$NON-NLS-1$
      }
   }
}
