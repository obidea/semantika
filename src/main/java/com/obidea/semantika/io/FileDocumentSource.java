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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;

public class FileDocumentSource implements IDocumentSource
{
   private final File mFile;

   public FileDocumentSource(File file)
   {
      mFile = file;
   }

   /**
    * Gets the URI that points to this file document.
    * 
    * @return a URI
    */
   public URI getDocumentUri()
   {
      return mFile.toURI();
   }

   @Override
   public boolean isReaderAvailable()
   {
      return true;
   }

   /**
    * Get the reader from this document source.
    * 
    * @return a new <code>Reader</code> which the input file can be read from.
    */
   public Reader getReader() throws IOException
   {
      return new InputStreamReader(getInputStream(), "UTF-8"); //$NON-NLS-1$
   }

   @Override
   public boolean isInputStreamAvailable()
   {
      return true;
   }

   /**
    * Get the input stream from this document source.
    * 
    * @return a new <code>InputStream</code> which the input file can be read
    *         from.
    */
   public InputStream getInputStream() throws IOException
   {
      return new BufferedInputStream(new FileInputStream(mFile));
   }
}
