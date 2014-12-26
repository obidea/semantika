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
package com.obidea.semantika.expression.base;

/**
 * Represent the constant literal symbol.
 */
public interface ILiteral extends IConstant
{
   /**
    * Returns the literal value. The value associated with a typed literal is
    * found by applying the lexical-to-value mapping associated with the
    * datatype URI to the lexical form.
    * 
    * @return the literal value.
    */
   public Object getValue();

   /**
    * Returns the language tag of this constant. Only available if
    * the constant uses the plain literal type.
    *  
    * @return the language tag.
    */
   public String getLanguageTag();
}
