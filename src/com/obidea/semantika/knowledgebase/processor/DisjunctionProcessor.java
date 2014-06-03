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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.obidea.semantika.database.sql.base.ISqlExpression;
import com.obidea.semantika.knowledgebase.model.IKnowledgeBase;
import com.obidea.semantika.mapping.MutableMappingSet;
import com.obidea.semantika.mapping.base.IMapping;
import com.obidea.semantika.mapping.base.TripleAtom;
import com.obidea.semantika.mapping.base.sql.SqlAnd;
import com.obidea.semantika.mapping.base.sql.SqlOr;
import com.obidea.semantika.mapping.base.sql.SqlQuery;
import com.obidea.semantika.util.Serializer;

/**
 * A mapping optimization processor that "merge" similar mappings' query using
 * disjunction (i.e., OR operator).
 * <p>
 * Input:<pre>
 * A(a) <^- T1(a, b, c), &eq(c, 1)
 * A(a) <^- T1(a, b, c), &eq(c, 2)
 * B(p) <^- T2(p, q, r) </pre>
 * <p>
 * Output:<pre>
 * A(a) <^- T1(a, b, c), &or(&eq(c, 1), &eq(c, 2))
 * B(p) <^- T2(p, q, r) </pre>
 */
public class DisjunctionProcessor implements IKnowledgeBaseProcessor
{
   public DisjunctionProcessor()
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
      MutableMappingSet mappingSet = mutableMappingSet(kb);
      List<IMapping> removeList = new ArrayList<IMapping>();
      List<IMapping> addList = new ArrayList<IMapping>();
      
      for (URI signature : mappingSet.getMappingSignatures()) {
         /*
          * The optimization only applies for target mappings that have the same signature.
          */
         Set<IMapping> targetMappings = mappingSet.get(signature);
         if (targetMappings.size() == 1) {
            continue; // skip if the size == 1
         }
         
         /*
          * Make a copy of the target mappings. The method will work on the copied mappings.
          */
         List<IMapping> workingMappings = makeCopyMappings(targetMappings);
         
         /*
          * Do iterative comparison between a reference mapping and its successive mappings
          * in the working mappings. "Merge" both mappings' query using disjunction operator
          * if possible.
          */
         for (int i = 0; i < workingMappings.size(); i++) {
            TripleAtom referenceEntity = workingMappings.get(i).getTargetAtom(); // reference mapping
            for (int j = (i+1); j < workingMappings.size(); ) {
               TripleAtom currentEntity = workingMappings.get(j).getTargetAtom(); // current mapping
               /*
                * Check first if the current mapping has the same entity atom with the reference
                * mapping.
                */
               if (currentEntity.equals(referenceEntity)) {
                  /*
                   * Apply the disjunction. Remove the current mapping if it applies or try
                   * the next mapping, otherwise.
                   */
                  SqlQuery query1 = workingMappings.get(i).getSourceQuery();
                  SqlQuery query2 = workingMappings.get(j).getSourceQuery();
                  if (applyDisjunction(query1, query2)) {
                     workingMappings.remove(j);
                     continue;
                  }
               }
               j++; // advance to the next test query
            }
         }
         /*
          * The candidate mappings are going to be removed later and will be replaced by
          * the substitute mappings.
          */
         removeList.addAll(targetMappings);
         addList.addAll(workingMappings);
      }
      /*
       * Update the mapping set
       */
      mappingSet.removeAll(removeList);
      mappingSet.addAll(addList);
   }

   private static boolean applyDisjunction(SqlQuery targetQuery, SqlQuery otherQuery)
   {
      if (!targetQuery.getBody().equals(otherQuery.getBody())) {
         return false;
      }
      ISqlExpression f1 = normalizedFilters(targetQuery.getWhereExpression());
      ISqlExpression f2 = normalizedFilters(otherQuery.getWhereExpression());
      ISqlExpression disjunction = createDisjunction(f1, f2);
      targetQuery.resetFilters();
      targetQuery.addWhereExpression(disjunction);
      return true;
   }

   /*
    * Redefine the filter functions as a single function using conjunction (i.e., AND operator).
    */
   private static ISqlExpression normalizedFilters(Set<ISqlExpression> filters)
   {
      if (filters.isEmpty()) {
         return null;
      }
      Iterator<ISqlExpression> iter = filters.iterator();
      ISqlExpression rightExpression = iter.next();
      while (iter.hasNext()) {
         ISqlExpression leftExpression = iter.next();
         rightExpression = new SqlAnd(leftExpression, rightExpression);
      }
      return rightExpression;
   }

   private static ISqlExpression createDisjunction(ISqlExpression leftExpression, ISqlExpression rightExpression)
   {
      if (leftExpression == null) {
         return rightExpression;
      }
      else if (rightExpression == null) {
         return leftExpression;
      }
      else {
         return new SqlOr(leftExpression, rightExpression);
      }
   }

   private static List<IMapping> makeCopyMappings(Set<IMapping> candidateMappings)
   {
      List<IMapping> toReturn = new ArrayList<IMapping>();
      for (IMapping mapping : candidateMappings) {
         toReturn.add((IMapping) Serializer.copy(mapping));
      }
      return toReturn;
   }

   @Override
   public String getName()
   {
      return "Disjuction processor"; //$NON-NLS-1$
   }
}
