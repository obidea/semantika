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
package com.obidea.semantika.exception;

import java.io.IOException;

public class KnowledgeBaseCreationIOException extends KnowledgeBaseCreationException
{
   private static final long serialVersionUID = 629451L;

   public KnowledgeBaseCreationIOException(IOException e)
   {
      super(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
   }

   @Override
   public IOException getCause()
   {
      return (IOException) super.getCause();
   }

   /**
    * Delegates to the getMessage() method of the contained
    * <code>IOException</code>.
    * 
    * @return The message of the IOException
    */
   @Override
   public String getMessage()
   {
      return getCause().getMessage();
   }
}
