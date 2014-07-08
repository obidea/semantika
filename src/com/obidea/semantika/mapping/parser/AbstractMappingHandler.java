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
package com.obidea.semantika.mapping.parser;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.obidea.semantika.database.IDatabaseMetadata;
import com.obidea.semantika.database.sql.parser.SqlFactory;
import com.obidea.semantika.database.sql.parser.SqlParserException;
import com.obidea.semantika.exception.SemantikaRuntimeException;
import com.obidea.semantika.expression.ExpressionObjectFactory;
import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.mapping.IMetaModel;
import com.obidea.semantika.mapping.MappingObjectFactory;
import com.obidea.semantika.mapping.base.IMapping;
import com.obidea.semantika.mapping.base.sql.SqlQuery;
import com.obidea.semantika.ontology.IOntology;

public abstract class AbstractMappingHandler
{
   private String mBaseIri;
   private boolean mUseStrictParsing;
   private IOntology mOntology;
   private IDatabaseMetadata mDatabaseMetadata;

   private SqlFactory mSqlFactory;
   private SqlQuery mSqlQuery;

   private URI mClassUri;

   private ITerm mSubjectMapValue;
   private ITerm mPredicateMapValue;
   private ITerm mObjectMapValue;

   private List<IMapping> mMappings = new ArrayList<IMapping>();

   public AbstractMappingHandler(IMetaModel metaModel)
   {
      mOntology = metaModel.getOntology();
      mDatabaseMetadata = metaModel.getDatabaseMetadata();
      mSqlFactory = new SqlFactory(mDatabaseMetadata);
   }

   public IOntology getOntology()
   {
      return mOntology;
   }

   public IDatabaseMetadata getDatabaseMetadata()
   {
      return mDatabaseMetadata;
   }

   public void addMapping(IMapping mapping)
   {
      mMappings.add(mapping);
   }

   public List<IMapping> getMappings()
   {
      return mMappings;
   }

   public void setBaseIri(String baseIri)
   {
      mBaseIri = baseIri;
   }

   public String getBaseIri()
   {
      return mBaseIri;
   }

   public void setStrictParsing(boolean useStrictParsing)
   {
      mUseStrictParsing = useStrictParsing;
   }

   public boolean isStrictParsing()
   {
      return mUseStrictParsing;
   }

   public void setSqlQuery(String sqlString)
   {
      try {
         mSqlQuery = mSqlFactory.create(sqlString);
      }
      catch (SqlParserException e) {
         throw new SemantikaRuntimeException(e);
      }
   }

   public SqlQuery getSqlQuery()
   {
      return mSqlQuery;
   }

   public void setClassUri(String uri)
   {
      if (!StringUtils.isEmpty(uri)) {
         URI classUri = createUri(uri);
         checkClassSignature(classUri);
         mClassUri = classUri;
      }
   }

   public URI getClassUri()
   {
      return mClassUri;
   }

   public void setSubjectMapValue(ITerm subjectTerm)
   {
      mSubjectMapValue = subjectTerm;
   }

   public ITerm getSubjectMapValue()
   {
      return mSubjectMapValue;
   }

   public void setPredicateMapValue(ITerm predicateTerm)
   {
      mPredicateMapValue = predicateTerm;
   }

   public ITerm getPredicateMapValue()
   {
      return mPredicateMapValue;
   }

   public void setObjectMapValue(ITerm objectTerm)
   {
      mObjectMapValue = objectTerm;
   }

   public ITerm getObjectMapValue()
   {
      return mObjectMapValue;
   }

   /*
    * Protected utility methods
    */

   protected ExpressionObjectFactory getExpressionObjectFactory()
   {
      return ExpressionObjectFactory.getInstance();
   }

   protected MappingObjectFactory getMappingObjectFactory()
   {
      return MappingObjectFactory.getInstance();
   }

   protected void checkClassSignature(URI uri)
   {
      if (isStrictParsing()) {
         if (getOntology().containClass(uri)) {
            return;
         }
         throw new SemantikaRuntimeException("Class URI not found in ontology \"" + uri + "\"");
      }
   }

   protected void checkPropertySignature(URI uri)
   {
      if (isStrictParsing()) {
         if (getOntology().containObjectProperty(uri) || getOntology().containDataProperty(uri)) {
            return;
         }
         throw new SemantikaRuntimeException("Property URI not found in ontology \"" + uri + "\"");
      }
   }

   protected URI createUri(String uriString)
   {
      return URI.create(uriString);
   }
}
