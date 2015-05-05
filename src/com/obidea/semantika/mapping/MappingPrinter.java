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
package com.obidea.semantika.mapping;

import com.obidea.semantika.database.sql.SqlPrinter;
import com.obidea.semantika.expression.base.IAtom;
import com.obidea.semantika.expression.base.IConstant;
import com.obidea.semantika.expression.base.IFunction;
import com.obidea.semantika.expression.base.ILiteral;
import com.obidea.semantika.expression.base.IPredicate;
import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.expression.base.IUriReference;
import com.obidea.semantika.expression.base.IVariable;
import com.obidea.semantika.expression.base.UriReference;
import com.obidea.semantika.knowledgebase.EmptyPrefixManager;
import com.obidea.semantika.knowledgebase.IPrefixManager;
import com.obidea.semantika.mapping.base.IClassMapping;
import com.obidea.semantika.mapping.base.IMapping;
import com.obidea.semantika.mapping.base.IMappingVisitor;
import com.obidea.semantika.mapping.base.IPropertyMapping;
import com.obidea.semantika.mapping.base.TripleAtom;
import com.obidea.semantika.mapping.base.sql.SqlColumn;
import com.obidea.semantika.mapping.base.sql.SqlQuery;

public class MappingPrinter implements IMappingVisitor
{
   private static final IUriReference RDF_TYPE = new UriReference("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"); //$NON-NLS-1$

   private SqlPrinter sqlPrinter = new SqlPrinter();
   private IPrefixManager mPrefixManager;

   private StringBuilder mStringBuilder;

   public MappingPrinter()
   {
      mPrefixManager = new EmptyPrefixManager();
   }

   public void setPrefixManager(IPrefixManager prefixManager)
   {
      if (prefixManager != null) {
         mPrefixManager = prefixManager;
      }
   }

   public String print(IMapping mapping)
   {
      initStringBuilder();
      mapping.accept(this);
      return mStringBuilder.toString();
   }

   @Override
   public void visit(IClassMapping classMapping)
   {
      visitMapping(classMapping);
   }

   @Override
   public void visit(IPropertyMapping propertyMapping)
   {
      visitMapping(propertyMapping);
   }

   protected void visitMapping(IMapping mapping)
   {
      final TripleAtom targetAtom = mapping.getTargetAtom();
      targetAtom.accept(this);
      
      mStringBuilder.append(" <^- "); //$NON-NLS-1$
      
      final SqlQuery sourceQuery = mapping.getSourceQuery();
      String sourceQueryStr = sqlPrinter.print(sourceQuery, true);
      mStringBuilder.append(sourceQueryStr);
   }

   @Override
   public void visit(IAtom atom)
   {
      if (atom instanceof TripleAtom) {
         TripleAtom tripleAtom = (TripleAtom) atom;
         if (containRdfType(tripleAtom)) {
            TripleAtom.getObject(tripleAtom).accept(this);
            mStringBuilder.append("("); //$NON-NLS-1$
            TripleAtom.getSubject(tripleAtom).accept(this);
            mStringBuilder.append(")"); //$NON-NLS-1$
         }
         else {
            TripleAtom.getPredicate(tripleAtom).accept(this);
            mStringBuilder.append("("); //$NON-NLS-1$
            TripleAtom.getSubject(tripleAtom).accept(this);
            mStringBuilder.append(", "); //$NON-NLS-1$
            TripleAtom.getObject(tripleAtom).accept(this);
            mStringBuilder.append(")"); //$NON-NLS-1$
         }
      }
   }

   private boolean containRdfType(TripleAtom atom)
   {
      ITerm predicateTerm = TripleAtom.getPredicate(atom);
      return predicateTerm.equals(RDF_TYPE);
   }

   @Override
   public void visit(IPredicate predicate)
   {
      // NO-OP
   }

   @Override
   public void visit(IVariable variable)
   {
      if (variable instanceof SqlColumn) {
         SqlColumn column = (SqlColumn) variable;
         mStringBuilder.append(column.getColumnName());
      }
   }

   @Override
   public void visit(IConstant literal)
   {
      // NO-OP
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
      /*
       * Shorten the URI name using the given prefix manager, if possible. The
       * name shortening will be ignored if no prefix mapping is defined (i.e.,
       * using the default EmptyPrefixManager).
       */
      String shortenNameIfPossible = mPrefixManager.shorten(uriReference.toUri());
      mStringBuilder.append(shortenNameIfPossible);
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
