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
package com.obidea.semantika.expression;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import com.obidea.semantika.datatype.DataType;
import com.obidea.semantika.expression.base.Atom;
import com.obidea.semantika.expression.base.Function;
import com.obidea.semantika.expression.base.FunctionSymbol;
import com.obidea.semantika.expression.base.IAtom;
import com.obidea.semantika.expression.base.IPredicate;
import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.expression.base.IVariable;
import com.obidea.semantika.expression.base.Literal;
import com.obidea.semantika.expression.base.Predicate;
import com.obidea.semantika.expression.base.Query;
import com.obidea.semantika.expression.base.Rule;
import com.obidea.semantika.expression.base.Term;
import com.obidea.semantika.expression.base.TermUtils;
import com.obidea.semantika.expression.base.UriReference;
import com.obidea.semantika.expression.base.Variable;

public class ExpressionObjectFactory implements IExpressionObjectFactory
{
   private static ExpressionObjectFactory sInstance;

   public static ExpressionObjectFactory getInstance()
   {
      if (sInstance == null) {
         sInstance = new ExpressionObjectFactory();
      }
      return sInstance;
   }

   public Query createQuery(IAtom... body)
   {
      return createQuery(false, Arrays.asList(body));
   }

   public Query createQuery(boolean isDistinct, IAtom... body)
   {
      return createQuery(isDistinct, Arrays.asList(body));
   }

   public Query createQuery(List<? extends IAtom> body)
   {
      return createQuery(false, body);
   }

   @Override
   public Query createQuery(boolean isDistinct, List<? extends IAtom> body)
   {
      Query query = new Query(isDistinct);
      for (IAtom atom : body) {
         query.addAtom(atom);
      }
      return query;
   }

   public Rule createRule(IAtom head, IAtom... body)
   {
      return createRule(head, Arrays.asList(body));
   }

   @Override
   public Rule createRule(IAtom head, List<? extends IAtom> body)
   {
      IPredicate rulePredicate = head.getPredicate();
      Rule rule = new Rule(rulePredicate);
      for (ITerm term : head.getTerms()) {
         if (term instanceof IVariable) {
            IVariable var = (IVariable) term;
            rule.addDistVar(var);
         }
      }
      for (IAtom atom : body) {
         rule.addAtom(atom);
      }
      return rule;
   }

   public Atom createAtom(String name, ITerm... terms)
   {
      return createAtom(name, Arrays.asList(terms));
   }

   @Override
   public Atom createAtom(String name, List<? extends ITerm> terms)
   {
      List<Term> termList = new ArrayList<Term>();
      for (ITerm t : terms) {
         termList.add((Term) t);
      }
      return new Atom(getPredicate(name), termList);
   }

   @Override
   public Predicate getPredicate(String name)
   {
      return new Predicate(name);
   }

   @Override
   public Variable getVariable(String name)
   {
      return (Variable) TermUtils.makeVariable(name);
   }

   @Override
   public Variable getVariable(String name, String datatype)
   {
      return (Variable) TermUtils.makeTypedVariable(name, datatype);
   }

   @Override
   public Literal getLiteral(String value, String datatype)
   {
      return (Literal) TermUtils.makeTypedLiteral(value, datatype);
   }

   @Override
   public Literal getLiteral(String value)
   {
      return (Literal) TermUtils.makeTypedLiteral(value, DataType.STRING);
   }

   @Override
   public Literal getLiteral(boolean value)
   {
      return (Literal) TermUtils.makeTypedLiteral(value ? "true" : "false", DataType.BOOLEAN);
   }

   @Override
   public Literal getLiteral(double value)
   {
      return (Literal) TermUtils.makeTypedLiteral(String.valueOf(value), DataType.DOUBLE);
   }

   @Override
   public Literal getLiteral(float value)
   {
      return (Literal) TermUtils.makeTypedLiteral(String.valueOf(value), DataType.FLOAT);
   }

   @Override
   public Literal getLiteral(BigInteger value)
   {
      return (Literal) TermUtils.makeTypedLiteral(String.valueOf(value), DataType.INTEGER);
   }

   @Override
   public Literal getLiteral(BigDecimal value)
   {
      return (Literal) TermUtils.makeTypedLiteral(String.valueOf(value), DataType.DECIMAL);
   }

   @Override
   public Literal getLiteral(long value)
   {
      return (Literal) TermUtils.makeTypedLiteral(String.valueOf(value), DataType.LONG);
   }

   @Override
   public Literal getLiteral(int value)
   {
      return (Literal) TermUtils.makeTypedLiteral(String.valueOf(value), DataType.INT);
   }

   @Override
   public Literal getLiteral(short value)
   {
      return (Literal) TermUtils.makeTypedLiteral(String.valueOf(value), DataType.SHORT);
   }

   @Override
   public Literal getLiteral(byte value)
   {
      return (Literal) TermUtils.makeTypedLiteral(String.valueOf(value), DataType.BYTE);
   }

   @Override
   public Literal getDate(XMLGregorianCalendar value)
   {
      final QName schemaType = DatatypeConstants.DATE;
      if(!schemaType.equals(value.getXMLSchemaType())) {
         throw new IllegalArgumentException();
      }
      return (Literal) TermUtils.makeTypedLiteral(value.toXMLFormat(), DataType.DATE);
   }

   @Override
   public Literal getTime(XMLGregorianCalendar value)
   {
      final QName schemaType = DatatypeConstants.TIME;
      if(!schemaType.equals(value.getXMLSchemaType())) {
         throw new IllegalArgumentException();
      }
      return (Literal) TermUtils.makeTypedLiteral(value.toXMLFormat(), DataType.TIME);
   }

   @Override
   public Literal getDateTime(XMLGregorianCalendar value)
   {
      final QName schemaType = DatatypeConstants.DATETIME;
      if(!schemaType.equals(value.getXMLSchemaType())) {
         throw new IllegalArgumentException();
      }
      return (Literal) TermUtils.makeTypedLiteral(value.toXMLFormat(), DataType.DATE_TIME);
   }

   @Override
   public Literal getPlainLiteral(String value)
   {
      return (Literal) TermUtils.makePlainLiteral(value);
   }

   @Override
   public Literal getPlainLiteral(String value, String lang)
   {
      return (Literal) TermUtils.makePlainLiteral(value, lang);
   }

   @Override
   public UriReference getUriReference(URI uri)
   {
      return (UriReference) TermUtils.makeUriReference(uri.toString());
   }

   @Override
   public Function getFunction(FunctionSymbol functionSymbol, List<? extends ITerm> parameters)
   {
      List<Term> parameterList = new ArrayList<Term>();
      for (ITerm p : parameters) {
         parameterList.add((Term) p);
      }
      return (Function) TermUtils.makeFunction(functionSymbol, parameterList);
   }

   @Override
   public Function formAnd(ITerm t1, ITerm t2)
   {
      return (Function) TermUtils.makeAnd((Term) t1, (Term) t2);
   }

   @Override
   public Function formOr(ITerm t1, ITerm t2)
   {
      return (Function) TermUtils.makeOr((Term) t1, (Term) t2);
   }

   @Override
   public Function formNot(ITerm t)
   {
      return (Function) TermUtils.makeNot((Term) t);
   }

   @Override
   public Function formEq(ITerm t1, ITerm t2)
   {
      return (Function) TermUtils.makeEqual((Term) t1, (Term) t2);
   }

   @Override
   public Function formNeq(ITerm t1, ITerm t2)
   {
      return (Function) TermUtils.makeNotEqual((Term) t1, (Term) t2);
   }

   @Override
   public Function formGt(ITerm t1, ITerm t2)
   {
      return (Function) TermUtils.makeGreaterThan((Term) t1, (Term) t2);
   }

   @Override
   public Function formGte(ITerm t1, ITerm t2)
   {
      return (Function) TermUtils.makeGreaterThanAndEqualTo((Term) t1, (Term) t2);
   }

   @Override
   public Function formLt(ITerm t1, ITerm t2)
   {
      return (Function) TermUtils.makeLessThan((Term) t1, (Term) t2);
   }

   @Override
   public Function formLte(ITerm t1, ITerm t2)
   {
      return (Function) TermUtils.makeLessThanAndEqualTo((Term) t1, (Term) t2);
   }

   @Override
   public Function formIsNull(ITerm t)
   {
      return (Function) TermUtils.makeIsNull((Term) t);
   }

   @Override
   public Function formIsNotNull(ITerm t)
   {
      return (Function) TermUtils.makeIsNotNull((Term) t);
   }

   @Override
   public Function formAddition(ITerm t1, ITerm t2)
   {
      return (Function) TermUtils.makeAdd((Term) t1, (Term) t2);
   }

   @Override
   public Function formSubtraction(ITerm t1, ITerm t2)
   {
      return (Function) TermUtils.makeSubstract((Term) t1, (Term) t2);
   }

   @Override
   public Function formMultiplication(ITerm t1, ITerm t2)
   {
      return (Function) TermUtils.makeMultiply((Term) t1, (Term) t2);
   }

   @Override
   public Function formDivision(ITerm t1, ITerm t2)
   {
      return (Function) TermUtils.makeDivide((Term) t1, (Term) t2);
   }

   @Override
   public Function formStringConcat(ITerm t1, ITerm t2)
   {
      return (Function) TermUtils.makeConcat((Term) t1, (Term) t2);
   }

   public Function formRegex(ITerm text, ITerm pattern, ITerm flag)
   {
      return (Function) TermUtils.makeRegex((Term) text, (Term) pattern, (Term) flag);
   }

   public Function formLang(ITerm t)
   {
      return (Function) TermUtils.makeLang((Term) t);
   }

   @Override
   public Function formStr(ITerm t)
   {
      return (Function) TermUtils.makeStr((Term) t);
   }

   /*
    * Prevent external instantiation
    */
   private ExpressionObjectFactory()
   {
      // NO-OP
   }
}
