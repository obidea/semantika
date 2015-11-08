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
package com.obidea.semantika.mapping.parser.termalxml;

import com.obidea.semantika.database.sql.parser.SqlFactory;
import com.obidea.semantika.database.sql.parser.SqlParserException;
import com.obidea.semantika.mapping.base.sql.SqlQuery;
import com.obidea.semantika.mapping.exception.MappingParserException;
import com.obidea.semantika.util.StringUtils;

public class LogicalTableElementHandler extends AbstractMappingQueryElementHandler
{
   private String mSqlString = "";

   private SqlFactory mSqlFactory = new SqlFactory(getDatabaseMetadata());

   public LogicalTableElementHandler(TermalXmlParserHandler handler)
   {
      super(handler);
   }

   @Override
   public void startElement(String name) throws MappingParserException
   {
      super.startElement(name);
   }

   @Override
   public void endElement() throws MappingParserException
   {
      try {
         setSqlQuery(createQuery());
         getParentElement().handleChild(this);
         mSqlString = ""; //$NON-NLS-1$ // clear the SQL string
      }
      catch (SqlParserException e) {
         throw sourceQueryParsingException(e);
      }
   }

   @Override
   public void attribute(String name, String value) throws MappingParserException
   {
      if (name.equals(R2RmlVocabulary.TABLE_NAME.getQName())) {
         mSqlString = createSimpleQuery(value);
      }
      else {
         throw unknownXmlAttributeException(name);
      }
   }

   @Override
   public void characters(char[] ch, int start, int length) throws MappingParserException
   {
      /*
       * Construct the SQL string from CDATA section. The string concatenation is
       * necessary because SAX parser may present the text in CDATA in several chunks
       * of strings by calling this method more than once.
       */
      String buffer = new String(ch, start, length);
      buffer = (StringUtils.isEmpty(buffer)) ? "" : buffer.replaceAll("\\s+", " "); //$NON-NLS-1$
      mSqlString += buffer;
   }

   @Override
   protected SqlQuery createQuery() throws SqlParserException
   {
      return mSqlFactory.create(getSqlString()); //$NON-NLS-1$
   }

   /**
    * Get the SQL string found in this element
    */
   private String getSqlString()
   {
      return mSqlString.trim();
   }

   private String createSimpleQuery(String tableName)
   {
      return String.format("select * from %s", tableName); //$NON-NLS-1$
   }
}
