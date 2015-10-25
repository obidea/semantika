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
package com.obidea.semantika.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import com.obidea.semantika.app.ApplicationFactory;
import com.obidea.semantika.exception.ConfigurationException;
import com.obidea.semantika.exception.ResourceNotFoundException;

public final class ConfigHelper
{
   public static Properties getConfigProperties(final String path) throws ConfigurationException
   {
      try {
         Properties properties = new Properties();
         properties.load(getResourceStream(path));
         return properties;
      }
      catch (IOException e) {
         throw new ConfigurationException("Unable to load properties from specified config file: " + path, e); //$NON-NLS-1$
      }
   }

   /**
    * Open an <code>InputStream</code> given the <code>url</code> string. The
    * method will first make a call to
    * <code>locateResource(java.lang.String)</code> to get the appropriate URL
    * object and then
    * <code>java.net.URL.openStream() is called to obtain the stream.
    * 
    * @param url
    *           The URL string representing the resource location.
    * @return An input stream to the requested resource.
    */
   public static InputStream getResourceStream(final String url) throws ConfigurationException
   {
      final URL urlObj = ConfigHelper.locateResource(url);

      if (urlObj == null) {
         throw new ResourceNotFoundException(url + " is missing"); //$NON-NLS-1$
      }

      try {
         return urlObj.openStream();
      }
      catch (IOException e) {
         throw new ConfigurationException("Unable to open stream: " + url, e); //$NON-NLS-1$
      }
   }

   public static InputStream getResourceInputStream(String resource) throws ConfigurationException
   {
      String stripped = resource.startsWith("/") ? resource.substring(1) : resource; //$NON-NLS-1$

      InputStream stream = null;

      // First trial: through the current context class-loader.
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      if (cl != null) {
         stream = cl.getResourceAsStream(stripped);
      }
      // Second trial: through this class's class-loader.
      if (stream == null) {
         stream = ApplicationFactory.class.getClassLoader().getResourceAsStream(stripped);
      }
      // Third trial: through the absolute location of the resource.
      if (stream == null) {
         stream = ApplicationFactory.class.getResourceAsStream(resource);
      }
      // Fourth trial: through the absolute location of the resource using file input stream
      if (stream == null) {
         stream = getInputStreamFromAbsolutePath(resource);
      }
      // Give up
      if (stream == null) {
         throw new ResourceNotFoundException(resource + " is missing"); //$NON-NLS-1$
      }
      return stream;
   }

   public static InputStream getUserResourceInputStream(String resource) throws ConfigurationException
   {
      boolean hasLeadingSlash = resource.startsWith("/"); //$NON-NLS-1$
      String stripped = hasLeadingSlash ? resource.substring(1) : resource;

      InputStream stream = null;

      // First trial: through the current context class-loader.
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      if (cl != null) {
         stream = cl.getResourceAsStream(resource);
         if (stream == null && hasLeadingSlash) {
            stream = cl.getResourceAsStream(stripped);
         }
      }
      // Second trial: through this class's class-loader.
      if (stream == null) {
         stream = ApplicationFactory.class.getClassLoader().getResourceAsStream(stripped);
      }
      // Third trial: through the absolute location of the resource.
      if (stream == null) {
         stream = ApplicationFactory.class.getResourceAsStream(resource);
      }
      // Fourth trial: through the absolute location of the resource using file input stream
      if (stream == null) {
         stream = getInputStreamFromAbsolutePath(resource);
      }
      // Give up
      if (stream == null) {
         throw new ResourceNotFoundException(resource + " is missing"); //$NON-NLS-1$
      }
      return stream;
   }

   public static InputStream getInputStreamFromAbsolutePath(String resource)
   {
      try {
         return new FileInputStream(resource);
      }
      catch (IOException e) {
         return null;
      }
   }

   /**
    * Try to locate a local URL given the input <code>path</code> string. The
    * first attempt assumes that the incoming path is a valid URL string (e.g.,
    * http://, file://, etc.). If this does not work then the next attempts is
    * to try to locate the path as a Java system resource.
    * 
    * @param path
    *           The path representing the resource location.
    * @return An appropriate URL object, or <code>null</code>.
    */
   public static URL locateResource(final String path)
   {
      try {
         return new URL(path);
      }
      catch (MalformedURLException e) {
         return ConfigHelper.findAsResource(path);
      }
   }

   /**
    * Try to locate a local URL from the given <code>path</code> string. This
    * method only attempts to locate the path as a Java system resource.
    * 
    * @param path
    *           The path representing the resource location
    * @return An appropriate URL object, or <code>null</code>
    */
   public static URL findAsResource(final String path)
   {
      URL url = null;

      // First, try to locate this resource through the current context class
      // loader.
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      if (cl != null) {
         url = cl.getResource(path);
      }
      if (url != null) {
         return url;
      }

      // Next, try to locate this resource through this class's class loader.
      url = ConfigHelper.class.getClassLoader().getResource(path);
      if (url != null) {
         return url;
      }

      // Next, try to locate this resource through the system class loader.
      url = ClassLoader.getSystemClassLoader().getResource(path);

      // Give up
      return url;
   }

   // Prevent initialization
   private ConfigHelper()
   {
      // NO-OP
   }
}
