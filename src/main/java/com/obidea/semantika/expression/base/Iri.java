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
package com.obidea.semantika.expression.base;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.obidea.semantika.exception.SemantikaRuntimeException;
import com.obidea.semantika.util.XmlUtils;

/**
 * Represents International Resource Identifiers
 *
 * @author Josef Hardi <josef.hardi@gmail.com>
 * @since 1.8
 */
public class Iri implements Serializable
{
   private static final long serialVersionUID = 629451L;

   private String namespace;
   private String localName;

   /**
    * Creates a new IRI by concatenating the supplied namespace and local name.
    */
   protected Iri(@Nonnull String namespace, @Nullable String localName)
   {
      this.namespace = namespace;
      this.localName = localName;
   }

   /**
    * Creates an IRI from the supplied string.
    * 
    * @param iriString
    *        The string that specifies the IRI
    * @return The IRI object that has the specified string representation.
    */
   public static Iri create(@Nonnull String iriString)
   {
      requireNonNull(iriString, "iriString cannot be null");
      int index = XmlUtils.getNCNameSuffixIndex(iriString);
      if (index < 0) {
         return new Iri(iriString, "");
      }
      return new Iri(iriString.substring(0, index), iriString.substring(index));
   }

   /**
    * Creates a new IRI by concatenating the supplied namespace and local name.
    * 
    * @param namespace
    *           The first string
    * @param localName
    *           The second string
    * @return An IRI whose strings consist of namespace + localName.
    */
   public static Iri create(@Nonnull String namespace, @Nullable String localName)
   {
      requireNonNull(namespace, "namespace cannot be null");
      return new Iri(namespace, localName);
   }

   /**
    * Creates a new IRI from file location.
    * 
    * @param file
    *           the file to create the IRI from
    * @return An IRI whose string is from {@link File#toURI()}
    */
   @Nonnull
   public static Iri create(@Nonnull File file)
   {
      requireNonNull(file, "file cannot be null");
      URI fileUri = file.toURI();
      return create(fileUri);
   }

   /**
    * Creates a new IRI from URI
    * 
    * @param uri
    *           the uri to create the IRI from
    * @return An IRI whose string is from {@link URI#toString()}
    */
   @Nonnull
   public static Iri create(@Nonnull URI uri)
   {
      requireNonNull(uri, "uri cannot be null");
      String uriString = uri.toString();
      return new Iri(XmlUtils.getNCNamePrefix(uriString), XmlUtils.getNCNameSuffix(uriString));
   }

   /**
    * Creates a new IRI from URI
    *
    * @param url
    *           the url to create the IRI from
    * @return An IRI whose string is from {@link URL#toURI()}
    * @throws SemantikaRuntimeException
    *            if the URL is ill formed
    */
   public static Iri create(@Nonnull URL url)
   {
      requireNonNull(url, "url cannot be null");
      try {
         return create(url.toURI());
      }
      catch (URISyntaxException e) {
         throw new SemantikaRuntimeException(e);
      }
   }

   /**
    * Retrieves the namespace used by this IRI.
    */
   public String getNamespace()
   {
      return namespace;
   }

   /**
    * Returns the local name used by this IRI.
    */
   public String getLocalName()
   {
      return localName;
   }

   /**
    * Obtained this IRI surrounded by angled brackets
    * 
    * @return This IRI surrounded by &lt; and &gt;
    */
   public String toQuotedString()
   {
       return '<' + namespace + localName + '>';
   }

   public URI toUri()
   {
      return URI.create(this.toString());
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o) {
         return true;
      }
      if (o instanceof Iri) {
         return toString().equals(o.toString());
      }
      return false;
   }

   @Override
   public int hashCode()
   {
      return namespace.hashCode() + localName.hashCode();
   }

   @Override
   public String toString()
   {
      return namespace + localName;
   }
}
