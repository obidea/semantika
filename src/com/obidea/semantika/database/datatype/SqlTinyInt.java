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
package com.obidea.semantika.database.datatype;

import java.sql.Types;

import com.obidea.semantika.datatype.exception.InvalidLexicalFormException;

public class SqlTinyInt extends AbstractNumericType<Byte>
{
   private static final SqlTinyInt mInstance;

   static {
      mInstance = new SqlTinyInt();
   }

   private SqlTinyInt()
   {
      super("TINYINT");
   }

   public static SqlTinyInt getInstance()
   {
      return mInstance;
   }

   @Override
   protected Byte parseLexicalForm(String lexicalForm) throws InvalidLexicalFormException
   {
      try {
         return Byte.parseByte(lexicalForm);
      }
      catch (NumberFormatException e) {
         throw new InvalidLexicalFormException(getName(), lexicalForm, e);
      }
   }

   @Override
   public int getType()
   {
      return Types.TINYINT;
   }
}