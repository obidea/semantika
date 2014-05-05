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
package com.obidea.semantika.knowledgebase.processor;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;

import com.obidea.semantika.database.IDatabase;
import com.obidea.semantika.database.IDatabaseMetadata;
import com.obidea.semantika.database.NamingUtils;
import com.obidea.semantika.database.base.IColumn;
import com.obidea.semantika.database.base.IColumnReference;
import com.obidea.semantika.database.base.IForeignKey;
import com.obidea.semantika.database.base.ITable;
import com.obidea.semantika.database.datatype.SqlTypeToXmlType;
import com.obidea.semantika.expression.ExpressionObjectFactory;
import com.obidea.semantika.expression.base.IAtom;
import com.obidea.semantika.expression.base.IRule;
import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.expression.base.IVariable;
import com.obidea.semantika.knowledgebase.TermSubstitutionBinding;
import com.obidea.semantika.knowledgebase.UnificationException;
import com.obidea.semantika.knowledgebase.model.IKnowledgeBase;
import com.obidea.semantika.mapping.MutableMappingSet;
import com.obidea.semantika.mapping.base.IMapping;
import com.obidea.semantika.util.CollectionUtils;
import com.obidea.semantika.util.LogUtils;
import com.obidea.semantika.util.MultiMap;

public class ReferentialIntegrityProcessor implements IKnowledgeBaseProcessor
{
   private List<IRule> mFkRules = new ArrayList<IRule>();

   private IDatabase mDatabase;

   private MutableMappingSet mMappingSet;

   private IgnoredMappings mIgnoredMappings = new IgnoredMappings();

   private static ExpressionObjectFactory sExpressionFactory = ExpressionObjectFactory.getInstance();

   private static final Logger LOG = LogUtils.createLogger("semantika.knowledgebase.processor"); //$NON-NLS-1$

   public ReferentialIntegrityProcessor()
   {
      // NO-OP
   }

   private static MutableMappingSet mutableMappingSet(IKnowledgeBase kb) throws KnowledgeBaseProcessorException
   {
      if (!(kb.getMappingSet() instanceof MutableMappingSet)) {
         throw new KnowledgeBaseProcessorException("Optimization requires mutable mapping set object"); //$NON-NLS-1$
      }
      return (MutableMappingSet) kb.getMappingSet();
   }

   @Override
   public void optimize(IKnowledgeBase kb) throws KnowledgeBaseProcessorException
   {
      mIgnoredMappings.clear();
      mMappingSet = mutableMappingSet(kb);
      mDatabase = kb.getDatabase();
      
      createForeignKeyRules();
      
      for (URI signature : mMappingSet.getMappingSignatures()) {
         Set<IMapping> mappings = mMappingSet.get(signature);
         if (mappings.size() == 1) { // skip if found a single mapping
            continue;
         }
         doOptimization(signature, mappings);
      }
      logIgnoreMappings();
   }

   private void doOptimization(URI signature, Set<IMapping> mappings)
   {
      List<IMapping> removeList = new ArrayList<IMapping>();
      List<IMapping> mappingList = CollectionUtils.toList(mappings);
      for (int i = 0; i < mappingList.size(); i++) {
         for (int j = 0; j < mappingList.size(); j++) {
            try {
               IMapping mapping1 = mappingList.get(i);
               IMapping mapping2 = mappingList.get(j);
               if (mapping1.equals(mapping2)) { // skip if both mappings are identical
                  continue;
               }
               if (MappingContainmentChecker.isContained(mapping1, mapping2, mFkRules)) {
                  removeList.add(mapping2);
               }
            }
            catch (MappingContaimentCheckException e) {
               String message = String.format("Failed to optimize %s (Reason: %s)", signature, e.getMessage());
               mIgnoredMappings.add(message, e.getInvalidMapping());
            }
         }
      }
      mMappingSet.removeAll(removeList);
   }

   private void createForeignKeyRules()
   {
      IDatabaseMetadata md = mDatabase.getMetadata();
      Set<IForeignKey> fkeys = md.getForeignKeys();
      for (IForeignKey fk : fkeys) {
         ITable referenceTable = null;
         ITable sourceTable = null;
         TermSubstitutionBinding theta = TermSubstitutionBinding.createEmptyBinding();
         for (IColumnReference cr : fk.getReferences()) {
            try {
               IColumn fkColumn = cr.getForeignKeyColumn();
               IColumn pkColumn = cr.getPrimaryKeyColumn();
               createSubstitutionBinding(fkColumn, pkColumn, theta);
               
               referenceTable = fkColumn.getTableOrigin();
               sourceTable = pkColumn.getTableOrigin();
            }
            catch (UnificationException e) {
               LOG.error("Error while creating foreign key rules"); //$NON-NLS-1$
               LOG.error("Detailed cause: {}", e.getMessage()); //$NON-NLS-1$
            }
         }
         IAtom ruleHead = createAtomFromTable(referenceTable);
         IAtom ruleBody = createAtomFromTable(sourceTable);
         ruleBody.apply(theta);
         mFkRules.add(sExpressionFactory.createRule(ruleHead, CollectionUtils.asList(ruleBody)));
      }
   }

   private static void createSubstitutionBinding(IColumn fkColumn, IColumn pkColumn, TermSubstitutionBinding theta) throws UnificationException
   {
      IVariable fkVariable = getVariableFromColumn(fkColumn);
      IVariable pkVariable = getVariableFromColumn(pkColumn);
      theta.put(pkVariable, fkVariable);
   }

   private IAtom createAtomFromTable(ITable table)
   {
      String name = getPredicateNameFromTable(table);
      List<ITerm> terms = new ArrayList<ITerm>();
      for (IColumn column : table.getColumns()) {
         terms.add(getVariableFromColumn(column));
      }
      return sExpressionFactory.createAtom(name, terms);
   }

   private static String getPredicateNameFromTable(ITable table)
   {
      return NamingUtils.constructExpressionObjectLabel(table.getSchemaName(), table.getLocalName());
   }

   private static IVariable getVariableFromColumn(IColumn column)
   {
      String variableName = NamingUtils.constructExpressionObjectLabel(column.getSchemaName(), column.getTableName(), column.getLocalName());
      String datatype = SqlTypeToXmlType.get(column.getSqlType());
      return sExpressionFactory.getVariable(variableName, datatype);
   }

   @Override
   public String getName()
   {
      return "ReferentialIntegrity processor"; //$NON-NLS-1$
   }

   private void logIgnoreMappings()
   {
      if (!mIgnoredMappings.isEmpty()) {
         MultiMap<String, IMapping> ignoreMap = mIgnoredMappings.asMap();
         for (String cause : ignoreMap.keySet()) {
            LOG.debug("  - {} ({} items)", cause, ignoreMap.get(cause).size()); //$NON-NLS-1$
         }
      }
   }

   /**
    * Utility class to store the ignored mappings when optimizing the mapping set using referential
    * integrity constraint (i.e., foreign key definition).
    */
   private class IgnoredMappings
   {
      private MultiMap<String, IMapping> mIgnoreMap = new MultiMap<String, IMapping>(true);

      public void add(String cause, IMapping mapping)
      {
         mIgnoreMap.put(cause, mapping);
      }

      public MultiMap<String, IMapping> asMap()
      {
         return mIgnoreMap;
      }

      public boolean isEmpty()
      {
         return mIgnoreMap.size() == 0;
      }

      public void clear()
      {
         mIgnoreMap.clear();
      }
   }
}
