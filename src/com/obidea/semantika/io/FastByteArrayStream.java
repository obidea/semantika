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
import java.io.OutputStream;

public class FastByteArrayStream
{
   private byte[] mBuffer;
   private int mPointer;

   public FastByteArrayStream()
   {
      this(5 * 1024);
   }

   public FastByteArrayStream(int bufferSize)
   {
      mBuffer = new byte[bufferSize];
      mPointer = 0;
   }

   public OutputStream getOutputStream()
   {
      return new OutputStream()
      {
         @Override
         public void write(int b) throws IOException
         {
            verifyBufferSize(mPointer + 1);
            mBuffer[mPointer++] = (byte) b;
         }
         
         @Override
         public void write(byte[] b) {
            verifyBufferSize(mPointer + b.length);
            System.arraycopy(b, 0, mBuffer, mPointer, b.length);
            mPointer += b.length;
         }
         
         @Override
         public void write(byte[] b, int offset, int length)
         {
            verifyBufferSize(mPointer + length);
            System.arraycopy(b, offset, mBuffer, mPointer, length);
            mPointer += length;
         }
         
         private void verifyBufferSize(int size)
         {
            if (size > mBuffer.length) {
               byte[] tmp = mBuffer;
               mBuffer = new byte[Math.max(size, 2 * mBuffer.length)];
               System.arraycopy(tmp, 0, mBuffer, 0, tmp.length);
               tmp = null; // clean
            }
         }
      };
   }

   public InputStream getInputStream()
   {
      return new InputStream()
      {
         private int mPos = 0;

         @Override
         public int read() throws IOException
         {
            return (mPos < mPointer) ? mBuffer[mPos++] & 0xff : -1;
         }
         
         @Override
         public int read(byte[] b, int offset, int length)
         {
            if (mPos >= mPointer) {
               return -1;
            }
            if ((mPos + length) > mPointer) {
               length = mPointer - mPos;
            }
            System.arraycopy(mBuffer, mPos, b, offset, length);
            mPos += length;
            return length;
         }
         
         @Override
         public long skip(long n)
         {
            if ((mPos + n) > mPointer) {
               n = mPointer - mPos;
            }
            if (n < 0) {
               return 0;
            }
            mPos += n;
            return n;
         }
         
         @Override
         public int available()
         {
            return mPointer - mPos;
         }
      };
   }
}
