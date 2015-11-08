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
package com.obidea.semantika.queryanswer.result;

public interface IValue
{
   /**
    * A constant value to indicate the value is an object value.
    */
   public static final int URI = 0;

   /**
    * A constant value to indicate the value is a literal value.
    */
   public static final int LITERAL = 1;

   /**
    * Returns the value type.
    */
   public int getType();

   /**
    * Returns the string representation of this value.
    */
   public String stringValue();

   /**
    * Returns the Java object representation of this value.
    */
   public Object getObject();
}
