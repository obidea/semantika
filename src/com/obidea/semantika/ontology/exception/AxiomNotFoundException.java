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
package com.obidea.semantika.ontology.exception;

import com.obidea.semantika.exception.NotFoundException;

public class AxiomNotFoundException extends NotFoundException
{
   private static final long serialVersionUID = 629451L;

   private int mLineNumber = -1;
   private int mColumnNumber = -1;

   public AxiomNotFoundException(String axiom)
   {
      super(axiom);
   }

   public AxiomNotFoundException(String axiom, int lineNumber, int columnNumber)
   {
      super(axiom);
      mLineNumber = lineNumber;
      mColumnNumber = columnNumber;
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
