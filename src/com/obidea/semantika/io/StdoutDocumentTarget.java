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

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;

import com.obidea.semantika.exception.IllegalOperationException;

public class StdoutDocumentTarget implements IDocumentTarget
{
   @Override
   public boolean isWriterAvailable()
   {
      return false;
   }

   @Override
   public Writer getWriter() throws IOException
   {
      throw new IllegalOperationException("Output writer is not available"); //$NON-NLS-1$
   }

   @Override
   public boolean isOutputStreamAvailable()
   {
      return true;
   }

   @Override
   public OutputStream getOutputStream() throws IOException
   {
      return new OutputStream()
      {
         @Override
         public void write(int b) throws IOException
         {
            System.out.write(b);
         }

         @Override
         public void close() throws IOException
         {
            // NO-OP
         }
      };
   }

   @Override
   public URI getDocumentUri()
   {
      throw new IllegalOperationException("Document URI is not available"); //$NON-NLS-1$
   }
}
