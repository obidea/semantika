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
package com.obidea.semantika.expression.base;

import com.obidea.semantika.datatype.AbstractXmlType;
import com.obidea.semantika.datatype.XmlDataTypeProfile;

public class LiteralUtils
{
   /**
    * Get the data type given a <code>term</code>, which can be a variable, a
    * constant, or a function.
    * 
    * @param term
    *           the <code>term</code> object.
    * @return the <code>Datatype</code> if the data type in the
    *         <code>term</code> is recognized, or <code>null</code> otherwise
    */
   public static AbstractXmlType<?> getDatatype(ITerm term)
   {
      String datatypeUri = term.getDatatype();
      return XmlDataTypeProfile.getXmlDatatype(datatypeUri);
   }

   /**
    * Get the Java object representation of the <code>literal</code> value.
    * E.g., if the literal object represents "2"^^xsd:int, then the returned
    * object is a <code>java.lang.Integer</code>.
    * 
    * @param literal
    *           the <code>literal</code> object.
    * @return Java object representation of <code>literal</code>
    */
   public static Object getValue(ILiteral literal)
   {
      AbstractXmlType<?> dt = getDatatype(literal);
      String lexicalValue = literal.getLexicalValue();
      return dt.getValue(lexicalValue);
   }
}
