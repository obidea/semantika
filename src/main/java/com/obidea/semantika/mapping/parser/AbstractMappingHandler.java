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
package com.obidea.semantika.mapping.parser;

import java.util.ArrayList;
import java.util.List;

import com.obidea.semantika.database.IDatabaseMetadata;
import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.expression.base.Iri;
import com.obidea.semantika.mapping.IMetaModel;
import com.obidea.semantika.mapping.base.IMapping;
import com.obidea.semantika.mapping.base.sql.SqlQuery;
import com.obidea.semantika.ontology.IOntology;

public abstract class AbstractMappingHandler
{
   private String mBaseIri;
   private IOntology mOntology;
   private IDatabaseMetadata mDatabaseMetadata;

   private SqlQuery mSqlQuery;

   private Iri mSubjectIri;
   private Iri mPredicateIri;

   private ITerm mSubjectMapValue;
   private ITerm mPredicateMapValue;
   private ITerm mObjectMapValue;

   private List<IMapping> mMappings = new ArrayList<IMapping>();

   public AbstractMappingHandler(IMetaModel metaModel)
   {
      mOntology = metaModel.getOntology();
      mDatabaseMetadata = metaModel.getDatabaseMetadata();
   }

   public void setBaseIri(String baseIri)
   {
      mBaseIri = baseIri;
   }

   public String getBaseIri()
   {
      return mBaseIri;
   }

   public IOntology getOntology()
   {
      return mOntology;
   }

   public IDatabaseMetadata getDatabaseMetadata()
   {
      return mDatabaseMetadata;
   }

   protected void addMapping(IMapping mapping)
   {
      mMappings.add(mapping);
   }

   public List<IMapping> getMappings()
   {
      return mMappings;
   }

   protected void setSqlQuery(SqlQuery sqlQuery)
   {
      mSqlQuery = sqlQuery;
   }

   public SqlQuery getSqlQuery()
   {
      return mSqlQuery;
   }

   protected void setSubjectIri(Iri subjectIri)
   {
      mSubjectIri = subjectIri;
   }

   public Iri getSubjectIri()
   {
      return mSubjectIri;
   }

   protected void setPredicateIri(Iri predicateIri)
   {
      mPredicateIri = predicateIri;
   }

   public Iri getPredicateIri()
   {
      return mPredicateIri;
   }

   protected void setSubjectMapValue(ITerm subjectTerm)
   {
      mSubjectMapValue = subjectTerm;
   }

   public ITerm getSubjectMapValue()
   {
      return mSubjectMapValue;
   }

   protected void setPredicateMapValue(ITerm predicateTerm)
   {
      mPredicateMapValue = predicateTerm;
   }

   public ITerm getPredicateMapValue()
   {
      return mPredicateMapValue;
   }

   protected void setObjectMapValue(ITerm objectTerm)
   {
      mObjectMapValue = objectTerm;
   }

   public ITerm getObjectMapValue()
   {
      return mObjectMapValue;
   }
}
