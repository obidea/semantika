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
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;

public interface IDocumentTarget
{
   /**
    * Determines if a writer is available from this document target.
    * 
    * @return <code>true</code> if a writer can be obtained from this document
    *         target, or <code>false</code> otherwise.
    */
   boolean isWriterAvailable();

   /**
    * Gets the writer from this document target. This method may be called
    * multiple times. Each invocation will return a new <code>Writer</code>.
    * This method should not be called if the <code>isWriterAvailable</code>
    * method returns <code>false</code>. A <code>RuntimeException</code> will be
    * thrown if it happens.
    * 
    * @return A new <code>Writer</code> from which the document target can be
    *         write.
    */
   Writer getWriter() throws IOException;

   /**
    * Determines if an output stream is available from this document target.
    * 
    * @return <code>true</code> if an output stream can be obtained, or
    *         <code>false</code> otherwise.
    */
   boolean isOutputStreamAvailable();

   /**
    * Gets the output stream from this document target. This method may be
    * called multiple times. Each invocation will return a new
    * <code>OutputStream</code>. This method should not be called if the
    * <code>isOutputStreamAvailable</code> method returns <code>false</code>. A
    * <code>RuntimeException</code> will be thrown if it happens.
    *
    * @return A new input stream from which this document source can be read.
    */
   OutputStream getOutputStream() throws IOException;

   /**
    * Gets the document target URI.
    * 
    * @return The document URI.
    */
   URI getDocumentUri();
}
