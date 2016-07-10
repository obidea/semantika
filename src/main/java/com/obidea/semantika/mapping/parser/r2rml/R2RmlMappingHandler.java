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
package com.obidea.semantika.mapping.parser.r2rml;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import io.github.johardi.r2rmlparser.R2RmlVocabulary;
import io.github.johardi.r2rmlparser.document.IMappingVisitor;
import io.github.johardi.r2rmlparser.document.LogicalTable;
import io.github.johardi.r2rmlparser.document.ObjectMap;
import io.github.johardi.r2rmlparser.document.PredicateMap;
import io.github.johardi.r2rmlparser.document.PredicateObjectMap;
import io.github.johardi.r2rmlparser.document.RefObjectMap;
import io.github.johardi.r2rmlparser.document.SubjectMap;
import io.github.johardi.r2rmlparser.document.TermMap;

import com.obidea.semantika.database.sql.parser.SqlFactory;
import com.obidea.semantika.datatype.DataType;
import com.obidea.semantika.datatype.exception.UnsupportedDataTypeException;
import com.obidea.semantika.exception.SemantikaRuntimeException;
import com.obidea.semantika.expression.ExpressionObjectFactory;
import com.obidea.semantika.expression.base.IIriReference;
import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.expression.base.Iri;
import com.obidea.semantika.mapping.IMetaModel;
import com.obidea.semantika.mapping.IriTemplate;
import com.obidea.semantika.mapping.MappingObjectFactory;
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
   public void setSubjectIri(Iri subjectIri)
   {
      checkClassSignature(subjectIri);
      super.setSubjectIri(subjectIri);
   }

   @Override
   public void setPredicateIri(Iri propertyIri)
   {
      checkPropertySignature(propertyIri);
      super.setPredicateIri(propertyIri);
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
      setSubjectIri(Iri.create(arg.getClassIri()));
      
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
      if (getSubjectIri() != null) {
         addMapping(sMappingObjectFactory.createClassMapping(getSubjectIri(), getSqlQuery(), getSubjectMapValue()));
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
      addMapping(sMappingObjectFactory.createPropertyMapping(getPredicateIri(), getSqlQuery(), getSubjectMapValue(),
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
            IIriReference predicateIri = (IIriReference) getLiteralTerm(value, termType, datatype);
            setPredicateIri(predicateIri.toIri());
            setPredicateMapValue(predicateIri);
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

   private void checkClassSignature(Iri iri)
   {
      if (isStrictParsing()) {
         if (getOntology().containClass(iri)) {
            return;
         }
         throw new SemantikaRuntimeException(format("Class %s is not found in ontology", iri.toQuotedString()));
      }
   }

   private void checkPropertySignature(Iri iri)
   {
      if (isStrictParsing()) {
         if (getOntology().containObjectProperty(iri) || getOntology().containDataProperty(iri)) {
            return;
         }
         throw new SemantikaRuntimeException(format("Property %s is not found in ontology", iri.toQuotedString()));
      }
   }

   private ITerm getColumnTerm(String columnName, String termType, String datatype)
   {
      if (termType.equals(R2RmlVocabulary.IRI)) {
         if (!StringUtils.isEmpty(datatype)) {
            throw new IllegalR2RmlMappingException("Cannot use rr:datatype together with term type rr:IRI"); //$NON-NLS-1$
         }
         SqlColumn column = getColumnTerm(columnName);
         column.setTermType(TermType.IRI_TYPE);
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
         return sExpressionObjectFactory.getIriReference(value);
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
         IriTemplate uriTemplate = sMappingObjectFactory.createIriTemplate(templateString, parameters);
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
}
