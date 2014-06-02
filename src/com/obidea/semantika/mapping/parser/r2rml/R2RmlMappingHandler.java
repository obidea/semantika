package com.obidea.semantika.mapping.parser.r2rml;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import io.github.johardi.r2rmlparser.document.IMappingVisitor;
import io.github.johardi.r2rmlparser.document.LogicalTable;
import io.github.johardi.r2rmlparser.document.ObjectMap;
import io.github.johardi.r2rmlparser.document.PredicateMap;
import io.github.johardi.r2rmlparser.document.PredicateObjectMap;
import io.github.johardi.r2rmlparser.document.RefObjectMap;
import io.github.johardi.r2rmlparser.document.SubjectMap;
import io.github.johardi.r2rmlparser.document.TermMap;

import com.obidea.semantika.database.sql.base.ISqlExpression;
import com.obidea.semantika.exception.SemantikaRuntimeException;
import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.expression.base.UriReference;
import com.obidea.semantika.mapping.base.ClassMapping;
import com.obidea.semantika.mapping.base.PropertyMapping;
import com.obidea.semantika.mapping.parser.BaseMappingHandler;

public class R2RmlMappingHandler extends BaseMappingHandler implements IMappingVisitor
{
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
            List<ITerm> parameters = getColumnTerms(template.getColumnNames());
            setSubjectMapValue(getMappingObjectFactory().createUriTemplate(template.getTemplateString(), parameters));
            break;
      }
      ClassMapping cm = getMappingObjectFactory().createClassMapping(getClassUri(), getSqlQuery());
      cm.setSubjectMapValue(getSubjectMapValue()); // subject template
      addMapping(cm);
   }

   @Override
   public void visit(PredicateObjectMap arg)
   {
      arg.getPredicateMap().accept(this);
      arg.getObjectMap().accept(this);
      
      PropertyMapping pm = getMappingObjectFactory().createPropertyMapping(getPropertyUri(), getSqlQuery());
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
      switch (termMap) {
         case TermMap.COLUMN_VALUE:
            setObjectMapValue(getColumnTerm(value));
         case TermMap.CONSTANT_VALUE:
            setObjectMapValue(getExpressionObjectFactory().getUriReference(createUri(value)));
            break;
         case TermMap.TEMPLATE_VALUE:
            R2RmlTemplate template = new R2RmlTemplate(value);
            String templateString = template.getTemplateString();
            List<ITerm> parameters = getColumnTerms(template.getColumnNames());
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

   private ITerm getColumnTerm(String columnName)
   {
      ISqlExpression column = getSqlQuery().findSelectItemExpression(columnName);
      if (column != null) {
         return (ITerm) column;
      }
      throw new SemantikaRuntimeException("Unknown column name in template-valued term map (value=" + columnName + ")");
   }

   private List<ITerm> getColumnTerms(List<String> columnNames)
   {
      List<ITerm> toReturn = new ArrayList<ITerm>();
      for (String columnName : columnNames) {
         toReturn.add(getColumnTerm(columnName));
      }
      return toReturn;
   }
}
