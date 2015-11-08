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

import com.obidea.semantika.expression.base.IAtom;
import com.obidea.semantika.expression.base.IConstant;
import com.obidea.semantika.expression.base.IFunction;
import com.obidea.semantika.expression.base.ILiteral;
import com.obidea.semantika.expression.base.IPredicate;
import com.obidea.semantika.expression.base.IQueryExt;
import com.obidea.semantika.expression.base.IQueryExtVisitor;
import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.expression.base.IUriReference;
import com.obidea.semantika.expression.base.IVariable;
import com.obidea.semantika.expression.base.Join;
import com.obidea.semantika.expression.base.SyntacticSugar;


public class QueryPrinter implements IQueryExtVisitor
{
   private StringBuilder mStringBuilder;

   public String print(IQueryExt query)
   {
      initStringBuilder();
      
      // Print query projection
      query.getHead().accept(this);
      
      mStringBuilder.append(" :- "); //$NON-NLS-1$
      mStringBuilder.append("\n"); //$NON-NLS-1$
      
      // Print query body
      boolean needComma = false;
      for (IAtom atom : query.getBody()) {
         if (needComma) {
            mStringBuilder.append(",\n"); //$NON-NLS-1$
         }
         mStringBuilder.append("   "); //$NON-NLS-1$
         atom.accept(this);
         needComma = true;
      }
      
      // Print filters, if any
      for (IFunction function : query.getFilters()) {
         mStringBuilder.append(",\n"); //$NON-NLS-1$
         mStringBuilder.append("   "); //$NON-NLS-1$
         function.accept(this);
      }
      return mStringBuilder.toString();
   }

   @Override
   public void visit(IQueryExt query)
   {
      // NO-OP
   }

   @Override
   public void visit(IAtom atom)
   {
      if (atom instanceof SyntacticSugar) {
         SyntacticSugar ss = (SyntacticSugar) atom;
         if (ss instanceof Join) {
            Join join = (Join) ss;
            mStringBuilder.append(join.getName());
            mStringBuilder.append("("); //$NON-NLS-1$
            join.getLeftExpression().accept(this);
            mStringBuilder.append(", "); //$NON-NLS-1$
            join.getRightExpression().accept(this);
            mStringBuilder.append(")"); //$NON-NLS-1$
         }
      }
      else {
         mStringBuilder.append(atom.getPredicate());
         mStringBuilder.append("("); //$NON-NLS-1$
         boolean needComma = false;
         for (ITerm term : atom.getTerms()) {
            if (needComma) {
               mStringBuilder.append(", "); //$NON-NLS-1$
            }
            term.accept(this);
            needComma = true;
         }
         mStringBuilder.append(")"); //$NON-NLS-1$
      }
   }

   @Override
   public void visit(IPredicate predicate)
   {
      // NO-OP
   }

   @Override
   public void visit(IVariable variable)
   {
      mStringBuilder.append(variable.getName());
   }

   @Override
   public void visit(IConstant constant)
   {
      mStringBuilder.append("\"");
      mStringBuilder.append(constant);
      mStringBuilder.append("\"");
   }

   @Override
   public void visit(ILiteral literal)
   {
      String lexicalValue = literal.getLexicalValue();
      
      Object value = literal.getValue();
      if (value instanceof Number) {
         mStringBuilder.append(lexicalValue);
      }
      else {
         /*
          * Any sequence of characters delimited by single quotes. If the single
          * quote character is included in the sequence it must be written twice.
          */
         lexicalValue = lexicalValue.replaceAll("'", "''"); //$NON-NLS-1$ //$NON-NLS-2%
         mStringBuilder.append("'").append(lexicalValue).append("'"); //$NON-NLS-1$ //$NON-NLS-2%
      }
   }

   @Override
   public void visit(IUriReference uriReference)
   {
      mStringBuilder.append(uriReference.getLexicalValue());
   }

   @Override
   public void visit(IFunction function)
   {
      mStringBuilder.append(function.getName());
      mStringBuilder.append("("); //$NON-NLS-1$
      boolean needComma = false;
      for (ITerm parameter : function.getParameters()) {
         if (needComma) {
            mStringBuilder.append(", "); //$NON-NLS-1$
         }
         parameter.accept(this);
         needComma = true;
      }
      mStringBuilder.append(")"); //$NON-NLS-1$
   }

   private void initStringBuilder()
   {
      mStringBuilder = new StringBuilder();
   }
}
