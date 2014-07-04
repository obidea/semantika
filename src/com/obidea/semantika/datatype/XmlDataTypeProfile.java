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

import java.util.HashMap;
import java.util.Map;

import com.obidea.semantika.datatype.derived.XsdByte;
import com.obidea.semantika.datatype.derived.XsdDateTimeStamp;
import com.obidea.semantika.datatype.derived.XsdInt;
import com.obidea.semantika.datatype.derived.XsdInteger;
import com.obidea.semantika.datatype.derived.XsdLong;
import com.obidea.semantika.datatype.derived.XsdNegativeInteger;
import com.obidea.semantika.datatype.derived.XsdNonNegativeInteger;
import com.obidea.semantika.datatype.derived.XsdNonPositiveInteger;
import com.obidea.semantika.datatype.derived.XsdPositiveInteger;
import com.obidea.semantika.datatype.derived.XsdShort;
import com.obidea.semantika.datatype.derived.XsdUnsignedByte;
import com.obidea.semantika.datatype.derived.XsdUnsignedInt;
import com.obidea.semantika.datatype.derived.XsdUnsignedLong;
import com.obidea.semantika.datatype.derived.XsdUnsignedShort;
import com.obidea.semantika.datatype.exception.UnsupportedDataTypeException;
import com.obidea.semantika.datatype.primitive.RdfPlainLiteral;
import com.obidea.semantika.datatype.primitive.XsdAnyUri;
import com.obidea.semantika.datatype.primitive.XsdBoolean;
import com.obidea.semantika.datatype.primitive.XsdDate;
import com.obidea.semantika.datatype.primitive.XsdDateTime;
import com.obidea.semantika.datatype.primitive.XsdDecimal;
import com.obidea.semantika.datatype.primitive.XsdDouble;
import com.obidea.semantika.datatype.primitive.XsdFloat;
import com.obidea.semantika.datatype.primitive.XsdGDay;
import com.obidea.semantika.datatype.primitive.XsdGMonth;
import com.obidea.semantika.datatype.primitive.XsdGMonthDay;
import com.obidea.semantika.datatype.primitive.XsdGYear;
import com.obidea.semantika.datatype.primitive.XsdGYearMonth;
import com.obidea.semantika.datatype.primitive.XsdString;
import com.obidea.semantika.datatype.primitive.XsdTime;

public final class XmlDataTypeProfile
{
   private static final Map<String, AbstractXmlType<?>> coreDatatypes;

   static {
      coreDatatypes = new HashMap<String, AbstractXmlType<?>>();
      {
         coreDatatypes.put(DataType.PLAIN_LITERAL, RdfPlainLiteral.getInstance());
         coreDatatypes.put(DataType.STRING, XsdString.getInstance());
         
      // TODO: Implement later
      // coreDatatypes.put(DataType.NORMALIZED_STRING, XsdNormalizedString.getInstance());
      // coreDatatypes.put(DataType.TOKEN, XsdToken.getInstance());
      // coreDatatypes.put(DataType.LANGUAGE, XsdLanguage.getInstance());
      // coreDatatypes.put(DataType.NMToken, XsdNMToken.getInstance());
      // coreDatatypes.put(DataType.NAME, XsdName.getInstance());
      // coreDatatypes.put(DataType.NCName, XsdNCName.getInstance());
         
         coreDatatypes.put(DataType.BOOLEAN, XsdBoolean.getInstance());
         
         coreDatatypes.put(DataType.DECIMAL, XsdDecimal.getInstance());
         coreDatatypes.put(DataType.INTEGER, XsdInteger.getInstance());
         coreDatatypes.put(DataType.LONG, XsdLong.getInstance());
         coreDatatypes.put(DataType.INT, XsdInt.getInstance());
         coreDatatypes.put(DataType.SHORT, XsdShort.getInstance());
         coreDatatypes.put(DataType.BYTE, XsdByte.getInstance());
         
         coreDatatypes.put(DataType.NON_NEGATIVE_INTEGER, XsdNonNegativeInteger.getInstance());
         coreDatatypes.put(DataType.NON_POSITIVE_INTEGER, XsdNonPositiveInteger.getInstance());
         coreDatatypes.put(DataType.NEGATIVE_INTEGER, XsdNegativeInteger.getInstance());
         coreDatatypes.put(DataType.POSITIVE_INTEGER, XsdPositiveInteger.getInstance());
         coreDatatypes.put(DataType.UNSIGNED_LONG, XsdUnsignedLong.getInstance());
         coreDatatypes.put(DataType.UNSIGNED_INT, XsdUnsignedInt.getInstance());
         coreDatatypes.put(DataType.UNSIGNED_SHORT, XsdUnsignedShort.getInstance());
         coreDatatypes.put(DataType.UNSIGNED_BYTE, XsdUnsignedByte.getInstance());
         
         coreDatatypes.put(DataType.DOUBLE, XsdDouble.getInstance());
         
         coreDatatypes.put(DataType.FLOAT, XsdFloat.getInstance());
         
         coreDatatypes.put(DataType.DATE_TIME, XsdDateTime.getInstance());
         coreDatatypes.put(DataType.DATE_TIME_STAMP, XsdDateTimeStamp.getInstance());
         
         coreDatatypes.put(DataType.DATE, XsdDate.getInstance());
         coreDatatypes.put(DataType.G_YEAR_MONTH, XsdGYearMonth.getInstance());
         coreDatatypes.put(DataType.G_MONTH_DAY, XsdGMonthDay.getInstance());
         coreDatatypes.put(DataType.G_YEAR, XsdGYear.getInstance());
         coreDatatypes.put(DataType.G_MONTH, XsdGMonth.getInstance());
         coreDatatypes.put(DataType.G_DAY, XsdGDay.getInstance());
         coreDatatypes.put(DataType.TIME, XsdTime.getInstance());
         
      // coreDatatypes.put(DataType.DURATION, XsdDuration.getInstance());
         
       coreDatatypes.put(DataType.ANY_URI, XsdAnyUri.getInstance());
      }
   }

   public static AbstractXmlType<?> getXmlDatatype(String xmlType)
   {
      AbstractXmlType<?> dt = coreDatatypes.get(xmlType);
      if (dt == null) {
         throw new UnsupportedDataTypeException(xmlType);
      }
      return dt;
   }
}
