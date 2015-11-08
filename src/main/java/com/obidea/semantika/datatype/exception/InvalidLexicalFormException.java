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
package com.obidea.semantika.datatype.exception;

import static java.lang.String.format;

public class InvalidLexicalFormException extends DataTypeReasonerException
{
   private static final long serialVersionUID = 629451L;

   private final String mDatatype;

   private final String mValue;

   public InvalidLexicalFormException(String datatype, String value)
   {
      super(format("The string '%s' is not in the lexical space of \"%s\" type", value, datatype)); //$NON-NLS-1$
      mDatatype = datatype;
      mValue = value;
   }

   public InvalidLexicalFormException(String datatype, String value, Throwable cause)
   {
      this(datatype, value);
      initCause(cause);
   }

   public String getDatatype()
   {
      return mDatatype;
   }

   public String getValue()
   {
      return mValue;
   }
}
