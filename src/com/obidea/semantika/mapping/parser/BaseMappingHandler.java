package com.obidea.semantika.mapping.parser;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.obidea.semantika.database.IDatabaseMetadata;
import com.obidea.semantika.exception.SemantikaRuntimeException;
import com.obidea.semantika.expression.ExpressionObjectFactory;
import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.mapping.IMappingFactory.IMetaModel;
import com.obidea.semantika.mapping.MappingObjectFactory;
import com.obidea.semantika.mapping.base.IMapping;
import com.obidea.semantika.mapping.sql.SqlQuery;
import com.obidea.semantika.mapping.sql.parser.SqlFactory;
import com.obidea.semantika.mapping.sql.parser.SqlMappingParserException;
import com.obidea.semantika.ontology.IOntology;

public abstract class BaseMappingHandler
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

   public BaseMappingHandler(IMetaModel metaModel)
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
      catch (SqlMappingParserException e) {
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
