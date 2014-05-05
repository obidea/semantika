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
package com.obidea.semantika.mapping.parser.termalxml;

import com.obidea.semantika.expression.base.Term;
import com.obidea.semantika.expression.base.TermUtils;

public class InvalidSubjectMappedTermException extends InvalidMappedTermException
{
   private static final long serialVersionUID = 629451L;

   public InvalidSubjectMappedTermException(Term term)
   {
      super(term);
   }

   @Override
   public String getMessage()
   {
      return String.format("Subject map doesn't accept %s (%s)", //$NON-NLS-1$
            TermUtils.getClass(mMappedTerm), TermUtils.toString(mMappedTerm));
   }
}
