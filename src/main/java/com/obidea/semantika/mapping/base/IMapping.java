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
package com.obidea.semantika.mapping.base;

import java.net.URI;

import com.obidea.semantika.expression.base.IClause;
import com.obidea.semantika.mapping.base.sql.SqlQuery;

/**
 * Represent the construction of a mapping language.
 */
public interface IMapping extends IClause
{
   /**
    * Return the signature that identifies this mapping.
    */
   public URI getSignature();

   /**
    * Return the target atom that represents the class or property atom
    * defined in the ontology.
    */
   public TripleAtom getTargetAtom();

   /**
    * Return the source query object that represents the data projection over the
    * variables occurred in the target atom (see {@link getTargetAtom()}).
    */
   public SqlQuery getSourceQuery();

   /**
    * Accept a visitor to collect the internal content of this class.
    * 
    * @param visitor
    *           a visitor object.
    */
   public void accept(IMappingVisitor visitor);
}
