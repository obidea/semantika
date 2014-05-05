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
package com.obidea.semantika.mapping.exception;

public class MappingParserException extends MappingException
{
   private static final long serialVersionUID = 629451L;

   private int mLineNumber;

   private int mColumnNumber;

   public MappingParserException()
   {
      mLineNumber = -1;
   }

   public MappingParserException(String message)
   {
      super(message);
      mLineNumber = -1;
   }

   public MappingParserException(String message, Throwable cause)
   {
      super(message, cause);
      mLineNumber = -1;
   }

   public MappingParserException(Throwable cause)
   {
      super(cause);
      mLineNumber = -1;
   }

   public MappingParserException(String message, int lineNumber, int columnNumber)
   {
      super(message);
      mLineNumber = lineNumber;
      mColumnNumber = columnNumber;
   }

   public MappingParserException(Throwable cause, int lineNumber, int columnNumber)
   {
      super(cause);
      mLineNumber = lineNumber;
      mColumnNumber = columnNumber;
   }

   /**
    * Gets the line number of the line that the parser was parsing when the
    * error occurred.
    * 
    * @return A positive integer which represents the line number, or -1 if the
    *         line number could not be determined.
    */
   public int getLineNumber()
   {
      return mLineNumber;
   }

   public int getColumnNumber()
   {
      return mColumnNumber;
   }

   @Override
   public String getMessage()
   {
      String location = ""; //$NON-NLS-1$
      if (mLineNumber != -1 && mColumnNumber != -1) {
         location = String.format("(at line %s column %s)", mLineNumber, mColumnNumber); //$NON-NLS-1$
      }
      return super.getMessage() + " " + location; //$NON-NLS-1$
   }
}
