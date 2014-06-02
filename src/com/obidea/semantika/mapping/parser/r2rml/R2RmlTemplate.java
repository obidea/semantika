package com.obidea.semantika.mapping.parser.r2rml;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.obidea.semantika.util.StringUtils;

public class R2RmlTemplate
{
   private static final Pattern columnInCurlyBraces = Pattern.compile("\\{([^\\}]+)\\}");

   private int mIndex = 1;

   private String mTemplateString;
   private List<String> mColumnNames = new ArrayList<String>();

   public R2RmlTemplate(String templateString)
   {
      process(templateString);
   }

   public String getTemplateString()
   {
      return mTemplateString;
   }

   public List<String> getColumnNames()
   {
      return mColumnNames;
   }

   private void process(String templateString)
   {
      Matcher m = columnInCurlyBraces.matcher(templateString);
      while (m.find()) {
         String arg = m.group(2);
         if (!StringUtils.isEmpty(arg)) {
            mTemplateString.replace(arg, mIndex+"");
            mColumnNames.add(arg);
            mIndex++;
         }
      }
   }
}
