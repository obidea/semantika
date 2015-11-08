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
package com.obidea.semantika.queryanswer.processor;

import com.obidea.semantika.expression.base.IQueryExt;
import com.obidea.semantika.expression.base.QuerySet;
import com.obidea.semantika.mapping.base.sql.SqlQuery;

public interface IUnfolder
{
   public QuerySet<SqlQuery> unfold(IQueryExt query) throws QueryUnfoldingException;

   public QuerySet<SqlQuery> unfold(QuerySet<? extends IQueryExt> querySet) throws QueryUnfoldingException;

   public String getName();
}
