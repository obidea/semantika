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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.obidea.semantika.mapping.parser.IMappingParserFactory;

public class KnowledgeBaseProcessorRegistry
{
   private static KnowledgeBaseProcessorRegistry sInstance;

   private List<IKnowledgeBaseProcessor> mProcessor;

   public KnowledgeBaseProcessorRegistry()
   {
      mProcessor = new ArrayList<IKnowledgeBaseProcessor>();
   }

   public static KnowledgeBaseProcessorRegistry getInstance()
   {
      if (sInstance == null) {
         sInstance = new KnowledgeBaseProcessorRegistry();
      }
      return sInstance;
   }

   /**
    * Registers the knowledge base processor with priority such that the top
    * priority processor will be executed first.
    * 
    * @param priority
    *           The order priority. Priority = <code>0</code> has the highest
    *           priority.
    * @param processor
    *           The knowledge base processor
    */
   public void register(int priority, IKnowledgeBaseProcessor processor)
   {
      mProcessor.add(priority, processor);
   }

   /**
    * Registers the knowledge base processor with subsequent order.
    *
    * @param processor
    *           The knowledge base processor
    */
   public void register(IKnowledgeBaseProcessor processor)
   {
      mProcessor.add(processor);
   }

   /**
    * Removes the knowledge base processor from this registry.
    * 
    * @param processor
    *           The knowledge base processor to remove.
    */
   public void unregister(IMappingParserFactory processor)
   {
      mProcessor.remove(processor);
   }

   public List<IKnowledgeBaseProcessor> getProcessors()
   {
      return Collections.unmodifiableList(mProcessor);
   }

   public void clearParserFactories()
   {
      mProcessor.clear();
   }
}
