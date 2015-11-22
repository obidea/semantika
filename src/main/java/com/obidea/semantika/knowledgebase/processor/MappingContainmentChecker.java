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
package com.obidea.semantika.knowledgebase.processor;

import java.util.ArrayList;
import java.util.List;

import com.obidea.semantika.database.sql.base.ISqlExpression;
import com.obidea.semantika.database.sql.base.SqlSelectItem;
import com.obidea.semantika.expression.base.AbstractFunction;
import com.obidea.semantika.expression.base.Atom;
import com.obidea.semantika.expression.base.AtomVisitorAdapter;
import com.obidea.semantika.expression.base.IAtom;
import com.obidea.semantika.expression.base.IConstant;
import com.obidea.semantika.expression.base.IFunction;
import com.obidea.semantika.expression.base.IIriReference;
import com.obidea.semantika.expression.base.ILiteral;
import com.obidea.semantika.expression.base.IRule;
import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.expression.base.IVariable;
import com.obidea.semantika.expression.base.TermUtils;
import com.obidea.semantika.knowledgebase.TermSubstitutionBinding;
import com.obidea.semantika.knowledgebase.UnificationException;
import com.obidea.semantika.knowledgebase.Unifier;
import com.obidea.semantika.mapping.IIriTemplate;
import com.obidea.semantika.mapping.base.IMapping;
import com.obidea.semantika.mapping.base.TripleAtom;
import com.obidea.semantika.mapping.base.sql.SqlJoin;
import com.obidea.semantika.mapping.base.sql.SqlQuery;
import com.obidea.semantika.mapping.base.sql.SqlSubQuery;
import com.obidea.semantika.util.Serializer;

public class MappingContainmentChecker
{
   public static boolean isContained(IMapping sourceMapping, IMapping targetMapping, List<IRule> constraintRules) throws MappingContaimentCheckException
   {
      validateMapping(sourceMapping);
      validateMapping(targetMapping);
      
      /*
       * Freezing triple atom from mapping head (Note: possible terms are variables, constant or URI
       * template function).
       */
      IAtom sourceFact = freeze(sourceMapping.getHead());
      
      /*
       * Freezing SQL table atom from mapping body (Note: all terms are variables).
       */
      IAtom tableFact = freeze(sourceMapping.getBody().get(0));
      
      for (IRule rule : constraintRules) {
         try {
            TripleAtom mappingHead = (TripleAtom) Serializer.copy(targetMapping.getHead());
            TermSubstitutionBinding theta = TermSubstitutionBinding.createEmptyBinding();
            Unifier.findSubstitution(tableFact, rule.getBody().get(0), theta);
            mappingHead.apply(theta);
            IAtom targetFact = freeze(mappingHead);
            if (targetFact.equals(sourceFact)) {
               return true;
            }
         }
         catch (UnificationException e) {
            continue; // continue to the next rule if unifier is not found
         }
      } 
      return false;
   }
   
   public static IAtom freeze(IAtom atom)
   {
      final List<IConstant> constantList = new ArrayList<IConstant>();
      atom.accept(new AtomVisitorAdapter()
      {
         @Override
         public void visit(IAtom atom)
         {
            for (ITerm term : atom.getTerms()) {
               term.accept(this);
            }
         }
         @Override
         public void visit(IFunction function)
         {
            if (function instanceof IIriTemplate) {
               IIriTemplate uriTemplate = (IIriTemplate) function;
               List<IConstant> templateArgs = new ArrayList<IConstant>();
               for (ITerm term : uriTemplate.getParameters()) {
                  if (term instanceof IVariable) {
                     templateArgs.add(TermUtils.makePlainLiteral(term.getName()));
                  }
                  else if (term instanceof IConstant) {
                     templateArgs.add((IConstant) term);
                  }
               }
               IIriReference iriRef = uriTemplate.execute(templateArgs);
               constantList.add(iriRef);
            }
            else {
               // NO-OP: Should never go here
            }
         }
         @Override
         public void visit(IIriReference iriReference)
         {
            constantList.add(iriReference);
         }
         @Override
         public void visit(ILiteral literal)
         {
            constantList.add(literal);
         }
         @Override
         public void visit(IVariable variable)
         {
            constantList.add(TermUtils.makePlainLiteral(variable.getName()));
         }
      });
      
      return new Atom(atom.getPredicate(), constantList);
   }

   private static void validateMapping(IMapping mapping) throws MappingContaimentCheckException
   {
      SqlQuery sourceQuery = mapping.getSourceQuery();
      
      /*
       * MCC doesn't apply when the source query has built-in functions in its column projection
       */
      for (SqlSelectItem selectItem : sourceQuery.getSelectItems()) {
         ISqlExpression expression = selectItem.getExpression();
         if (expression instanceof AbstractFunction) {
            String message = String.format("Source query contains column expression '%s'", expression); //$NON-NLS-1$
            throw new MappingContaimentCheckException(mapping, message);
         }
      }
      
      /*
       * MCC doesn't apply for JOIN query or sub-queries.
       */
      ISqlExpression tableExpression = sourceQuery.getFromExpression();
      if (tableExpression instanceof SqlJoin) {
         throw new MappingContaimentCheckException(mapping, "Source query contains SQL JOIN expression"); //$NON-NLS-1$
      }
      else if (tableExpression instanceof SqlSubQuery) {
         throw new MappingContaimentCheckException(mapping, "Source query contains sub-query expression"); //$NON-NLS-1$
      }
      
      /*
       * MCC doesn't apply when the source query has filters.
       */
      if (sourceQuery.hasWhereExpression()) {
         throw new MappingContaimentCheckException(mapping, "Source query contains SQL WHERE expression"); //$NON-NLS-1$
      }
   }
}
