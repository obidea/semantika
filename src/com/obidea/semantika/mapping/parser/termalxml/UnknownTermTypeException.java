package com.obidea.semantika.mapping.parser.termalxml;

import com.obidea.semantika.mapping.exception.MappingParserException;

public class UnknownTermTypeException extends MappingParserException
{
   private static final long serialVersionUID = 629451L;

   public UnknownTermTypeException(String message, int lineNumber, int columnNumber)
   {
      super(message, lineNumber, columnNumber);
   }
}
