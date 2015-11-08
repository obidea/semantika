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
package com.obidea.semantika.util;

public class XmlUtils
{
   /**
    * Determines if the input character <code>c</code> is an XML name start
    * character.
    *
    * Source: http://www.w3.org/TR/xml/#NT-NameStartChar
    * 
    * @param c
    *           the input character value to test (according to UTF-8 or UTF-16)
    * @return Returns <code>true</code> if the input character is a name start
    *         character, or <code>false</code> otherwise.
    */
   public static boolean isXmlNameStartChar(int c)
   {
      return c == ':'  //$NON-NLS-1$
            || (c >= 'A' && c <= 'Z')  //$NON-NLS-1$ //$NON-NLS-2$
            || c == '_' //$NON-NLS-1$
            || (c >= 'a' && c <= 'z')  //$NON-NLS-1$ //$NON-NLS-2$
            || (c >= 0xC0 && c <= 0xD6)
            || (c >= 0xD8 && c <= 0xF6)
            || (c >= 0xF8 && c <= 0x2FF)
            || (c >= 0x370 && c <= 0x37D)
            || (c >= 0x37F && c <= 0x1FFF)
            || (c >= 0x200C && c <= 0x200D)
            || (c >= 0x2070 && c <= 0x218F)
            || (c >= 0x2C00 && c <= 0x2FEF)
            || (c >= 0x3001 && c <= 0xD7FF)
            || (c >= 0xF900 && c <= 0xFDCF)
            || (c >= 0xFDF0 && c <= 0xFFFD)
            || (c >= 0x10000 && c <= 0xEFFFF);
   }

   /**
    * Determines if the input character <code>c</code> is an XML name character.
    * 
    * Source: http://www.w3.org/TR/xml/#NT-NameChar
    *
    * @param c
    *           the input character value to test (according to UTF-8 or UTF-16)
    * @return Returns <code>true</code> if the input character is a name
    *         character, or <code>false</code> otherwise.
    */
   public static boolean isXmlNameChar(int c) {
      return isXmlNameStartChar(c)
            || c == '-' //$NON-NLS-1$
            || c == '.' //$NON-NLS-1$
            || c >= '0' && c <= '9' //$NON-NLS-1$ //$NON-NLS-2$
            || c == 0xB7
            || c >= 0x0300 && c <= 0x036F
            || c >= 0x203F && c <= 0x2040;
  }

   /**
    * Determines if the input character <code>c</code> is an NCName (Non-Colon
    * Name, i.e., an XML Name, minus the ":") start character.
    * 
    * @param c
    *           the input character value to test (according to UTF-8 or UTF-16)
    * @return Returns <code>true</code> if the input character is a NCName start
    *         character, or <code>false</code> otherwise.
    */
   public static boolean isNCNameStartChar(int c)
   {
      return c != ':' && isXmlNameStartChar(c); //$NON-NLS-1$
   }

   /**
    * Determines if the input character <code>c</code> is a NCName (Non-Colon
    * Name, i.e., an XML Name, minus the ":") character.
    *
    * @param c
    *           the input character value to test (according to UTF-8 or UTF-16)
    * @return Returns <code>true</code> if the input character is a NCName
    *         character, or <code>false</code> otherwise.
    */
   public static boolean isNCNameChar(int c)
   {
      return c != ':' && isXmlNameChar(c); //$NON-NLS-1$
   }

   /**
    * Determines if the input character sequence <code>cs</code> is a NCName
    * (Non-Colon Name). An NCName is a string which starts with an NCName start
    * character and is followed by zero or more NCName characters.
    * 
    * Source: http://www.w3.org/TR/xml-names/#NT-NCName
    * 
    * @param cs
    *           The character sequence to test.
    * @return Returns <code>true</code> if the input character sequence is a
    *         NCName or <code>false</code> otherwise.
    */
   public static boolean isNCName(CharSequence cs)
   {
      if (isEmpty(cs)) {
         return false;
      }
      int firstChar = Character.codePointAt(cs, 0);
      if (!isNCNameStartChar(firstChar)) {
         return false;
      }
      for (int i = Character.charCount(firstChar); i < cs.length();) {
         int c = Character.codePointAt(cs, i);
         if (!isNCNameChar(c)) {
            return false;
         }
         i += Character.charCount(c);
      }
      return true;
   }

   /**
    * Determines if a character sequence is a QName.
    * <p>
    * A QName is either:
    * <ul>
    * <li>an NCName (LocalName), or</li>
    * <li>an NCName followed by a colon and by another NCName
    * (PrefixName:LocalName)</li>
    * </ul>
    *
    * Source: http://www.w3.org/TR/xml-names/#NT-QName
    *
    * @param s
    *           The character sequence to test.
    * @return Returns <code>true</code> if the character sequence
    *         <code>cs</code> is a QName, or <code>false</code> otherwise.
    */
   public static boolean isQName(CharSequence s)
   {
      if (isEmpty(s)) {
         return false;
      }
      boolean foundColon = false;
      boolean inNCName = false;
      for (int i = 0; i < s.length();) {
         int c = Character.codePointAt(s, i);
         if (c == ':') { //$NON-NLS-1$
            if (foundColon) {
               return false;
            }
            foundColon = true;
            if (!inNCName) {
               return false;
            }
            inNCName = false;
         }
         else {
            if (!inNCName) {
               if (!isXmlNameStartChar(c)) {
                  return false;
               }
               inNCName = true;
            }
            else {
               if (!isXmlNameChar(c)) {
                  return false;
               }
            }
         }
         i += Character.charCount(c);
      }
      return true;
   }

   /**
    * Determines if a character sequence <code>cs</code> has a suffix that is an NCName.
    *
    * @param s
    *           The character sequence to test.
    * @return Returns <code>true</code> if the character sequence <code>cs</code> has a
    *         suffix that is an NCName, or <code>false</code> otherwise.
    */
   public static boolean hasNCNameSuffix(CharSequence cs)
   {
      return getNCNameSuffixIndex(cs) != -1;
   }


   /**
    * Gets the index of the longest NCName that is the suffix of a character
    * sequence.
    * 
    * @param cs
    *           The character sequence.
    * @return Returns the index of the longest suffix of the specified character
    *         sequence <code>cs</code> that is an NCName, or -1 if the character
    *         sequence <code>cs</code> does not have a suffix that is an NCName.
    */
   public static int getNCNameSuffixIndex(CharSequence cs)
   {
      int index = -1;
      for (int i = cs.length() - 1; i > -1; i--) {
         if (!Character.isLowSurrogate(cs.charAt(i))) {
            int c = Character.codePointAt(cs, i);
            if (isNCNameStartChar(c)) {
               index = i;
            }
            if (!isNCNameChar(c)) {
               break;
            }
         }
      }
      return index;
   }


   /**
    * Gets the longest NCName that is a suffix of a character sequence.
    * 
    * @param cs
    *           The character sequence.
    * @return Returns the string which is the longest suffix of the character
    *         sequence <code>s</code> that is an NCName, or <code>null</code> if
    *         the character sequence <code>s</code> does not have a suffix that
    *         is an NCName.
    */
   public static String getNCNameSuffix(CharSequence cs)
   {
      int localPartStartIndex = getNCNameSuffixIndex(cs);
      if (localPartStartIndex != -1) {
         return cs.toString().substring(localPartStartIndex);
      }
      else {
         return null;
      }
   }

   /**
    * Gets the part of a char sequence that is not the NCName suffix fragment
    * 
    * @param cs
    *           The character sequence.
    * @return Returns the prefix split at the last non-NCName character, or the
    *         whole input if no NCName is found.
    */
   public static String getNCNamePrefix(CharSequence cs)
   {
      int localPartStartIndex = getNCNameSuffixIndex(cs);
      if (localPartStartIndex != -1) {
         return cs.toString().substring(0, localPartStartIndex);
      }
      else {
         return cs.toString();
      }
   }

   /**
    * Determines if a character sequence is <code>null</code> or empty.
    * 
    * @param s
    *           The character sequence.
    * @return Returns <code>true</code> if the character sequence is
    *         <code>null</code> or empty, or <code>false</code> otherwise.
    */
   private static boolean isEmpty(CharSequence cs)
   {
      return cs == null || cs.length() == 0;
   }
}
