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
package com.obidea.semantika.mapping.parser.termalxml;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.obidea.semantika.database.sql.base.ISqlExpression;
import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.expression.base.IUriReference;
import com.obidea.semantika.mapping.IUriTemplate;
import com.obidea.semantika.mapping.base.IMapping;
import com.obidea.semantika.mapping.exception.MappingParserException;
import com.obidea.semantika.util.StringUtils;

public abstract class AbstractMappingElementHandler extends AbstractTermalElementHandler
{
   private IMapping mMapping;

   public AbstractMappingElementHandler(TermalXmlParserHandler handler)
   {
      super(handler);
   }

   public IMapping getMapping()
   {
      return mMapping;
   }

   protected void setMapping(IMapping mapping)
   {
      mMapping = mapping;
   }

   @Override
   protected MappingElementHandler getParentElement()
   {
      return (MappingElementHandler) super.getParentElement();
   }

   protected abstract IMapping createMapping();

   @Override
   /* package */void handleChild(MappingElementHandler handler)
   {
      // NO-OP: No child node afterwards
   }

   @Override
   /* package */void handleChild(LogicalTableElementHandler handler)
   {
      // NO-OP: No child node afterwards
   }

   @Override
   /* package */void handleChild(SubjectMapElementHandler handler)
   {
      // NO-OP: No child node afterwards
   }

   @Override
   /* package */void handleChild(PredicateObjectMapElementHandler handler)
   {
      // NO-OP: No child node afterwards
   }

   /*
    * Protected utility methods
    */

   protected ITerm getColumnExpression(String selectItemLabel) throws MappingParserException
   {
      /*
       * We extend the return type of column-valued term map to include other ITerm objects,
       * i.e., functions and constants. This is because the column-valued term map can also
       * be expressed in functions or constants, such that these expressions are "aliased"
       * using SQL alias name.
       */
      ISqlExpression expression = getParentElement().getSourceQuery().findSelectItemExpression(selectItemLabel);
      if (expression != null) {
         return (ITerm) expression;
      }
      throw selectItemNameNotFoundException(selectItemLabel);
   }

   protected IUriTemplate getUriTemplateFunction(String functionCall) throws MappingParserException
   {
      // Get the URI template name
      String templateName = FunctionCallUtils.getFunctionName(functionCall);
      if (StringUtils.isEmpty(templateName)) {
         throw invalidFunctionCallException(functionCall);
      }
      
      // Get the URI template arguments
      List<String> args = FunctionCallUtils.getFunctionParameters(functionCall);
      if (args == null) {
         throw invalidFunctionCallException(functionCall);
      }
      
      // Construct the function symbol and the parameter objects.
      String templateString = findTemplateString(templateName);
      List<ITerm> parameters = new ArrayList<ITerm>();
      for (String arg : args) {
         parameters.add(getColumnExpression(arg));
      }
      return getMappingObjectFactory().createUriTemplate(templateString, parameters);
   }

   protected URI getUri(String abbreviatedUri) throws PrefixNotFoundException
   {
      String normalizedAbbreviatedUri = normalizedAbbreviatedUri(abbreviatedUri);
      int colonPos = normalizedAbbreviatedUri.indexOf(":");
      String prefixName = normalizedAbbreviatedUri.substring(0, colonPos);
      String localName = normalizedAbbreviatedUri.substring(colonPos + 1);
      String namespace = getPrefixMapper().get(prefixName);
      if (!StringUtils.isEmpty(namespace)) {
         return URI.create(namespace + localName);
      }
      else {
         throw prefixNotFoundException(prefixName);
      }
   }

   protected IUriReference getUriReference(String abbreviatedUri) throws PrefixNotFoundException
   {
      URI value = getUri(abbreviatedUri);
      return getExpressionObjectFactory().getUriReference(value);
   }

   /*
    * Private helper methods
    */

   private String normalizedAbbreviatedUri(String input)
   {
      if (input.indexOf(":") != -1) { //$NON-NLS-1$
         return input;
      }
      else {
         return ":" + input; //$NON-NLS-1$
      }
   }

   private String findTemplateString(String templateName) throws TemplateNotFoundException
   {
      if (getUriTemplateMapper().containsKey(templateName)) {
         return getUriTemplateMapper().get(templateName);
      }
      throw templateNotFoundException(templateName);
   }
}
