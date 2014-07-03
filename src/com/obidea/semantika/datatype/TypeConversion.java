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

import static java.lang.String.format;

import org.slf4j.Logger;

import com.obidea.semantika.util.LogUtils;

/**
 * Utility class to verify conversion between two different types. The implementation follows:
 * {@link http://www.w3.org/TR/2002/WD-xquery-operators-20020816/#casting-from-primitive-to-primitive}.
 * <p>
 * Extra notes:
 * <ol>
 * <li>Conversion from xsd:string to rdf:Literal is NOT possible because RDF Literal is not a type.
 *    The reverse behaves the same.</li>
 * <li>Conversion from xsd:string to rdf:PlainLiteral is NOT possible because the strict ruling of
 *    RDF Plain Literal requires at least one @ (U+0040) character. This implementation takes a general
 *    view that a value with XSD String type is just a sequence of characters without any symbolic
 *    purpose. Thus, although an XSD-String value has @ character, it doesn't serve as a language tag
 *    indicator as defined in RDF Plain Literal. However, the reverse is possible with a non-reversible
 *    effect such that the converted value won't give any meaning to @ character once it is converted
 *    to XSD String. (Reference: {@link
 *    http://www.w3.org/TR/rdf-plain-literal/#Definition_of_the_rdf:PlainLiteral_Datatype}).</li>
 * <li>Conversion from xsd:string to rdf:XMLLiteral is possible because rdf:XMLLiteral is a type. The
 *    reverse behaves the same. (Reference: {@link
 *    http://www.w3.org/TR/2004/REC-rdf-concepts-20040210/#section-XMLLiteral}).</li>
 * </ol>
 */
public class TypeConversion
{
   private static final int YES = 1;
   private static final int NOT = 0;
   private static final int MAY = 2;
   private static final int LOS = 3; // means precision loss due to conversion

   private static final int[][] mCastingTable;
   static {
      mCastingTable = new int[][] {
            /*          str, flt, dbl, dec, dur, dT,  tim, dat, YM,  Yr,  MD,  Day, Mon, Bool,b64, hxB, aURI,QN,  NOT, dTS, INT, long,int, srt, byte,lit, Plit,Xlit,nPI, NI,  nNI, uL,  uI,  uS,  uB,  PI */
            /* str */ { YES, MAY, MAY, MAY, MAY, MAY, MAY, MAY, MAY, MAY, MAY, MAY, MAY, MAY, NOT, NOT, MAY, NOT, MAY, MAY, MAY, MAY, MAY, MAY, MAY, NOT, NOT, YES, MAY, MAY, MAY, MAY, MAY, MAY, MAY, MAY },
            /* flt */ { YES, YES, YES, MAY, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, MAY, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT },
            /* dbl */ { YES, YES, YES, MAY, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, MAY, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT },
            /* dec */ { YES, YES, YES, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, MAY, NOT, NOT, NOT, NOT, NOT, NOT, YES, YES, YES, YES, YES, NOT, NOT, NOT, YES, YES, YES, YES, YES, YES, YES, YES },
            /* dur */ { YES, NOT, NOT, NOT, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT },
            /* dT  */ { YES, NOT, NOT, NOT, NOT, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT },
            /* tim */ { YES, NOT, NOT, NOT, NOT, NOT, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT },
            /* dat */ { YES, NOT, NOT, NOT, NOT, NOT, NOT, YES, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT },
            /* YM  */ { YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT },
            /* Yr  */ { YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT },
            /* MD  */ { YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT },
            /* Day */ { YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT },
            /* Mon */ { YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT },
            /* Bool*/ { YES, YES, YES, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, YES, NOT, NOT, NOT, NOT, NOT, NOT, YES, YES, YES, YES, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, YES },
            /* b64 */ { YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, MAY, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT },
            /* hxB */ { YES, MAY, MAY, MAY, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, MAY, NOT, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT },
            /* aURI*/ { YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT },
            /* QN  */ { NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT },
            /* NOT */ { YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT },
            /* dTS */ { YES, NOT, NOT, NOT, NOT, YES, YES, YES, YES, YES, YES, YES, YES, NOT, NOT, NOT, NOT, NOT, NOT, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT },
            /* INT */ { YES, YES, YES, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, MAY, NOT, NOT, NOT, NOT, NOT, NOT, YES, LOS, LOS, LOS, LOS, NOT, NOT, NOT, MAY, MAY, MAY, MAY, MAY, MAY, MAY, MAY },
            /* long*/ { YES, YES, YES, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, MAY, NOT, NOT, NOT, NOT, NOT, NOT, YES, YES, LOS, LOS, LOS, NOT, NOT, NOT, MAY, MAY, MAY, MAY, MAY, MAY, MAY, MAY },
            /* int */ { YES, YES, YES, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, MAY, NOT, NOT, NOT, NOT, NOT, NOT, YES, YES, YES, LOS, LOS, NOT, NOT, NOT, MAY, MAY, MAY, MAY, MAY, MAY, MAY, MAY },
            /* srt */ { YES, YES, YES, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, MAY, NOT, NOT, NOT, NOT, NOT, NOT, YES, YES, YES, YES, LOS, NOT, NOT, NOT, MAY, MAY, MAY, MAY, MAY, MAY, MAY, MAY },
            /* byte*/ { YES, YES, YES, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, MAY, NOT, NOT, NOT, NOT, NOT, NOT, YES, YES, YES, YES, YES, NOT, NOT, NOT, MAY, MAY, MAY, MAY, MAY, MAY, MAY, MAY },
            /* lit */ { NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT },
            /* Plit*/ { YES, MAY, MAY, MAY, NOT, MAY, MAY, MAY, NOT, NOT, NOT, NOT, NOT, MAY, NOT, NOT, NOT, NOT, NOT, NOT, MAY, MAY, MAY, MAY, MAY, NOT, YES, NOT, MAY, MAY, MAY, MAY, MAY, MAY, MAY, MAY },
            /* Xlit*/ { YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT },
            /* nPI */ { YES, YES, YES, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, YES, LOS, LOS, LOS, LOS, NOT, NOT, NOT, YES, LOS, NOT, NOT, NOT, NOT, NOT, NOT },
            /* NI  */ { YES, YES, YES, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, YES, LOS, LOS, LOS, LOS, NOT, NOT, NOT, LOS, YES, NOT, NOT, NOT, NOT, NOT, NOT },
            /* nNI */ { YES, YES, YES, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, YES, LOS, LOS, LOS, LOS, NOT, NOT, NOT, NOT, NOT, YES, LOS, LOS, LOS, LOS, YES },
            /* uL  */ { YES, YES, YES, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, YES, YES, LOS, LOS, LOS, NOT, NOT, NOT, NOT, NOT, YES, YES, LOS, LOS, LOS, YES },
            /* uI  */ { YES, YES, YES, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, YES, YES, YES, LOS, LOS, NOT, NOT, NOT, NOT, NOT, YES, YES, YES, LOS, LOS, YES },
            /* uS  */ { YES, YES, YES, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, YES, YES, YES, YES, LOS, NOT, NOT, NOT, NOT, NOT, YES, YES, YES, YES, LOS, YES },
            /* uB  */ { YES, YES, YES, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, YES, YES, YES, YES, YES, NOT, NOT, NOT, NOT, NOT, YES, YES, YES, YES, YES, YES },
            /* PI  */ { YES, YES, YES, YES, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, NOT, MAY, NOT, NOT, NOT, NOT, NOT, NOT, YES, LOS, LOS, LOS, LOS, NOT, NOT, NOT, NOT, NOT, YES, LOS, LOS, LOS, LOS, YES },
           };
   }

   private static final Logger LOG = LogUtils.createLogger("semantika.datatype"); //$NON-NLS-1$

   /**
    * Verify if the <code>source</code> type can be converted to the
    * <code>target</code> type.
    * 
    * @param source
    *           the original source type.
    * @param target
    *           the target conversion type.
    * @return returns <code>true</code> if the source type can be or might be
    *         converted to the target type, or <code>false</code> otherwise.
    */
   public static boolean verify(final AbstractXmlType<?> source, final AbstractXmlType<?> target)
   {
      int from = getTypeConstant(source);
      int to = getTypeConstant(target);
      int indicator = mCastingTable[from][to];
      switch (indicator) {
         case YES:
            return true;
         case MAY :
            String msgMay = format(
                  "Conversion from \"%s\" to \"%s\" is subjected to the restrictions in target type", //$NON-NLS-1$
                  source, target);
            LOG.warn(msgMay);
            return true;
         case LOS :
            String msgLos = format(
                  "Conversion from \"%s\" to \"%s\" can result precision loss", //$NON-NLS-1$
                  source, target);
            LOG.warn(msgLos);
            return true;
         case NOT:
            return false;
         default:
            return false;
      }
   }

   private static int getTypeConstant(AbstractXmlType<?> input)
   {
      return input.isPrimitive() 
            ? input.getType() 
            : getTypeConstant((AbstractXmlType<?>) input.getPrimitiveDatatype());
   }
}
