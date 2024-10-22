package engine.expressions.imp.Boolean.Logic;

import engine.effectivevalue.api.EffectiveValue;
import engine.effectivevalue.imp.EffectiveValueImp;
import engine.expressions.api.Expression;
import engine.spreadsheet.api.Sheet;
import engine.spreadsheet.cell.api.CellType;

public class AndExpression implements Expression
{
    private Expression arg1;
    private Expression arg2;

    public AndExpression(Expression arg1, Expression arg2)
    {
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    @Override
    public EffectiveValue eval(Sheet sheet)
    {
        EffectiveValue v1 = arg1.eval(sheet);
        EffectiveValue v2 = arg2.eval(sheet);

        if (v1.getCellType() != CellType.BOOLEAN || v2.getCellType() != CellType.BOOLEAN)
            return new EffectiveValueImp(CellType.UNDEFINED, UNDEFINED_BOOLEAN);

        boolean bool1 = v1.extractValueWithExpectation(Boolean.class);
        boolean bool2 = v2.extractValueWithExpectation(Boolean.class);

        return new EffectiveValueImp(CellType.BOOLEAN,bool1 && bool2);
    }

    @Override
    public CellType getFunctionResultType()
    {
        return CellType.BOOLEAN;
    }
}
