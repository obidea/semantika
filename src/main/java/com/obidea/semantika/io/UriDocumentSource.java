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
package com.obidea.semantika.io;

import java.io.InputStream;
import java.io.Reader;
import java.net.URI;

import com.obidea.semantika.exception.IllegalOperationException;


public class UriDocumentSource implements IDocumentSource
{
   private final URI mDocumentUri;

   public UriDocumentSource(URI documentUri)
   {
      mDocumentUri = documentUri;
   }

   @Override
   public boolean isReaderAvailable()
   {
      return false;
   }

   @Override
   public Reader getReader()
   {
      throw new IllegalOperationException("Reader is not available"); //$NON-NLS-1$
   }

   @Override
   public boolean isInputStreamAvailable()
   {
      return false;
   }

   @Override
   public InputStream getInputStream()
   {
      throw new IllegalOperationException("Input stream is not available"); //$NON-NLS-1$
   }

   @Override
   public URI getDocumentUri()
   {
      return mDocumentUri;
   }
}
