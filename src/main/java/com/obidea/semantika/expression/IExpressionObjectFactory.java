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
package com.obidea.semantika.expression;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import com.obidea.semantika.expression.base.FunctionSymbol;
import com.obidea.semantika.expression.base.IAtom;
import com.obidea.semantika.expression.base.IFunction;
import com.obidea.semantika.expression.base.ILiteral;
import com.obidea.semantika.expression.base.IPredicate;
import com.obidea.semantika.expression.base.IQuery;
import com.obidea.semantika.expression.base.IRule;
import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.expression.base.IUriReference;
import com.obidea.semantika.expression.base.IVariable;

public interface IExpressionObjectFactory
{
   /**
    * Creates a rule given the rule head and rule body.
    * 
    * @param head
    *          an atom as the rule head.
    * @param body
    *          a list of atoms as the rule body
    * @return a rule object
    */
   IRule createRule(IAtom head, List<? extends IAtom> body);

   /**
    * Creates a query given the query body and a distinct indicator.
    * 
    * @param isDistinct
    *           if the query needs to return distinct (different) values, i.e.,
    *           ignoring duplicates.
    * @param body
    *           a list of atoms as the query body
    * @return a query object
    */
   IQuery createQuery(boolean isDistinct, List<? extends IAtom> body);

   /**
    * Creates an atom given the predicate and a list of terms.
    * 
    * @param name
    *           a predicate name.
    * @param terms
    *           a list of terms.
    * @return an atom object.
    */
   IAtom createAtom(String name, List<? extends ITerm> terms);

   /**
    * Creates a predicate object with the given name.
    * 
    * @param name
    *          a predicate name.
    * @return a predicate object.
    */
   IPredicate getPredicate(String name);

   /**
    * Creates a variable with the given name. The data type is assumed to be
    * a <code>PLAIN_LITERAL</code>.
    * 
    * @param name
    *           a variable name.
    * @return a variable object.
    */
   IVariable getVariable(String name);

   /**
    * Creates a typed variable with given name and data type.
    * 
    * @param name
    *           a variable name.
    * @param datatype
    *           type of the variable.
    * @return a variable object.
    */
   IVariable getVariable(String name, String datatype);

   /**
    * Creates a typed literal with given lexical form and data type.
    * 
    * @param value
    *           a lexical value of the literal
    * @param datatype
    *           type of the literal
    * @return a constant object representing the typed literal
    */
   ILiteral getLiteral(String value, String datatype);

   /**
    * Creates a typed literal with <code>xsd:string</code> datatype.
    * 
    * @param value
    *           a string value
    * @return a constant object representing the string literal
    */
   ILiteral getLiteral(String value);

   /**
    * Creates a typed literal with <code>xsd:boolean</code> datatype.
    * 
    * @param value
    *           a boolean value
    * @return a constant object representing the boolean literal
    */
   ILiteral getLiteral(boolean value);

   /**
    * Creates a typed literal with <code>xsd:double</code> datatype.
    * 
    * @param value
    *           a double value
    * @return a constant object representing the double literal
    */
   ILiteral getLiteral(double value);

   /**
    * Creates a typed literal with <code>xsd:float</code> datatype.
    * 
    * @param value
    *           a float value
    * @return a constant object representing the float literal
    */
   ILiteral getLiteral(float value);

   /**
    * Creates a typed literal with <code>xsd:int</code> datatype.
    * 
    * @param value
    *           a big integer value
    * @return a constant object representing the integer literal
    */
   ILiteral getLiteral(BigInteger value);

   /**
    * Creates a typed literal with <code>xsd:decimal</code> datatype.
    * 
    * @param value
    *           a big decimal value
    * @return a constant object representing the decimal literal
    */
   ILiteral getLiteral(BigDecimal value);

   /**
    * Creates a typed literal with <code>xsd:long</code> datatype. The value range for
    * <b>long</b> is 9223372036854775807 to -9223372036854775808.
    * 
    * @param value
    *           a long value
    * @return a constant object representing the long literal
    */
   ILiteral getLiteral(long value);

   /**
    * Creates a typed literal with <code>xsd:int</code> datatype. The value range for
    * <b>int</b> is 2147483647 to -2147483648.
    * 
    * @param value
    *           an integer value
    * @return a constant object representing the integer literal
    */
   ILiteral getLiteral(int value);

   /**
    * Creates a typed literal with <code>xsd:short</code> datatype. The value range for
    * <b>short</b> is 32767 to -32768.
    * 
    * @param value
    *           a short value
    * @return a constant object representing the short literal
    */
   ILiteral getLiteral(short value);

   /**
    * Creates a typed literal with <code>xsd:byte</code> datatype. The value range for
    * <b>byte</b> is 127 to -128
    * 
    * @param value
    *           a byte value
    * @return a constant object representing the byte literal
    */
   ILiteral getLiteral(byte value);

   /**
    * Creates a type literal with <code>xsd:date</code> datatype.
    * 
    * @param value
    *          a calendar value.
    * @return a constant object representing the date litereal.
    */
   ILiteral getDate(XMLGregorianCalendar value);

   /**
    * Creates a type literal with <code>xsd:time</code> datatype.
    * 
    * @param value
    *          a calendar value.
    * @return a constant object representing the time litereal.
    */
   ILiteral getTime(XMLGregorianCalendar value);

   /**
    * Creates a type literal with <code>xsd:dateTime</code> datatype.
    * 
    * @param value
    *          a calendar value.
    * @return a constant object representing the date-time litereal.
    */
   ILiteral getDateTime(XMLGregorianCalendar value);

   /**
    * Creates an untyped plain literal with no language tag.
    * 
    * @param value
    *           a string value
    * @return a constant object representing the plain literal
    */
   ILiteral getPlainLiteral(String value);

   /**
    * Creates an untyped plain literal with the given language tag.
    * 
    * @param value
    *           a string value
    * @param lang
    *           a language identifier
    * @return a constant object representing the plain literal with language tag
    */
   ILiteral getPlainLiteral(String value, String lang);

   /**
    * Creates a URI reference object with the given valid URI string.
    * 
    * @param uri
    *           a URI object.
    * @return a URI reference object.
    */
   IUriReference getUriReference(URI uri);

   /**
    * Creates a function with the given function symbol and its parameters.
    * 
    * @param functionSymbol
    *           the function symbol
    * @param parameters
    *           a list of function parameters.
    * @return a function object.
    */
   IFunction getFunction(FunctionSymbol functionSymbol, List<? extends ITerm> parameters);

   /**
    * Creates a boolean value expression using <code>AND</code> operator.
    * 
    * @param t1
    *           first boolean term.
    * @param t2
    *           second boolean term.
    * @return a function object representing the AND expression.
    */
   IFunction formAnd(ITerm t1, ITerm t2);

   /**
    * Creates a boolean value expression using <code>OR</code> operator.
    * 
    * @param t1
    *           first boolean term.
    * @param t2
    *           second boolean term.
    * @return a function object representing the OR expression.
    */
   IFunction formOr(ITerm t1, ITerm t2);

   /**
    * Creates a boolean value expression using <code>NOT</code> operator.
    * 
    * @param t
    *           a value expression to negate.
    * @return a function object representing the NOT expression.
    */
   IFunction formNot(ITerm t);

   /**
    * Creates a boolean comparison using equal <code>'='</code> operator.
    * 
    * @param t1
    *           first term.
    * @param t2
    *           second term.
    * @return a function object representing the equal comparison.
    */
   IFunction formEq(ITerm t1, ITerm t2);

   /**
    * Creates a boolean comparison using not equal <code>'!='</code> operator.
    * 
    * @param t1
    *           first term.
    * @param t2
    *           second term.
    * @return a function object representing the not equal comparison.
    */
   IFunction formNeq(ITerm t1, ITerm t2);

   /**
    * Creates a boolean comparison using greater than <code>'>'</code> operator.
    * 
    * @param t1
    *           first term.
    * @param t2
    *           second term.
    * @return a function object representing the greater than comparison.
    */
   IFunction formGt(ITerm t1, ITerm t2);

   /**
    * Creates a boolean comparison using greater than and equal to <code>'>='</code> operator.
    * 
    * @param t1
    *           first term.
    * @param t2
    *           second term.
    * @return a function object representing the greater than and equal to
    *         comparison.
    */
   IFunction formGte(ITerm t1, ITerm t2);

   /**
    * Creates a boolean comparison using less than <code>'<'</code> operator.
    * 
    * @param t1
    *          first term.
    * @param t2
    *          second term.
    * @return a function object representing the less than comparison.
    */
   IFunction formLt(ITerm t1, ITerm t2);

   /**
    * Creates a boolean comparison using less than and equal to <code>'<='</code> operator.
    * 
    * @param t1
    *           first term.
    * @param t2
    *           second term.
    * @return a function object representing the less than and equal to
    *         comparison.
    */
   IFunction formLte(ITerm t1, ITerm t2);

   /**
    * Creates a boolean null expression using <code>IS NULL</code> operator.
    * 
    * @param t
    *           a value expression to check null values.
    * @return a function object representing the IS NULL expression.
    */
   IFunction formIsNull(ITerm t);

   /**
    * Creates a boolean null expression using <code>IS NOT NULL</code> operator.
    * 
    * @param t
    *           a value expression to check null values.
    * @return a function object representing the IS NOT NULL expression.
    */
   IFunction formIsNotNull(ITerm t);

   /**
    * Creates an arithmetic expression using add '+' operator.
    * 
    * @param t1
    *           first term
    * @param t2
    *           second term
    * @return a function object representing addition operation
    */
   IFunction formAddition(ITerm t1, ITerm t2);

   /**
    * Creates an arithmetic expression using subtract '-' operator.
    * 
    * @param t1
    *           first term
    * @param t2
    *           second term
    * @return a function object representing subtraction operation
    */
   IFunction formSubtraction(ITerm t1, ITerm t2);

   /**
    * Creates an arithmetic expression using multiply '*' operator.
    * 
    * @param t1
    *           first term
    * @param t2
    *           second term
    * @return a function object representing multiplication operation
    */
   IFunction formMultiplication(ITerm t1, ITerm t2);

   /**
    * Creates an arithmetic expression using divide '/' operator.
    * 
    * @param t1
    *           first term
    * @param t2
    *           second term
    * @return a function object representing division operation
    */
   IFunction formDivision(ITerm t1, ITerm t2);

   /**
    * Creates a string concatenation expression using <code>CONCAT</code> operator.
    * 
    * @param t1
    *           first term
    * @param t2
    *           second term
    * @return a function object representing string concatenation operation
    */
   IFunction formStringConcat(ITerm t1, ITerm t2);

   /**
    * Creates a string convert expression using <code>STR</code> operator.
    * 
    * @param t
    *           a target term
    * @return a function object representing string convert operation
    */
   IFunction formStr(ITerm t);
}
