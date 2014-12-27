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
package com.obidea.semantika.mapping.parser.r2rml;

import static java.lang.String.format;
import io.github.johardi.r2rmlparser.R2RmlVocabulary;
import io.github.johardi.r2rmlparser.document.IMappingVisitor;
import io.github.johardi.r2rmlparser.document.LogicalTable;
import io.github.johardi.r2rmlparser.document.ObjectMap;
import io.github.johardi.r2rmlparser.document.PredicateMap;
import io.github.johardi.r2rmlparser.document.PredicateObjectMap;
import io.github.johardi.r2rmlparser.document.RefObjectMap;
import io.github.johardi.r2rmlparser.document.SubjectMap;
import io.github.johardi.r2rmlparser.document.TermMap;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.obidea.semantika.database.sql.parser.SqlFactory;
import com.obidea.semantika.datatype.DataType;
import com.obidea.semantika.datatype.exception.UnsupportedDataTypeException;
import com.obidea.semantika.exception.SemantikaRuntimeException;
import com.obidea.semantika.expression.ExpressionObjectFactory;
import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.expression.base.UriReference;
import com.obidea.semantika.mapping.IMetaModel;
import com.obidea.semantika.mapping.MappingObjectFactory;
import com.obidea.semantika.mapping.UriTemplate;
import com.obidea.semantika.mapping.base.TermType;
import com.obidea.semantika.mapping.base.sql.SqlColumn;
import com.obidea.semantika.mapping.base.sql.SqlQuery;
import com.obidea.semantika.mapping.parser.AbstractMappingHandler;

public class R2RmlMappingHandler extends AbstractMappingHandler implements IMappingVisitor
{
   private SqlFactory mSqlFactory = new SqlFactory(getDatabaseMetadata());

   private boolean mUseStrictParsing = false;

   private static ExpressionObjectFactory sExpressionObjectFactory = ExpressionObjectFactory.getInstance();
   private static MappingObjectFactory sMappingObjectFactory = MappingObjectFactory.getInstance();

   public R2RmlMappingHandler(IMetaModel metaModel)
   {
      super(metaModel);
   }

   public void setStrictParsing(boolean useStrictParsing)
   {
      mUseStrictParsing = useStrictParsing;
   }

   public boolean isStrictParsing()
   {
      return mUseStrictParsing;
   }

   @Override
   public void setSubjectUri(URI classUri)
   {
      checkClassSignature(classUri);
      super.setSubjectUri(classUri);
   }

   @Override
   public void setPredicateUri(URI propertyUri)
   {
      checkPropertySignature(propertyUri);
      super.setPredicateUri(propertyUri);
   }

   @Override
   public void visit(LogicalTable arg)
   {
      String sqlString = arg.getTableView().getSqlQuery();
      setSqlQuery(createQuery(sqlString));
   }

   private SqlQuery createQuery(String sqlString)
   {
      return mSqlFactory.create(sqlString);
   }

   @Override
   public void visit(SubjectMap arg)
   {
      validateSubjectMap(arg);
      setSubjectUri(createUri(arg.getClassIri()));
      
      int termMap = arg.getType();
      String value = arg.getValue();
      String termType = arg.getTermType();
      String datatype = arg.getDatatype();
      switch (termMap) {
         case TermMap.COLUMN_VALUE:
            setSubjectMapValue(getColumnTerm(value, termType, datatype));
            break;
         case TermMap.CONSTANT_VALUE:
            setSubjectMapValue(getLiteralTerm(value, termType, datatype));
            break;
         case TermMap.TEMPLATE_VALUE:
            setSubjectMapValue(getTemplateTerm(value, termType, datatype));
            break;
      }
      // Create the class mapping if a class URI specified in the mapping
      if (getSubjectUri() != null) {
         addMapping(sMappingObjectFactory.createClassMapping(getSubjectUri(), getSqlQuery(), getSubjectMapValue()));
      }
   }

   /*
    * Validation procedure based on http://www.w3.org/TR/r2rml/#termtype
    */
   private void validateSubjectMap(SubjectMap arg)
   {
      String termType = arg.getTermType();
      if (termType.equals(R2RmlVocabulary.LITERAL)) {
         throw new IllegalR2RmlMappingException("Subject map cannot have term type rr:Literal"); //$NON-NLS-1$
      }
   }

   @Override
   public void visit(PredicateObjectMap arg)
   {
      arg.getPredicateMap().accept(this);
      arg.getObjectMap().accept(this);
      addMapping(sMappingObjectFactory.createPropertyMapping(getPredicateUri(), getSqlQuery(), getSubjectMapValue(),
            getObjectMapValue()));
   }

   @Override
   public void visit(PredicateMap arg)
   {
      validatePredicateMap(arg);
      int termMap = arg.getType();
      String value = arg.getValue();
      String termType = arg.getTermType();
      String datatype = arg.getDatatype();
      switch (termMap) {
         case TermMap.COLUMN_VALUE:
            throw new IllegalR2RmlMappingException("Predicate map cannot use column-valued term map"); //$NON-NLS-1$
         case TermMap.CONSTANT_VALUE:
            ITerm predicateTerm = getLiteralTerm(value, termType, datatype);
            setPredicateUri(createUri(predicateTerm));
            setPredicateMapValue(predicateTerm);
            break;
         case TermMap.TEMPLATE_VALUE:
            throw new IllegalR2RmlMappingException("Predicate map cannot use template-valued term map"); //$NON-NLS-1$
      }
   }

   /*
    * Validation procedure based on http://www.w3.org/TR/r2rml/#termtype
    */
   private void validatePredicateMap(PredicateMap arg)
   {
      String termType = arg.getTermType();
      if (termType.equals(R2RmlVocabulary.LITERAL)) {
         throw new IllegalR2RmlMappingException("Subject map cannot have term type rr:Literal"); //$NON-NLS-1$
      }
      else if (termType.equals(R2RmlVocabulary.BLANK_NODE)) {
         throw new IllegalR2RmlMappingException("Subject map cannot have term type rr:BlankNode"); //$NON-NLS-1$
      }
   }

   @Override
   public void visit(ObjectMap arg)
   {
      int termMap = arg.getType();
      String value = arg.getValue();
      String termType = arg.getTermType();
      String datatype = arg.getDatatype();
      switch (termMap) {
         case TermMap.COLUMN_VALUE:
            setObjectMapValue(getColumnTerm(value, termType, datatype));
            break;
         case TermMap.CONSTANT_VALUE:
            setObjectMapValue(getLiteralTerm(value, termType, datatype));
            break;
         case TermMap.TEMPLATE_VALUE:
            setObjectMapValue(getTemplateTerm(value, termType, datatype));
            break;
      }
   }

   @Override
   public void visit(RefObjectMap arg)
   {
      // NO-OP
   }

   /*
    * Private utility methods
    */

   private void checkClassSignature(URI uri)
   {
      if (uri == null) {
         return; // if input is null then nothing to check
      }
      if (isStrictParsing()) {
         if (getOntology().containClass(uri)) {
            return;
         }
         throw new SemantikaRuntimeException(format("Class <%s> is not found in ontology", uri));
      }
   }

   private void checkPropertySignature(URI uri)
   {
      if (uri == null) {
         throw new IllegalArgumentException("Property name is null");
      }
      if (isStrictParsing()) {
         if (getOntology().containObjectProperty(uri) || getOntology().containDataProperty(uri)) {
            return;
         }
         throw new SemantikaRuntimeException(format("Property <%s> is not found in ontology", uri));
      }
   }

   private ITerm getColumnTerm(String columnName, String termType, String datatype)
   {
      if (termType.equals(R2RmlVocabulary.IRI)) {
         if (!StringUtils.isEmpty(datatype)) {
            throw new IllegalR2RmlMappingException("Cannot use rr:datatype together with term type rr:IRI"); //$NON-NLS-1$
         }
         SqlColumn column = getColumnTerm(columnName);
         column.setTermType(TermType.URI_TYPE);
         return column;
      }
      else if (termType.equals(R2RmlVocabulary.LITERAL)) {
         SqlColumn column = getColumnTerm(columnName);
         column.setTermType(TermType.LITERAL_TYPE);
         if (!StringUtils.isEmpty(datatype)) {
            overrideColumn(column, datatype); // set as datatype-override RDF literal
         }
         return column;
      }
      else if (termType.equals(R2RmlVocabulary.BLANK_NODE)) {
         throw new UnsupportedR2RmlFeatureException("rr:BlankNode as term type"); //$NON-NLS-1$
      }
      throw new R2RmlParserException(format("Unknown term type \"%s\"", termType)); //$NON-NLS-1$
   }

   private ITerm getLiteralTerm(String value, String termType, String datatype)
   {
      if (termType.equals(R2RmlVocabulary.IRI)) {
         if (!StringUtils.isEmpty(datatype)) {
            throw new IllegalR2RmlMappingException("Cannot use rr:datatype together with term type rr:IRI"); //$NON-NLS-1$
         }
         return sExpressionObjectFactory.getUriReference(createUri(value));
      }
      else if (termType.equals(R2RmlVocabulary.LITERAL)) {
         return (StringUtils.isEmpty(datatype)) ?
               sExpressionObjectFactory.getLiteral(value, DataType.STRING) : // by default
               sExpressionObjectFactory.getLiteral(value, datatype);
      }
      else if (termType.equals(R2RmlVocabulary.BLANK_NODE)) {
         throw new UnsupportedR2RmlFeatureException("rr:BlankNode as term type"); //$NON-NLS-1$
      }
      throw new R2RmlParserException(format("Unknown term type \"%s\"", termType)); //$NON-NLS-1$
   }

   private ITerm getTemplateTerm(String value, String termType, String datatype)
   {
      if (termType.equals(R2RmlVocabulary.IRI)) {
         if (!StringUtils.isEmpty(datatype)) {
            throw new IllegalR2RmlMappingException("Cannot use rr:datatype together with term type rr:IRI"); //$NON-NLS-1$
         }
         R2RmlTemplate template = new R2RmlTemplate(value);
         String templateString = template.getTemplateString();
         List<SqlColumn> parameters = getColumnTerms(template.getColumnNames());
         UriTemplate uriTemplate = sMappingObjectFactory.createUriTemplate(templateString, parameters);
         return uriTemplate;
      }
      else if (termType.equals(R2RmlVocabulary.LITERAL)) {
         throw new UnsupportedR2RmlFeatureException("rr:template for literal string construction");
      }
      else if (termType.equals(R2RmlVocabulary.BLANK_NODE)) {
         throw new UnsupportedR2RmlFeatureException("rr:BlankNode as term type"); //$NON-NLS-1$
      }
      throw new R2RmlParserException(format("Unknown term type \"%s\"", termType)); //$NON-NLS-1$
   }

   private SqlColumn getColumnTerm(String columnName)
   {
      SqlColumn column = (SqlColumn) getSqlQuery().findSelectItemExpression(columnName);
      if (column != null) {
         return column;
      }
      throw new R2RmlParserException(format("Unknown column name \"%s\"", columnName)); //$NON-NLS-1$
   }

   private List<SqlColumn> getColumnTerms(List<String> columnNames)
   {
      List<SqlColumn> toReturn = new ArrayList<SqlColumn>();
      for (String columnName : columnNames) {
         toReturn.add(getColumnTerm(columnName));
      }
      return toReturn;
   }

   private void overrideColumn(SqlColumn column, String datatype) throws R2RmlParserException
   {
      try {
         column.overrideDatatype(datatype);
      }
      catch (UnsupportedDataTypeException e) {
         throw new IllegalR2RmlMappingException(e.getMessage());
      }
      catch (IllegalArgumentException e) {
         throw new IllegalR2RmlMappingException(e.getMessage());
      }
   }

   private URI createUri(String uriString)
   {
      if (!StringUtils.isEmpty(uriString)) {
         return URI.create(uriString);
      }
      return null;
   }

   private URI createUri(ITerm term)
   {
      if (term instanceof UriReference) {
         return ((UriReference) term).toUri();
      }
      return null;
   }
}
