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
package com.obidea.semantika.datatype;

import com.obidea.semantika.datatype.exception.InvalidLexicalFormException;

public interface IDatatype<T>
{
   /**
    * Get the datatype identifier.
    * 
    * @return the URI identifier of this datatype
    */
   public String getName();

   /**
    * Get the primitive datatype associated with this datatype.
    * 
    * @return <code>this</code> if the datatype is primitive or a primitive
    *         datatype that is a superset of the value space of this datatype.
    */
   public IDatatype<?> getPrimitiveDatatype();

   /**
    * Get the Java object representation of the given a lexical form of a string.
    * 
    * @param lexicalForm
    *           a lexical form of a string.
    * @return the Java object representation of the given a lexical form of a string.
    */
   public T getValue(String lexicalForm) throws InvalidLexicalFormException;

   /**
    * Check if the datatype is primitive. All datatypes are either primitive or
    * derived. Derived datatypes are names for subsets of the value spaces of
    * primitive datatypes, defined using specific constraining facet values.
    * <p>
    * The definition of type of datatypes follows W3C recommendation:
    * {@link http://www.w3.org/TR/2004/REC-xmlschema-2-20041028}
    * 
    * @return true if the datatype is primitive, or false otherwise.
    */
   public boolean isPrimitive();
}
