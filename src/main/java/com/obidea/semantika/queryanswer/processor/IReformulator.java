/*
 * Copyright (c) 2013-2015 Josef Hardi <josef.hardi@gmail.com>
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
package com.obidea.semantika.queryanswer.processor;

import com.obidea.semantika.expression.base.IQueryExt;
import com.obidea.semantika.expression.base.QuerySet;

public interface IReformulator
{
   public QuerySet<IQueryExt> reformulate(IQueryExt querySet) throws QueryReformulationException;

   public QuerySet<IQueryExt> reformulate(QuerySet<? extends IQueryExt> querySet) throws QueryReformulationException;

   public String getName();
}
