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
package com.obidea.semantika.knowledgebase;

import java.util.Set;

/**
 * A substitution binding V/T. This class stores a substitution map
 * <code>{T_1/t_1, ..., T_n/t_n}</code>, where each <code>T_i</code> is a term,
 * each <code>t_i</code> is another term distinct from <code>T_i</code> and the
 * terms <code>T_1, ..., T_i</code> are distinct. A term can be a variable, a
 * constant or a function.
 * 
 * Given that <code>x, y, a, b, f, g</code> are the alphabets, where
 * <code>x, y</code> are variable names, <code>a, b</code> are constants, and
 * <code>f, g</code> are function symbols The unifying substitution follows
 * these rules below:
 * <ol>
 * <li><code>{ x / y }</code> : <code>x</code> and <code>y</code> are aliased.</li>
 * <li><code>{ x / a }</code> : <code>x</code> is unified with the constant <code>a</code>.</li>
 * <li><code>{ x / f(y) }</code> : <code>x</code> is unified with the function <code>f(y)</code>.</li>
 * <li><code>{ a / a }</code> : A constant can unified by itself.</li>
 * <li><code>{ a / b }</code> : Fails. A constant can't unified by any different constants.</li>
 * <li><code>{ f(x) / f(y) }</code> : <code>x</code> and <code>y</code> are aliased.</li>
 * <li><code>{ f(x) / g(y) }</code> : Fails. <code>f</code> and <code>g</code> do not match.</li>
 * <li><code>{ f(x) / f(x, y) }</code> : Fails. The function has different arity.</li>
 * <li><code>{ f(x) / f(g(y)) }</code> : Unifies <code>x</code> with the term <code>g(y)</code>.</li>
 * <li><code>{ f(a, x) / f(a, b) }</code> : Function and constant symbols match, 
 *     <code>x</code> is unified with the constant <code>b</code></li>
 * </ol>
 * 
 * (Source: <code>http://en.wikipedia.org/wiki/Unification_(computer_science)</code>)
 */
public interface ISubstitutionBinding<V, T>
{
   public void put(V variable, T term);

   public void remove(V variable);

   public Set<V> getVariables();

   public boolean isBound(V variable);

   public T replace(V variable);

   public boolean isEmpty();
}
