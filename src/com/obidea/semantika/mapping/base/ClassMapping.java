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
package com.obidea.semantika.mapping.base;

import java.net.URI;

import com.obidea.semantika.expression.base.IAtom;
import com.obidea.semantika.expression.base.IFunction;
import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.mapping.MappingObjectFactory;
import com.obidea.semantika.mapping.base.sql.SqlQuery;

public class ClassMapping extends AbstractMapping implements IClassMapping
{
   private static final long serialVersionUID = 629451L;

   private TripleAtom mClassAtom;

   private ITerm mSubjectMapValue;

   private SqlQuery mSourceQuery;

   /**
    * Constructs a class mapping for a concept entity named in class signature such that each
    * instances of this concept is coming from the data in the database, represented by the source
    * query.
    * 
    * @param class signature
    *           the name of the concept entity (i.e., the class name).
    * @param sourceQuery
    *           the data projection that will be mapped to the class instances.
    */
   public ClassMapping(URI classSignature, SqlQuery sourceQuery)
   {
      super(classSignature);
      mSourceQuery = sourceQuery;
   }

   @Override
   public TripleAtom getTargetAtom()
   {
      if (mClassAtom == null) {
         if (mSubjectMapValue == null) {
            throw new NullPointerException("Subject map has not defined yet."); //$NON-NLS-1$
         }
         mClassAtom = MappingObjectFactory.getInstance().createClassAtom(getSignature(),
               mSubjectMapValue);
      }
      return mClassAtom;
   }

   @Override
   public SqlQuery getSourceQuery()
   {
      return mSourceQuery;
   }

   @Override
   public void setSubjectMapValue(ITerm value)
   {
      mSubjectMapValue = value;
      mClassAtom = null; // a flag that indicates map value has been changed
   }

   @Override
   public ITerm getSubjectMapValue()
   {
      return mSubjectMapValue;
   }

   @Override
   public void accept(IMappingVisitor visitor)
   {
      visitor.visit(this);
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final ClassMapping other = (ClassMapping) obj;
      return getTargetAtom().equals(other.getTargetAtom())
            && getSourceQuery().equals(other.getSourceQuery());
   }

   /**
    * Internal use only for debugging
    */
   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder(getHeadSymbol() + "("); //$NON-NLS-1$
      sb.append(TripleAtom.getSubject(getTargetAtom()));
      sb.append(")"); //$NON-NLS-1$

      if (getBody().size() > 0) {
         sb.append(" <^- "); //$NON-NLS-1$
         boolean needComma = false;
         for (final IAtom atom : getBody()) {
            if (needComma) {
               sb.append(", "); //$NON-NLS-1$
            }
            sb.append(atom);
            needComma = true;
         }
      }
      if (getFilters().size() > 0) {
         for (final IFunction filter : getFilters()) {
            sb.append(", "); //$NON-NLS-1$
            sb.append(filter);
         }
      }
      return sb.toString();
   }
}
