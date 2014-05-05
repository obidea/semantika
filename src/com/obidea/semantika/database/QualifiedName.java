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
package com.obidea.semantika.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class QualifiedName implements Iterable<String>
{
   private static final String DEFAULT_SEPARATOR = ".";

   private List<String> mElements = new ArrayList<String>();

   public QualifiedName(String... elements)
   {
      mElements.addAll(Arrays.asList(elements));
   }

   public QualifiedName(String separator, String... elements)
   {
      mElements.addAll(Arrays.asList(elements));
   }

   /**
    * Creates a new qualified name by parsing the given string. When iterating
    * the QName object, the object name always comes first followed by the
    * namespace components.
    * <p>
    * <b>For example</b>:
    * 
    * <pre>
    * QualifiedName qname = QualifiedName.create("moo.foo.bar"); // 'bar' is the object name
    * 
    * String firstElement = qname.getComponent(0); // returns 'bar'
    * String secondElement = qname.getComponent(1); // returns 'foo'
    * String thirdElement = qname.getComponent(2); // returns 'moo'
    * String fourthElement = qname.getComponent(3) // returns null
    * </pre>
    * 
    * @param str
    *           The input string. The default separator is a period.
    * @return The qualified name object.
    */
   public static QualifiedName create(String str)
   {
      String[] elements = parse(str, Pattern.quote(DEFAULT_SEPARATOR));
      return new QualifiedName(elements);
   }

   /**
    * Creates a new qualified name by parsing the given string with a custom
    * separator, When iterating the QName object, the object name always comes
    * first followed by the namespace components.
    * <p>
    * <b>For example</b>:
    * <pre>
    * QualifiedName qname = QualifiedName.create("moo:foo:bar", ":"); // 'bar is the object name
    * 
    * String firstElement = qname.getComponent(0); // returns 'bar'
    * String secondElement = qname.getComponent(1); // returns 'foo'
    * String thirdElement = qname.getComponent(2); // returns 'moo'
    * String fourthElement = qname.getComponent(3) // returns null
    * </pre>
    * 
    * @param str
    *           The input string.
    * @param separator
    *           User defined separator character.
    * @return The qualified name object.
    */
   public static QualifiedName create(String str, String separator)
   {
      String[] elements = parse(str, Pattern.quote(separator));
      return new QualifiedName(separator, elements);
   }

   public String getIdentifier(int index)
   {
      try {
         return mElements.get(index);
      }
      catch (IndexOutOfBoundsException e) {
         return null;
      }
   }

   private static String[] parse(String text, String regex)
   {
      final String[] tokens = text.split(regex);

      int n = tokens.length;
      String[] toReturn = new String[n];
      for (int i = 0; i < tokens.length; i++) {
         toReturn[tokens.length-1-i] = tokens[i];
      }
      return toReturn;
   }

   @Override
   public Iterator<String> iterator()
   {
      List<String> copyElements = new ArrayList<String>(mElements);
      Collections.reverse(copyElements);
      return copyElements.iterator();
   }
}
