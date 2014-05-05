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
package com.obidea.semantika.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestUtils
{
   public static byte[] md5(String text)
   {
      return digest(getMd5Digest(), text);
   }

   public static String md5Hex(String text)
   {
      StringBuilder sb = new StringBuilder();
      for (byte b : digest(getMd5Digest(), text)) {
         sb.append(Integer.toHexString((int) (b & 0xff)));
      }
      return sb.toString();
   }

   public static String md5(File file) throws IOException
   {
      return digest(getMd5Digest(), file).toString();
   }

   public static String md5Hex(File file) throws IOException
   {
      StringBuilder sb = new StringBuilder();
      for (byte b : digest(getMd5Digest(), file)) {
         sb.append(Integer.toHexString((int) (b & 0xff)));
      }
      return sb.toString();
   }

   private static byte[] digest(MessageDigest md, String text)
   {
      md.update(getBytesUtf8(text));
      return md.digest();
   }

   private static byte[] digest(MessageDigest md, File file) throws IOException
   {
      InputStream is = new FileInputStream(file);
      try {
         byte[] buffer = new byte[1024];
         int read = is.read(buffer, 0, 1024);
         while (read > -1) {
            getMd5Digest().update(buffer, 0, read);
            read = is.read(buffer, 0, 1024);
         }
         return md.digest();
      }
      finally {
         if (is != null) {
            is.close();
         }
      }
   }

   private static MessageDigest getMd5Digest()
   {
      return getDigest("MD5");
   }

   private static MessageDigest getDigest(String algorithm)
   {
      try {
         return MessageDigest.getInstance(algorithm);
      }
      catch (NoSuchAlgorithmException e) {
         throw new RuntimeException(e.getMessage());
      }
   }

   private static byte[] getBytesUtf8(String text)
   {
      return StringUtils.getBytesUtf8(text);
   }
}
