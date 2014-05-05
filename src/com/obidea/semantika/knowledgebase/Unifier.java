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
package com.obidea.semantika.knowledgebase;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.obidea.semantika.expression.base.IAtom;
import com.obidea.semantika.expression.base.IConstant;
import com.obidea.semantika.expression.base.IExpressionObject;
import com.obidea.semantika.expression.base.IFunction;
import com.obidea.semantika.expression.base.ISignature;
import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.expression.base.IUriReference;
import com.obidea.semantika.expression.base.IVariable;
import com.obidea.semantika.expression.base.TermUtils;
import com.obidea.semantika.mapping.IUriTemplate;
import com.obidea.semantika.mapping.MappingObjectFactory;
import com.obidea.semantika.util.Serializer;

public class Unifier
{
   public static TermSubstitutionBinding findSubstitution(IAtom a1, IAtom a2) throws UnificationException
   {
      TermSubstitutionBinding theta = TermSubstitutionBinding.createEmptyBinding();
      findSubstitution(a1, a2, theta);
      return theta;
   }

   public static TermSubstitutionBinding findSubstitution(ITerm t1, ITerm t2) throws UnificationException
   {
      TermSubstitutionBinding theta = TermSubstitutionBinding.createEmptyBinding();
      findSubstitution(t1, t2, theta);
      return theta;
   }

   public static void findSubstitution(IAtom a1, IAtom a2, TermSubstitutionBinding substitution) throws UnificationException
   {
      /*
       * The fastest way to ignore finding the unifier is to compare both atoms syntactically. This
       * checking will throw UnificationException if the two atoms are not similar.
       */
      checkSyntacticalUnification(a1, a2);
      IAtom ac1 = copy(a1);
      IAtom ac2 = copy(a2);
      for (int i = 0; i < a1.getArity(); i++) {
         final ITerm t1 = ac1.getTerm(i);
         final ITerm t2 = ac2.getTerm(i);
         findSubstitution(t1, t2, substitution);
         ac1.apply(substitution);
         ac2.apply(substitution);
      }
      checkOverflowUnification(ac1, ac2);
   }

   public static void findSubstitution(ITerm t1, ITerm t2, TermSubstitutionBinding substitution) throws UnificationException
   {
      if (t1.equals(t2)) {
         return; // if both terms are equal then ignore them.
      }
      
      if (t1 instanceof IConstant && t2 instanceof IConstant) {
         findSubstitution(TermUtils.asConstant(t1), TermUtils.asConstant(t2), substitution);
      }
      if (t1 instanceof IFunction && t2 instanceof IFunction) {
         findSubstitution(TermUtils.asFunction(t1), TermUtils.asFunction(t2), substitution);
      }
      
      if (t1 instanceof IUriTemplate && t2 instanceof IUriReference) {
         findSubstitution((IUriTemplate) t1, TermUtils.asUriReference(t2), substitution);
      }
      if (t1 instanceof IUriReference && t2 instanceof IUriTemplate) {
         findSubstitution((IUriTemplate) t2, TermUtils.asUriReference(t1), substitution);
      }
      
      // Create a substitution unifier between variable and term
      TermSubstitutionBinding sigma = TermSubstitutionBinding.createEmptyBinding();
      if (t2 instanceof IVariable) {
         sigma.put(TermUtils.asVariable(t2), t1);
      }
      else if (t1 instanceof IVariable) {
         sigma.put(TermUtils.asVariable(t1), t2);
      }
      
      // Do substitution unifier composition.
      substitution.compose(sigma);
   }

   public static void findSubstitution(IConstant c1, IConstant c2, TermSubstitutionBinding substitution) throws UnificationException
   {
      if (!c1.equals(c2)) {
         String message = String.format("Constants are not equal \"%s\" and \"%s\"", c1.getLexicalValue(), c2.getLexicalValue()); //$NON-NLS-1$
         throw new UnificationException(message);
      }
   }

   public static void findSubstitution(IFunction f1, IFunction f2, TermSubstitutionBinding substitution) throws UnificationException
   {
      /*
       * The fastest way to ignore finding the unifier is to compare both functions syntactically. This
       * checking will throw UnificationException if the two functions are not similar.
       */
      checkSyntacticalUnification(f1, f2);
      IFunction fc1 = TermUtils.copy(f1);
      IFunction fc2 = TermUtils.copy(f2);
      for (int i = 0; i < fc1.getArity(); i++) {
         final ITerm p1 = fc1.getParameter(i);
         final ITerm p2 = fc2.getParameter(i);
         findSubstitution(p1, p2, substitution);
         fc1.apply(substitution);
         fc2.apply(substitution);
      }
      checkOverflowUnification(fc1, fc2);
   }

   public static void findSubstitution(IUriTemplate uriTemplate, IUriReference uriReference, TermSubstitutionBinding substitution) throws UnificationException
   {
      String templateString = uriTemplate.getTemplateString();
      String templateRegex = templateString.replaceAll("\\{\\d+\\}", "(.*)"); //$NON-NLS-1$
      Pattern templatePattern = Pattern.compile(templateRegex);
      
      List<ITerm> parameters = new ArrayList<ITerm>();
      Matcher m = templatePattern.matcher(uriReference.getLexicalValue());
      if (m.matches()) {
         for (int i = 1; i <= m.groupCount(); i++) {
            String value = m.group(i); // value comes from pattern matching
            String datatype = uriTemplate.getParameter(i-1).getDatatype(); // datatype comes from template function
            parameters.add(TermUtils.makeTypedLiteral(value, datatype)); //$NON-NLS-1$
         }
      }
      IUriTemplate u2 = MappingObjectFactory.getInstance().createUriTemplate(templateString, parameters);
      findSubstitution(uriTemplate, u2, substitution);
   }

   private static void checkSyntacticalUnification(ISignature s1, ISignature s2) throws UnificationException
   {
      if (!s1.isEquivalent(s2)) {
         throw new UnificationException("Signatures are not equal"); //$NON-NLS-1$
      }
   }

   private static void checkOverflowUnification(IExpressionObject a1, IExpressionObject a2) throws UnificationException
   {
      try {
         a1.toString();
         a2.toString();
      }
      catch (StackOverflowError e) {
         throw new UnificationException("Infinite loop in unification", e); //$NON-NLS-1$
      }
   }

   private static IAtom copy(IAtom atom)
   {
      return (IAtom) Serializer.copy(atom);
   }
}
