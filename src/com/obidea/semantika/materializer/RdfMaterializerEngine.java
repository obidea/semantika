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
package com.obidea.semantika.materializer;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import org.slf4j.Logger;

import com.obidea.semantika.app.ApplicationManager;
import com.obidea.semantika.app.Settings;
import com.obidea.semantika.database.sql.base.ISqlExpression;
import com.obidea.semantika.database.sql.base.SqlSelectItem;
import com.obidea.semantika.database.sql.deparser.SqlDeparser;
import com.obidea.semantika.exception.SemantikaRuntimeException;
import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.expression.base.IUriReference;
import com.obidea.semantika.io.FileDocumentTarget;
import com.obidea.semantika.io.IDocumentTarget;
import com.obidea.semantika.knowledgebase.model.KnowledgeBase;
import com.obidea.semantika.mapping.IUriTemplate;
import com.obidea.semantika.mapping.base.IMapping;
import com.obidea.semantika.mapping.base.TripleAtom;
import com.obidea.semantika.mapping.sql.SqlColumn;
import com.obidea.semantika.mapping.sql.SqlIsNotNull;
import com.obidea.semantika.mapping.sql.SqlQuery;
import com.obidea.semantika.mapping.sql.SqlSelectQuery;
import com.obidea.semantika.mapping.sql.SqlUriConcat;
import com.obidea.semantika.mapping.sql.SqlUriValue;
import com.obidea.semantika.queryanswer.processor.TermToSqlConverter;
import com.obidea.semantika.util.LogUtils;

public class RdfMaterializerEngine implements IMaterializerEngine
{
   private ApplicationManager mAppManager;

   private Connection mConnection;

   private SqlDeparser mSqlDeparser;

   private IRdfMaterializer mMaterializer = new NTriplesMaterializer(); // by default

   private TermToSqlConverter mConverter = new TermToSqlConverter();

   private static final Logger LOG = LogUtils.createLogger("semantika.materializer"); //$NON-NLS-1$

   public RdfMaterializerEngine(ApplicationManager appManager)
   {
      mAppManager = appManager;
      mSqlDeparser = new SqlDeparser(appManager.getSettings().getDialect());
   }

   private KnowledgeBase getKnowledgeBase()
   {
      return mAppManager.getKnowledgeBase();
   }

   private Settings getSettings()
   {
      return mAppManager.getSettings();
   }

   @Override
   public void start() throws MaterializerEngineException
   {
      try {
         LOG.debug("Starting materializer engine."); //$NON-NLS-1$
         mConnection = getSettings().getConnectionProvider().getConnection();
      }
      catch (SQLException e) {
         throw new MaterializerEngineException(e);
      }
   }

   @Override
   public void stop() throws MaterializerEngineException
   {
      try {
         LOG.debug("Stopping materializer engine."); //$NON-NLS-1$
         getSettings().getConnectionProvider().closeConnection(mConnection);
      }
      catch (SQLException e) {
         throw new MaterializerEngineException(e);
      }
   }

   @Override
   public boolean isStarted()
   {
      try {
         if (mConnection != null && !mConnection.isClosed()) {
            return true;
         }
         return false;
      }
      catch (SQLException e) {
         LOG.error("Connection error", e);
         return false;
      }
   }

   public RdfMaterializerEngine useNTriples()
   {
      LOG.debug("Using NTriples format."); //$NON-NLS-1$
      setMateriliazer(new NTriplesMaterializer());
      return this;
   }

   public RdfMaterializerEngine useTurtle()
   {
      LOG.debug("Using Turtle format."); //$NON-NLS-1$
      setMateriliazer(new TurtleMaterializer());
      return this;
   }

   public RdfMaterializerEngine useRdfXml()
   {
      LOG.debug("Using RDF/XML format."); //$NON-NLS-1$
      setMateriliazer(new RdfXmlMaterializer());
      return this;
   }

   public RdfMaterializerEngine useRdfJson()
   {
      LOG.debug("Using JSON-LD format."); //$NON-NLS-1$
      setMateriliazer(new RdfJsonMaterializer());
      return this;
   }

   public void setMateriliazer(IRdfMaterializer materializer)
   {
      mMaterializer = materializer;
   }

   @Override
   public void materialize(File file) throws MaterializationException
   {
      materialize(file, new DefaultProgressMonitor());
   }

   @Override
   public void materialize(File file, IProgressMonitor progressMonitor) throws MaterializationException
   {
      if (file == null) {
         throw new MaterializationException("Output file cannot be empty"); //$NON-NLS-1$
      }
      removeFileIfExists(file);
      materialize(new FileDocumentTarget(file), progressMonitor);
   }

   public void materialize(IDocumentTarget output, IProgressMonitor progressMonitor) throws MaterializationException
   {
      try {
         checkConnection();
         int returnSize = 0;
         int mappingSize = getKnowledgeBase().getMappingSet().size();
         
         LOG.info(""); //$NON-NLS-1$
         LOG.info("Materialization in progress."); //$NON-NLS-1$
         progressMonitor.start(mappingSize);
         for (IMapping mapping : getKnowledgeBase().getMappingSet().getAll()) {
            ResultSet resultSet = null;
            progressMonitor.advanced(returnSize);
            try {
               SqlQuery query = prepareQueryForMaterialization(mapping);
               TriplesProjection projection = new TriplesProjection(query);
               String sql = mSqlDeparser.deparse(query);
               Statement stmt = createSqlStatement();
               resultSet = stmt.executeQuery(sql);
               returnSize = mMaterializer.materializeTuples(resultSet, projection, output);
            }
            catch (SQLException e) {
               throw new MaterializationException(e);
            }
            finally {
               if (resultSet != null && !resultSet.isClosed()) {
                  resultSet.close();
               }
            }
         }
         progressMonitor.finish();
      }
      catch (SQLException e) {
         throw new MaterializationException(e);
      }
   }

   protected void checkConnection() throws SQLException
   {
      if (mConnection == null) {
         throw new SemantikaRuntimeException(
               "Connection is null, call start() method first to start the engine."); //$NON-NLS-1$
      }
      if (mConnection.isClosed()) {
         throw new SemantikaRuntimeException(
               "Connection is closed, call start() method first to start the engine."); //$NON-NLS-1$
      }
   }

   protected Statement createSqlStatement() throws SQLException
   {
      Statement stmt = mConnection.createStatement();
      adjustFetchSize(stmt);
      return stmt;
   }

   protected void adjustFetchSize(Statement stmt) throws SQLException
   {
      final String databaseName = getSettings().getDatabase().getDatabaseProduct();
      if (databaseName.equals("MySQL")) { //$NON-NLS-1$
         stmt.setFetchSize(Integer.MIN_VALUE); // allow data streaming thus avoid heap memory error.
      }
      else {
         stmt.setFetchSize(100);
      }
   }

   /*
    * Private utility methods
    */

   private SqlQuery prepareQueryForMaterialization(final IMapping mapping)
   {
      SqlQuery query = initQuery(mapping.getSourceQuery());
      insertProjection(query, mapping.getTargetAtom());
      insertSelection(query, mapping.getSourceQuery().getFromExpression());
      insertFilters(query, mapping.getSourceQuery().getWhereExpression());
      insertNotNullFilters(query, mapping.getTargetAtom());
      return query;
   }

   private SqlQuery initQuery(SqlQuery sourceQuery)
   {
      return new SqlSelectQuery(sourceQuery.isDistinct());
   }

   private void insertProjection(SqlQuery query, TripleAtom head)
   {
      setSubject(query, TripleAtom.getSubject(head));
      setPredicate(query, TripleAtom.getPredicate(head));
      setObject(query, TripleAtom.getObject(head));
   }

   private void setSubject(SqlQuery query, ITerm term)
   {
      if (term instanceof IUriTemplate) {
         SqlUriConcat uriConcatExpression = mConverter.toSqlUriConcat((IUriTemplate) term);
         SqlSelectItem selectItem = new SqlSelectItem(uriConcatExpression);
         selectItem.setAliasName("subject"); //$NON-NLS-1$
         query.addSelectItem(selectItem);
      }
      else if (term instanceof IUriReference) {
         SqlUriValue valueExpression = mConverter.toSqlUriValue((IUriReference) term);
         SqlSelectItem selectItem = new SqlSelectItem(valueExpression);
         selectItem.setAliasName("subject"); //$NON-NLS-1$
         query.addSelectItem(selectItem);
      }
      else {
         throw new SemantikaRuntimeException("Other type: " + term.getClass());
      }
   }

   private void setPredicate(SqlQuery query, ITerm term)
   {
      if (term instanceof IUriReference) {
         SqlUriValue valueExpression = mConverter.toSqlUriValue((IUriReference) term);
         SqlSelectItem selectItem = new SqlSelectItem(valueExpression);
         selectItem.setAliasName("predicate"); //$NON-NLS-1$
         query.addSelectItem(selectItem);
      }
      else {
         throw new SemantikaRuntimeException("Other type: " + term.getClass());
      }
   }

   private void setObject(SqlQuery query, ITerm term)
   {
      if (term instanceof SqlColumn) {
         SqlSelectItem selectItem = new SqlSelectItem((SqlColumn) term);
         selectItem.setAliasName("object"); //$NON-NLS-1$
         query.addSelectItem(selectItem);
      }
      else if (term instanceof IUriTemplate) {
         SqlUriConcat uriConcatExpression = mConverter.toSqlUriConcat((IUriTemplate) term);
         SqlSelectItem selectItem = new SqlSelectItem(uriConcatExpression);
         selectItem.setAliasName("object"); //$NON-NLS-1$
         query.addSelectItem(selectItem);
      }
      else if (term instanceof IUriReference) {
         SqlUriValue valueExpression = mConverter.toSqlUriValue((IUriReference) term);
         SqlSelectItem selectItem = new SqlSelectItem(valueExpression);
         selectItem.setAliasName("object"); //$NON-NLS-1$
         query.addSelectItem(selectItem);
      }
      else {
         throw new SemantikaRuntimeException("Other type: " + term.getClass());
      }
   }

   private void insertSelection(SqlQuery query, ISqlExpression fromExpression)
   {
      query.setFromExpression(fromExpression);
   }

   private void insertFilters(SqlQuery query, Set<ISqlExpression> whereExpressions)
   {
      for (ISqlExpression filter : whereExpressions) {
         query.addWhereExpression(filter);
      }
   }

   private void insertNotNullFilters(SqlQuery query, TripleAtom head)
   {
      ITerm subjectTerm = TripleAtom.getSubject(head);
      setNotNullFilter(query, subjectTerm);
      
      ITerm objectTerm = TripleAtom.getObject(head);
      setNotNullFilter(query, objectTerm);
   }

   private void setNotNullFilter(SqlQuery query, ITerm term)
   {
      if (term instanceof SqlColumn) {
         ISqlExpression filter = new SqlIsNotNull((SqlColumn) term);
         query.addWhereExpression(filter);
      }
      else if (term instanceof IUriTemplate) {
         IUriTemplate uriTemplate = (IUriTemplate) term;
         for (ITerm parameter : uriTemplate.getParameters()) {
            setNotNullFilter(query, parameter);
         }
      }
   }

   private static void removeFileIfExists(File file)
   {
      if (file.exists()) {
         file.delete();
      }
   }
}
