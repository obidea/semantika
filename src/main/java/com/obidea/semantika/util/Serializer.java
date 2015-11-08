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
package com.obidea.semantika.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.obidea.semantika.exception.SemantikaRuntimeException;
import com.obidea.semantika.io.FastByteArrayStream;

public class Serializer
{
   public static Object copy(Object target)
   {
      FastByteArrayStream streamer = new FastByteArrayStream();
      Object toReturn = null;
      try {
         ObjectOutputStream out = new ObjectOutputStream(streamer.getOutputStream());
         out.writeObject(target);
         out.flush();
         out.close();
         
         ObjectInputStream in = new ObjectInputStream(streamer.getInputStream());
         toReturn = in.readObject();
      }
      catch (IOException e) {
         final String msg = String.format("Error during object serialization: %s", target);
         throw new SemantikaRuntimeException(msg, e);
      }
      catch (ClassNotFoundException e) {
         final String msg = String.format("Error during object deserialization: %s", target);
         throw new SemantikaRuntimeException(msg, e);
      }
      return toReturn;
   }
}
