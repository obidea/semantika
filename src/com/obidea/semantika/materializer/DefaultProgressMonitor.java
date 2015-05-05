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
package com.obidea.semantika.materializer;

import org.slf4j.Logger;

import com.obidea.semantika.util.LogUtils;

public class DefaultProgressMonitor implements IProgressMonitor
{
   private static final Logger LOG = LogUtils.createLogger("semantika.materializer"); //$NON-NLS-1$

   private int mMax = 0;
   private int mCurrent = 0;

   @Override
   public void start(int max)
   {
      reset();
      mMax = max;
   }

   private void reset()
   {
      mMax = 0;
      mCurrent = 0;
   }

   @Override
   public void advanced()
   {
      // NO-OP
   }

   @Override
   public void advanced(int value)
   {
      mCurrent++;
      String message = String.format("Materializing... (%s/%s)", mCurrent, mMax); //$NON-NLS-1$
      LOG.info(message);
   }

   @Override
   public void finish()
   {
      LOG.info("Process completed."); //$NON-NLS-1$
   }
}
