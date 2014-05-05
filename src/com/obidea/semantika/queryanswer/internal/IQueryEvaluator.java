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
package com.obidea.semantika.queryanswer.internal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.obidea.semantika.exception.SemantikaException;

public interface IQueryEvaluator
{
   PreparedStatement prepareQueryStatement(String sql) throws SQLException, SemantikaException;

   void closeQueryStatement(PreparedStatement ps, ResultSet rs) throws SQLException;

   ResultSet getResultSet(PreparedStatement ps) throws SQLException;

   void setTransactionTimeout(int seconds);

   void setTransactionFetchSize(int fetchSize);

   void setTransactionMaxRows(int maxRows);

   void unsetTransactionTimeout();

   void unsetTransactionFetchSize();

   void unsetTransactionMaxRows();
}
