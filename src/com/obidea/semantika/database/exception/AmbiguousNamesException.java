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
package com.obidea.semantika.database.exception;

import java.util.List;

import com.obidea.semantika.database.base.IDatabaseObject;

public class AmbiguousNamesException extends InternalDatabaseException
{
   private static final long serialVersionUID = 629451L;

   private List<? extends IDatabaseObject> mMultipleObjectsFound;

   public AmbiguousNamesException(String inputName, List<? extends IDatabaseObject> multipleObjectsFound)
   {
      super("Found other similar names for \"" + inputName + "\"");
      mMultipleObjectsFound = multipleObjectsFound;
   }

   @Override
   public String getMessage()
   {
      StringBuilder msg = new StringBuilder();
      msg.append(super.getMessage());
      msg.append("\n"); //$NON-NLS-1$
      msg.append("Possible name found:\n"); //$NON-NLS-1$
      int counter = 1;
      for (IDatabaseObject dbo : mMultipleObjectsFound) {
         msg.append(counter + ". "); //$NON-NLS-1$
         msg.append(dbo.getFullName());
         msg.append("\n"); //$NON-NLS-1$
         counter++;
      }
      return msg.toString();
   }
}
