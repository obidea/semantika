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
package com.obidea.semantika.app;

import java.util.List;
import java.util.Properties;

import com.obidea.semantika.database.connection.IConnectionProvider;
import com.obidea.semantika.knowledgebase.IPrefixManager;
import com.obidea.semantika.knowledgebase.model.IKnowledgeBase;
import com.obidea.semantika.knowledgebase.processor.IKnowledgeBaseProcessor;

/**
 * An <code>IApplicationManager</code> manages the overall Semantika
 * application. It is the main point for creating, loading and accessing
 * Semantika Knowledge Base (SKB). The <code>IApplicationManager</code> also
 * manages the KB optimizers that are used by the system.
 */
public interface IApplicationManager
{
   String getApplicationName();

   Properties getSystemProperties();

   IKnowledgeBase getKnowledgeBase();

   IPrefixManager getPrefixManager();

   IConnectionProvider getConnectionProvider();

   List<IKnowledgeBaseProcessor> getKnowledgeBaseProcessors();
}
