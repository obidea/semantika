/*
 * Copyright (c) 2013-2015 Josef Hardi <josef.hardi@gmail.com>
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;

import org.semanticweb.owlapi.model.OWLRuntimeException;

import com.obidea.semantika.exception.IllegalOperationException;

public class StreamDocumentSource implements IDocumentSource
{
   private static int counter = 0;

   private final URI mDocumentUri;

   private byte[] mBuffer;

   public StreamDocumentSource(InputStream is)
   {
      this(is, getNextDocumentUri());
   }

   protected static synchronized URI getNextDocumentUri()
   {
      counter = counter + 1;
      return URI.create("inputstream:" + counter); //$NON-NLS-1$
   }

   /**
    * Constructs an input source which will read an ontology from a
    * representation from the specified stream.
    * 
    * @param stream
    *           The stream that the ontology representation will be read from.
    * @param documentUri
    *           The document URI
    */
   public StreamDocumentSource(InputStream stream, URI documentUri)
   {
      mDocumentUri = documentUri;
      readIntoBuffer(stream);
   }

   /**
    * Reads all the bytes from the specified stream into a temporary buffer,
    * which is necessary because we may need to access the input stream more
    * than once. In other words, this method caches the input stream.
    * 
    * @param stream
    *           The stream to be cached
    */
   private void readIntoBuffer(InputStream reader)
   {
      try {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         final int length = 100000;
         byte[] tempBuffer = new byte[length];
         int read = 0;
         do {
            read = reader.read(tempBuffer, 0, length);
            if (read > 0) {
               bos.write(tempBuffer, 0, read);
            }
         }
         while (read > 0);
         mBuffer = bos.toByteArray();
      }
      catch (IOException e) {
         throw new OWLRuntimeException(e);
      }
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
      return true;
   }

   @Override
   public InputStream getInputStream()
   {
      return new ByteArrayInputStream(mBuffer);
   }

   @Override
   public URI getDocumentUri()
   {
      return mDocumentUri;
   }
}
