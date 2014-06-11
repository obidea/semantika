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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import io.github.johardi.r2rmlparser.document.IMappingVisitor;
import io.github.johardi.r2rmlparser.document.LogicalTable;
import io.github.johardi.r2rmlparser.document.ObjectMap;
import io.github.johardi.r2rmlparser.document.PredicateMap;
import io.github.johardi.r2rmlparser.document.PredicateObjectMap;
import io.github.johardi.r2rmlparser.document.RefObjectMap;
import io.github.johardi.r2rmlparser.document.SubjectMap;
import io.github.johardi.r2rmlparser.document.TermMap;

import com.obidea.semantika.exception.SemantikaRuntimeException;
import com.obidea.semantika.expression.base.UriReference;
import com.obidea.semantika.mapping.IMappingFactory.IMetaModel;
import com.obidea.semantika.mapping.base.ClassMapping;
import com.obidea.semantika.mapping.base.PropertyMapping;
import com.obidea.semantika.mapping.base.sql.SqlColumn;
import com.obidea.semantika.mapping.parser.AbstractMappingHandler;

public class R2RmlMappingHandler extends AbstractMappingHandler implements IMappingVisitor
{
   public R2RmlMappingHandler(IMetaModel metaModel)
   {
      super(metaModel);
   }

   @Override
   public void visit(LogicalTable arg)
   {
      setSqlQuery(arg.getTableView().getSqlQuery());
   }

   @Override
   public void visit(SubjectMap arg)
   {
      setClassUri(arg.getClassIri());
      
      int termMap = arg.getType();
      String value = arg.getValue();
      switch (termMap) {
         case TermMap.COLUMN_VALUE:
            throw new SemantikaRuntimeException("Subject map cannot use column-valued term map");
         case TermMap.CONSTANT_VALUE:
            setSubjectMapValue(getExpressionObjectFactory().getUriReference(createUri(value)));
            break;
         case TermMap.TEMPLATE_VALUE:
            R2RmlTemplate template = new R2RmlTemplate(value);
            List<SqlColumn> parameters = getColumnTerms(template.getColumnNames());
            setSubjectMapValue(getMappingObjectFactory().createUriTemplate(template.getTemplateString(), parameters));
            break;
      }
      // Create the class mapping if a class URI specified in the mapping
      if (getClassUri() != null) {
         ClassMapping cm = getMappingObjectFactory().createClassMapping(getClassUri(), getSqlQuery());
         cm.setSubjectMapValue(getSubjectMapValue()); // subject template
         addMapping(cm);
      }
   }

   @Override
   public void visit(PredicateObjectMap arg)
   {
      arg.getPredicateMap().accept(this);
      arg.getObjectMap().accept(this);
      
      PropertyMapping pm = getMappingObjectFactory().createPropertyMapping(getPropertyUri(), getSqlQuery());
      pm.setSubjectMapValue(getSubjectMapValue());
      pm.setObjectMapValue(getObjectMapValue());
      addMapping(pm);
   }

   @Override
   public void visit(PredicateMap arg)
   {
      int termMap = arg.getType();
      String value = arg.getValue();
      switch (termMap) {
         case TermMap.COLUMN_VALUE:
            throw new SemantikaRuntimeException("Predicate map cannot use column-valued term map");
         case TermMap.CONSTANT_VALUE:
            setPredicateMapValue(getExpressionObjectFactory().getUriReference(createUri(value)));
            break;
         case TermMap.TEMPLATE_VALUE:
            throw new SemantikaRuntimeException("Predicate map cannot use template-valued term map");
      }
   }

   @Override
   public void visit(ObjectMap arg)
   {
      int termMap = arg.getType();
      String value = arg.getValue();
      String datatype = arg.getDatatype();
      switch (termMap) {
         case TermMap.COLUMN_VALUE:
            setObjectMapValue(getColumnTerm(value, datatype));
            break;
         case TermMap.CONSTANT_VALUE:
            setObjectMapValue(getExpressionObjectFactory().getUriReference(createUri(value)));
            break;
         case TermMap.TEMPLATE_VALUE:
            R2RmlTemplate template = new R2RmlTemplate(value);
            String templateString = template.getTemplateString();
            List<SqlColumn> parameters = getColumnTerms(template.getColumnNames());
            setObjectMapValue(getMappingObjectFactory().createUriTemplate(templateString, parameters));
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

   private URI getPropertyUri()
   {
      return ((UriReference) getPredicateMapValue()).toUri();
   }

   private SqlColumn getColumnTerm(String columnName, String datatype)
   {
      SqlColumn column = getColumnTerm(columnName);
      if (!StringUtils.isEmpty(datatype)) {
         column.setUserDatatype(datatype);
      }
      return column;
   }

   private SqlColumn getColumnTerm(String columnName)
   {
      SqlColumn column = (SqlColumn) getSqlQuery().findSelectItemExpression(columnName);
      if (column != null) {
         return column;
      }
      throw new SemantikaRuntimeException("Unknown column name in template-valued term map \"" + columnName + "\")");
   }

   private List<SqlColumn> getColumnTerms(List<String> columnNames)
   {
      List<SqlColumn> toReturn = new ArrayList<SqlColumn>();
      for (String columnName : columnNames) {
         toReturn.add(getColumnTerm(columnName));
      }
      return toReturn;
   }
}
