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
package com.obidea.semantika.database.datatype;

import java.sql.Time;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class SqlTime extends AbstractDateTimeType<Time>
{
   private static final SqlTime mInstance;

   static {
      mInstance = new SqlTime();
   }

   private SqlTime()
   {
      super("TIME");
   }

   public static SqlTime getInstance()
   {
      return mInstance;
   }

   @Override
   protected Time parseLexicalForm(String lexicalForm)
   {
      final SimpleDateFormat df = new SimpleDateFormat();
      try {
         java.util.Date date = df.parse(lexicalForm);
         long time = date.getTime();
         return new Time(time);
      }
      catch (ParseException e) {
         throw new IllegalArgumentException("Invalid input for SQL Time type", e);
      }
   }

   @Override
   public int getType()
   {
      return Types.TIME;
   }
}
