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
package com.obidea.semantika.knowledgebase;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.obidea.semantika.expression.base.IFunction;
import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.expression.base.IVariable;
import com.obidea.semantika.expression.base.TermUtils;

public class TermSubstitutionBinding implements ISubstitutionBinding<IVariable, ITerm>
{
   private final Map<IVariable, ITerm> mBindings = new HashMap<IVariable, ITerm>();

   /**
    * Creates a blank unifier binding.
    */
   public TermSubstitutionBinding()
   {
      // NO-OP
   }

   public static TermSubstitutionBinding createEmptyBinding()
   {
      return new TermSubstitutionBinding();
   }

   @Override
   public void put(IVariable v, ITerm t)
   {
      mBindings.put(v, t);
   }

   @Override
   public void remove(IVariable v)
   {
      mBindings.remove(v);
   }

   /**
    * Get all variables in this binding.
    * 
    * @return a set of all variables.
    */
   @Override
   public Set<IVariable> getVariables()
   {
      return mBindings.keySet();
   }

   /**
    * Computes substitution composition.
    * <p>
    * Let <code>theta = {u_1/s_1, ..., u_m/s_m}</code> and
    * <code>sigma = {v_1/t_1, ..., v_n/t_n}</code> be substitutions, then the
    * composition <code>theta-sigma</code> of <code>theta</code> and
    * <code>sigma</code> is the substitutions obtained from the set:
    * 
    * <pre>
    * theta-sigma = {u_1/s_1#sigma, ..., u_m/s_m#sigma, v_1/t_1, ..., v_n/t_n}</pre>
    *
    * by deleting any binding <code>u_i/s_i#sigma</code> for which
    * <code>u_i = s_i#sigma</code> and deleting any binding <code>v_j/t_j</code>
    * for which <code>v_j element in {u_i, ..., u_m}</code>
    * <p>
    * 
    * @param other
    *           The other substitution to be composed with this substitution.
    */
   public void compose(TermSubstitutionBinding other)
   {
      /*
       * Applying sigma to theta substitution such that {u_1/s_1#sigma, ...,
       * u_m/s_m#sigma}
       */
      Set<IVariable> thetaVariables = getVariables();
      for (final IVariable u : thetaVariables) {
         ITerm s = mBindings.get(u);
         if (s instanceof IFunction) {
            // Apply the substitution recursively
            IFunction function = (IFunction) s;
            function.apply(other);
         }
         else if (s instanceof IVariable) {
            // Replace the variable accordingly if it is bounded
            IVariable var = (IVariable) s;
            if (other.isBound(var)) {
               mBindings.put(u, other.getTerm(var));
            }
         }
      }
      /*
       * Insert sigma substitution to the composition result, if its variable
       * doesn't contain in theta substitution, i.e., v_j element of {u_i, ..., u_m}
       */
      for (final IVariable v : other.getVariables()) {
         if (!thetaVariables.contains(v)) { //
            mBindings.put(v, other.getTerm(v));
         }
      }
      /*
       * Delete bindings in the composition result if the variable and term are
       * equals, i.e., u_i = s_i#sigma
       */
      Object[] binds = mBindings.entrySet().toArray();
      for (int i = 0; i < binds.length; i++) {
         @SuppressWarnings("unchecked")
         Entry<IVariable, ITerm> bind = (Entry<IVariable, ITerm>) binds[i];
         final IVariable var = bind.getKey();
         final ITerm term = bind.getValue();
         if (var.equals(term)) {
            mBindings.remove(var);
         }
      }
   }

   @Override
   public ITerm replace(IVariable v)
   {
      return mBindings.get(v);
   }

   public ITerm getTerm(IVariable v)
   {
      return mBindings.get(v);
   }

   @Override
   public boolean isBound(IVariable v)
   {
      return mBindings.containsKey(v);
   }

   @Override
   public boolean isEmpty()
   {
      return mBindings.isEmpty();
   }

   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();

      boolean needComma = false;
      for (IVariable v : getVariables()) {
         if (needComma) {
            sb.append(", ");
         }
         sb.append(TermUtils.toString(v));
         sb.append("/");
         sb.append(TermUtils.toString(mBindings.get(v)));
         needComma = true;
      }
      return "{" + sb.toString() + "}";
   }
}
