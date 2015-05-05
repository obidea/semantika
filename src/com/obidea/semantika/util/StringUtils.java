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
package com.obidea.semantika.util;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

public final class StringUtils
{
   /**
    * System specific line separator character.
    */
   public static final String NEWLINE = System.getProperty("line.separator"); //$NON-NLS-1$

   private static final Pattern containsWhitespacePattern = Pattern.compile(".*\\s.*"); //$NON-NLS-1$
   private static final Pattern isAllWhitespacePattern = Pattern.compile("^\\s*$"); //$NON-NLS-1$

   public static boolean isEmpty(final String text)
   {
      return text == null || text.isEmpty() || isAllWhitespacePattern.matcher(text).matches();
   }

   public static boolean containsWhitespace(final String text)
   {
      if (text == null) {
         return false;
      }
      else {
         return containsWhitespacePattern.matcher(text).matches();
      }
   }

   public static String toLowerCase(final String text)
   {
      return text.toLowerCase();
   }

   public static String toUpperCase(final String text)
   {
      return text.toUpperCase();
   }

   /**
    * Replaces whitespace in the text with underscore.
    */
   public static String useUnderscore(final String text)
   {
      return text.replaceAll("\\s+", "_"); //$NON-NLS-1$
   }

   /**
    * Replaces whitespace in the text with plus sign.
    */
   public static String usePlus(final String text)
   {
      return text.replaceAll("\\s+", "+"); //$NON-NLS-1$
   }

   public static byte[] getBytesUnchecked(String text, String charsetName)
   {
      if (text == null) {
         return null;
      }
      try {
         return text.getBytes(charsetName);
      }
      catch (UnsupportedEncodingException e) {
         throw new IllegalStateException(charsetName + ": " + e); //$NON-NLS-1$
      }
   }

   public static byte[] getBytesUtf8(String string)
   {
      return StringUtils.getBytesUnchecked(string, "UTF-8"); //$NON-NLS-1$
   }

   private StringUtils()
   {
      // NO-OP: Prevent instantiation
   }
}
