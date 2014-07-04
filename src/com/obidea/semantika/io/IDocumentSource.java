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
package com.obidea.semantika.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;

public interface IDocumentSource
{
   /**
    * Determines if a reader is available from this document source.
    * 
    * @return <code>true</code> if a reader can be obtained from this document
    *         source, or <code>false</code> otherwise.
    */
   boolean isReaderAvailable();

   /**
    * Gets the reader from this document source. This method may be called
    * multiple times. Each invocation will return a new <code>Reader</code>.
    * This method should not be called if the <code>isReaderAvailable</code>
    * method returns false. A <code>RuntimeException</code> will be thrown if it
    * happens.
    * 
    * @return A new <code>Reader</code> from which the document source can be
    *         read.
    */
   Reader getReader() throws IOException;

   /**
    * Determines if an input stream is available from this document source.
    * 
    * @return <code>true</code> if an input stream can be obtained, or
    *         <code>false</code> otherwise.
    */
   boolean isInputStreamAvailable();

   /**
    * Gets the input stream from this document source. This method may be called
    * multiple times. Each invocation will return a new input stream. This
    * method should not be called if the <code>isInputStreamAvailable</code>
    * method returns <code>false</code>.
    * 
    * @return A new input stream from which this document source can be read.
    */
   InputStream getInputStream() throws IOException;

   /**
    * Gets the document URI.
    * 
    * @return The document URI.
    */
   URI getDocumentUri();
}
