package com.obidea.semantika.mapping.base.sql;

import com.obidea.semantika.database.sql.base.ISqlExpression;
import com.obidea.semantika.database.sql.base.ISqlExpressionVisitor;
import com.obidea.semantika.datatype.DataType;
import com.obidea.semantika.mapping.base.sql.SqlUnaryFunction;

public class SqlCastString extends SqlUnaryFunction
{
   private static final long serialVersionUID = 629451L;

   public SqlCastString(ISqlExpression expression)
   {
      super("CAST_STR", DataType.STRING, expression); //$NON-NLS-1$
   }

   @Override
   public String getStringExpression()
   {
      return "CAST"; //$NON-NLS-1$
   }

   @Override
   public void accept(ISqlExpressionVisitor visitor)
   {
      visitor.visit(this);
   }
}
