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
package com.obidea.semantika.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.obidea.semantika.exception.SemantikaRuntimeException;

/**
 * A collections of date-oriented methods.
 */
public final class DateUtils
{
   /**
    * Creates an instance of {@link java.util.Calendar} from the given
    * {@link java.util.Date} object.
    * 
    * @param date
    *           date object to convert.
    * @return a new instance of <code>Calendar</code> object.
    */
   public static Calendar toCalendar(final Date date)
   {
      final Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      return calendar;
   }

   /**
    * Creates an instance of {@link java.util.Calendar} from the given time in
    * milliseconds.
    * <p>
    * The input milliseconds value represents the specified number of
    * milliseconds since the standard base time known as "the epoch", namely
    * January 1, 1970, 00:00:00 GMT.
    * 
    * @param millis
    *           a given time corresponding to the number of milliseconds.
    * @return a new instance of <code>Calendar</code> object.
    */
   public static Calendar toCalendar(final long millis)
   {
      final Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(millis);
      return calendar;
   }

   /**
    * Creates an {@link javax.xml.datatype.XMLGregorianCalendar} from the given
    * time in {@link java.util.Date} object.
    * 
    * @param date
    *           a given time as an instance of <code>java.util.Date</code>
    * @return a new instance of <code>XMLGregorianCalendar</code> object.
    */
   public static XMLGregorianCalendar toXmlGregorianCalendar(final Date date)
   {
      return toXmlGregorianCalendar(date.getTime());
   }

   /**
    * Creates a {@link javax.xml.datatype.XMLGregorianCalendar} from the given
    * time in milliseconds.
    * <p>
    * The input milliseconds value represents the specified number of
    * milliseconds since the standard base time known as "the epoch", namely
    * January 1, 1970, 00:00:00 GMT.
    * 
    * @param milis
    *           a given time corresponding to the number of milliseconds.
    * @return a new instance of <code>XMLGregorianCalendar</code> object.
    */
   public static XMLGregorianCalendar toXmlGregorianCalendar(final long milis)
   {
      try {
         final GregorianCalendar calendar = new GregorianCalendar();
         calendar.setTimeInMillis(milis);
         return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
      }
      catch (final DatatypeConfigurationException ex) {
         throw new SemantikaRuntimeException("Unable to convert input " + milis
               + " to an XMLGregorianCalendar object");
      }
   }
}
