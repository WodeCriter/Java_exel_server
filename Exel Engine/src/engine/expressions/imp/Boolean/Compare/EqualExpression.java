package engine.expressions.imp.Boolean.Compare;

import engine.effectivevalue.api.EffectiveValue;
import engine.effectivevalue.imp.EffectiveValueImp;
import engine.expressions.api.Expression;
import engine.spreadsheet.api.Sheet;
import engine.spreadsheet.cell.api.CellType;

public class EqualExpression implements Expression
{
    private Expression arg1;
    private Expression arg2;

    public EqualExpression(Expression arg1, Expression arg2)
    {
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    @Override
    public EffectiveValue eval(Sheet sheet)
    {
        EffectiveValue v1 = arg1.eval(sheet);
        EffectiveValue v2 = arg2.eval(sheet);

        return new EffectiveValueImp(CellType.BOOLEAN, v1.equals(v2));
    }

    @Override
    public CellType getFunctionResultType()
    {
        return CellType.BOOLEAN;
    }
}
