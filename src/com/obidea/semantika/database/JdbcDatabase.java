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
package com.obidea.semantika.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Set;

import org.slf4j.Logger;

import com.obidea.semantika.database.base.IForeignKey;
import com.obidea.semantika.database.base.IPrimaryKey;
import com.obidea.semantika.database.base.ITable;
import com.obidea.semantika.database.exception.DataSourceException;
import com.obidea.semantika.database.internal.InternalDatabase;
import com.obidea.semantika.database.sql.dialect.IDialect;
import com.obidea.semantika.util.LogUtils;

/**
 * An implementation of JDBC database.
 */
public class JdbcDatabase implements IDatabase
{
   private String mDatabaseProduct;
   private int mDatabaseMajorVersion;
   private int mDatabaseMinorVersion;

   private IDialect mDialect;

   private InternalDatabase mInternal;

   private static final Logger LOG = LogUtils.createLogger("semantika.database"); //$NON-NLS-1$

   public JdbcDatabase(final Connection conn) throws DataSourceException
   {
      try {
         DatabaseMetaData metadata = conn.getMetaData();
         mDatabaseProduct = metadata.getDatabaseProductName();
         mDatabaseMajorVersion = metadata.getDatabaseMajorVersion();
         mDatabaseMinorVersion = metadata.getDatabaseMinorVersion();
      }
      catch (SQLException e) {
         LOG.error("Unable to get information about the database"); //$NON-NLS-1$
         LOG.error("Detailed cause: {}", e.getMessage()); //$NON-NLS-1$
         throw new DataSourceException("Unable to get information about the database", e); //$NON-NLS-1$
      }
      
      try {
         mInternal = buildInternalDatabase(conn);
      }
      catch (SQLException e) {
         LOG.error("Unable to build the internal structure of database"); //$NON-NLS-1$
         LOG.error("Detailed cause: {}", e.getMessage()); //$NON-NLS-1$
         throw new DataSourceException("Unable to build the internal structure of database", e); //$NON-NLS-1$
      }
   }

   @Override
   public String getDatabaseProduct()
   {
      return mDatabaseProduct;
   }

   @Override
   public int getDatabaseMajorVersion()
   {
      return mDatabaseMajorVersion;
   }

   @Override
   public int getDatabaseMinorVersion()
   {
      return mDatabaseMinorVersion;
   }

   public void setDialect(IDialect dialect)
   {
      mDialect = dialect;
   }

   @Override
   public IDialect getDialect()
   {
      return mDialect;
   }

   @Override
   public IDatabaseMetadata getMetadata()
   {
      /**
       * Implementation of a lazy-built database metadata.
       */
      return new IDatabaseMetadata()
      {
         /**
          * Returns the <code>ITable</code> object given the full qualified name.
          * If the name already exists, the metadata returns the object from its
          * cache, otherwise it contacts the JDBC metadata to retrieve the 
          * information and create the object.
          */
         @Override
         public ITable getTable(String fullName)
         {
            return mInternal.getValue(mInternal.getTableReferences(), fullName);
         }

         /**
          * Returns a set of <code>ITable</code> objects that are stored in the
          * metadata cache. Accordingly, the metadata doesn't provide the
          * complete information about the tables stored in the database but
          * instead it only returns the recognized ones (i.e., from method
          * calling <code>getTable(java.lang.String)</code>).
          */
         @Override
         public Set<ITable> getTables()
         {
            return mInternal.getAllValues(mInternal.getTableReferences());
         }

         /**
          * Returns a set of <code>IPrimaryKey</code> objects that are stored in
          * the metadata cache. Accordingly, the metadata doesn't provide the
          * complete information about the primary keys stored in the database
          * but instead it only returns the recognized ones (i.e., from method
          * calling <code>getTable(java.lang.String)</code>).
          */
         @Override
         public Set<IPrimaryKey> getPrimaryKeys()
         {
            return mInternal.getAllValues(mInternal.getPrimaryKeyReferences());
         }

         /**
          * Returns a set of <code>IForeignKey</code> objects that are stored in
          * the metadata cache. Accordingly, the metadata doesn't provide the
          * complete information about the foreign keys stored in the database
          * but instead it only returns the recognized ones (i.e., from method
          * calling <code>getTable(java.lang.String)</code>).
          */
         @Override
         public Set<IForeignKey> getForeignKeys()
         {
            return mInternal.getAllValues(mInternal.getForeignKeyReferences());
         }
      };
   }

   private InternalDatabase buildInternalDatabase(Connection conn) throws SQLException
   {
      DatabaseMetaData metadata = conn.getMetaData();
      return new InternalDatabase(metadata);
   }
}
