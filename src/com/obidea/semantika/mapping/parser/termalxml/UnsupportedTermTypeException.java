package com.obidea.semantika.mapping.parser.termalxml;

import com.obidea.semantika.mapping.exception.MappingParserException;

public class UnsupportedTermTypeException extends MappingParserException
{
   private static final long serialVersionUID = 629451L;

   public UnsupportedTermTypeException(String message, int lineNumber, int columnNumber)
   {
      super(message, lineNumber, columnNumber);
   }
}
