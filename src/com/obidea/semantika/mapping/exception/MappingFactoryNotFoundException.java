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

import java.net.URI;

import com.obidea.semantika.exception.NotFoundException;

public class MappingFactoryNotFoundException extends NotFoundException
{
   private static final long serialVersionUID = 629451L;

   private URI mDocumentUri;

   public MappingFactoryNotFoundException(URI documentUri)
   {
      super("[MappingFactory]"); // $NON-NLS-1$
      mDocumentUri = documentUri;
   }

   @Override
   public String getMessage()
   {
      return "Unable to find a suitable factory to load mapping set from document: " + mDocumentUri; //$NON-NLS-1$
   }
}
