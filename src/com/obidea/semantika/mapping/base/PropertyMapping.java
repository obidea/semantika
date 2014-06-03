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
import com.obidea.semantika.expression.base.IPredicate;
import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.expression.base.Predicate;
import com.obidea.semantika.mapping.base.sql.SqlQuery;

public class PropertyMapping extends AbstractMapping implements IPropertyMapping
{
   private static final long serialVersionUID = 629451L;

   private TripleAtom mPropertyAtom;

   private URI mPropertySignature;
   private ITerm mSubjectMapValue;
   private ITerm mObjectMapValue;

   /**
    * Constructs a property mapping for a named role/attribute in property signature such that each
    * instances of this role/attribute is coming from the data in the database, represented by the
    * source query.
    * 
    * @param propertySignature
    *           the name of the role/attribute entity (i.e., the property name).
    * @param sourceQuery
    *           the data projection that will be mapped to the property instances.
    */
   public PropertyMapping(URI propertySignature, SqlQuery sourceQuery)
   {
      super(sourceQuery);
      mPropertySignature = propertySignature;
   }

   @Override
   public IPredicate getHeadSymbol()
   {
      return new Predicate(mPropertySignature);
   }

   @Override
   public TripleAtom getTargetAtom()
   {
      if (mPropertyAtom == null) {
         if (mSubjectMapValue == null) {
            throw new NullPointerException("Subject map has not defined yet."); //$NON-NLS-1$
         }
         if (mObjectMapValue == null) {
            throw new NullPointerException("Object map has not defined yet."); //$NON-NLS-1$
         }
         mPropertyAtom = sMappingFactory.createPropertyAtom(mPropertySignature, mSubjectMapValue, mObjectMapValue);
      }
      return mPropertyAtom;
   }

   @Override
   public void setSubjectMapValue(ITerm value)
   {
      mSubjectMapValue = value;
      mPropertyAtom = null; // a flag that indicates map value has been changed
   }

   @Override
   public ITerm getSubjectMapValue()
   {
      return mSubjectMapValue;
   }

   @Override
   public void setObjectMapValue(ITerm value)
   {
      mObjectMapValue = value;
      mPropertyAtom = null; // a flag that indicates map value has been changed
   }

   @Override
   public ITerm getObjectMapValue()
   {
      return mObjectMapValue;
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
      final PropertyMapping other = (PropertyMapping) obj;
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
      sb.append(", ");
      sb.append(TripleAtom.getObject(getTargetAtom()));
      sb.append(")"); //$NON-NLS-1$

      if (getBody().size() > 0) {
         sb.append(" <^- "); //$NON-NLS-1$
         boolean needComma = false;
         for (final IAtom expr : getBody()) {
            if (needComma) {
               sb.append(", "); //$NON-NLS-1$
            }
            sb.append(expr);
            needComma = true;
         }
      }
      if (getConstraints().size() > 0) {
         for (final IFunction constraint : getConstraints()) {
            sb.append(", "); //$NON-NLS-1$
            sb.append(constraint);
         }
      }
      return sb.toString();
   }
}
