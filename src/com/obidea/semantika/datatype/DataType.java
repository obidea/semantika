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
package com.obidea.semantika.datatype;

import java.util.ArrayList;
import java.util.List;

import com.obidea.semantika.util.Namespaces;

public class DataType
{
   public static final String LITERAL = Namespaces.RDFS + "Literal"; //$NON-NLS-1$
   public static final String PLAIN_LITERAL = Namespaces.RDF + "PlainLiteral"; //$NON-NLS-1$
   public static final String XML_LITERAL = Namespaces.RDF + "XMLLiteral"; //$NON-NLS-1$
   public static final String ANY_TYPE = Namespaces.XSD + "anyType"; //$NON-NLS-1$
   public static final String ANY_SIMPLE_TYPE = Namespaces.XSD + "anySimpleType"; //$NON-NLS-1$
   public static final String STRING = Namespaces.XSD + "string"; //$NON-NLS-1$
   public static final String INTEGER = Namespaces.XSD + "integer"; //$NON-NLS-1$
   public static final String LONG = Namespaces.XSD + "long"; //$NON-NLS-1$
   public static final String INT = Namespaces.XSD + "int"; //$NON-NLS-1$
   public static final String SHORT = Namespaces.XSD + "short"; //$NON-NLS-1$
   public static final String BYTE = Namespaces.XSD + "byte"; //$NON-NLS-1$
   public static final String DECIMAL = Namespaces.XSD + "decimal"; //$NON-NLS-1$
   public static final String FLOAT = Namespaces.XSD + "float"; //$NON-NLS-1$
   public static final String BOOLEAN = Namespaces.XSD + "boolean"; //$NON-NLS-1$
   public static final String DOUBLE = Namespaces.XSD + "double"; //$NON-NLS-1$
   public static final String NON_POSITIVE_INTEGER = Namespaces.XSD + "nonPositiveInteger"; //$NON-NLS-1$
   public static final String NEGATIVE_INTEGER = Namespaces.XSD + "negativeInteger"; //$NON-NLS-1$
   public static final String NON_NEGATIVE_INTEGER = Namespaces.XSD + "nonNegativeInteger"; //$NON-NLS-1$
   public static final String UNSIGNED_LONG = Namespaces.XSD + "unsignedLong"; //$NON-NLS-1$
   public static final String UNSIGNED_INT = Namespaces.XSD + "unsignedInt"; //$NON-NLS-1$
   public static final String UNSIGNED_SHORT = Namespaces.XSD + "unsignedShort"; //$NON-NLS-1$
   public static final String UNSIGNED_BYTE = Namespaces.XSD + "unsignedByte"; //$NON-NLS-1$
   public static final String POSITIVE_INTEGER = Namespaces.XSD + "positiveInteger"; //$NON-NLS-1$
   public static final String BASE_64_BINARY = Namespaces.XSD + "base64Binary"; //$NON-NLS-1$
   public static final String HEX_BINARY = Namespaces.XSD + "hexBinary"; //$NON-NLS-1$
   public static final String ANY_URI = Namespaces.XSD + "anyURI"; //$NON-NLS-1$
   public static final String Q_NAME = Namespaces.XSD + "QName"; //$NON-NLS-1$
   public static final String NOTATION = Namespaces.XSD + "NOTATION"; //$NON-NLS-1$
   public static final String NORMALIZED_STRING = Namespaces.XSD + "normalizedString"; //$NON-NLS-1$
   public static final String TOKEN = Namespaces.XSD + "token"; //$NON-NLS-1$
   public static final String LANGUAGE = Namespaces.XSD + "language"; //$NON-NLS-1$
   public static final String NAME = Namespaces.XSD + "Name"; //$NON-NLS-1$
   public static final String NCNAME = Namespaces.XSD + "NCName"; //$NON-NLS-1$
   public static final String NMTOKEN = Namespaces.XSD + "NMToken"; //$NON-NLS-1$
   public static final String ID = Namespaces.XSD + "ID"; //$NON-NLS-1$
   public static final String IDREF = Namespaces.XSD + "IDREF"; //$NON-NLS-1$
   public static final String ENTITY = Namespaces.XSD + "ENTITY"; //$NON-NLS-1$
   public static final String DURATION = Namespaces.XSD + "duration"; //$NON-NLS-1$
   public static final String DATE_TIME = Namespaces.XSD + "dateTime"; //$NON-NLS-1$
   public static final String DATE_TIME_STAMP = Namespaces.XSD + "dateTimeStamp"; //$NON-NLS-1$
   public static final String TIME = Namespaces.XSD + "time"; //$NON-NLS-1$
   public static final String DATE = Namespaces.XSD + "date"; //$NON-NLS-1$
   public static final String G_YEAR_MONTH = Namespaces.XSD + "gYearMonth"; //$NON-NLS-1$
   public static final String G_YEAR = Namespaces.XSD + "gYear"; //$NON-NLS-1$
   public static final String G_MONTH_DAY = Namespaces.XSD + "gMonthDay"; //$NON-NLS-1$
   public static final String G_DAY = Namespaces.XSD + "gDay"; //$NON-NLS-1$
   public static final String G_MONTH = Namespaces.XSD + "gMonth"; //$NON-NLS-1$

   public static final List<String> CHARACTER_TYPES = new ArrayList<String>();
   static {
      CHARACTER_TYPES.add(LITERAL);
      CHARACTER_TYPES.add(PLAIN_LITERAL);
      CHARACTER_TYPES.add(XML_LITERAL);
      CHARACTER_TYPES.add(STRING);
      CHARACTER_TYPES.add(NORMALIZED_STRING);
   }

   public static final List<String> NUMERIC_TYPES = new ArrayList<String>();
   static {
      NUMERIC_TYPES.add(INTEGER);
      NUMERIC_TYPES.add(LONG);
      NUMERIC_TYPES.add(INT);
      NUMERIC_TYPES.add(SHORT);
      NUMERIC_TYPES.add(BYTE);
      NUMERIC_TYPES.add(DECIMAL);
      NUMERIC_TYPES.add(FLOAT);
      NUMERIC_TYPES.add(DOUBLE);
      NUMERIC_TYPES.add(NON_POSITIVE_INTEGER);
      NUMERIC_TYPES.add(NEGATIVE_INTEGER);
      NUMERIC_TYPES.add(NON_NEGATIVE_INTEGER);
      NUMERIC_TYPES.add(UNSIGNED_LONG);
      NUMERIC_TYPES.add(UNSIGNED_INT);
      NUMERIC_TYPES.add(UNSIGNED_SHORT);
      NUMERIC_TYPES.add(UNSIGNED_BYTE);
      NUMERIC_TYPES.add(POSITIVE_INTEGER);
   }

   public static String getShortName(String datatypeUri)
   {
      String prefix = "_";
      if (datatypeUri.contains(Namespaces.RDF)) {
         prefix = "rdf";
      }
      else if (datatypeUri.contains(Namespaces.RDFS)) {
         prefix = "rdfs";
      }
      else if (datatypeUri.contains(Namespaces.XSD)) {
         prefix = "xsd";
      }
      else if (datatypeUri.contains(Namespaces.OWL)) {
         prefix = "owl";
      }
      return prefix + ":" + datatypeUri.substring(datatypeUri.indexOf("#") + 1);
   }
}
