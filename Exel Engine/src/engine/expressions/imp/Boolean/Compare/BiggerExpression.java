package engine.expressions.imp.Boolean.Compare;

import engine.effectivevalue.api.EffectiveValue;
import engine.effectivevalue.imp.EffectiveValueImp;
import engine.expressions.api.Expression;
import engine.spreadsheet.api.Sheet;
import engine.spreadsheet.cell.api.CellType;

public class BiggerExpression implements Expression
{
    private Expression arg1;
    private Expression arg2;

    public BiggerExpression(Expression arg1, Expression arg2)
    {
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    @Override
    public EffectiveValue eval(Sheet sheet)
    {
        EffectiveValue v1 = arg1.eval(sheet);
        EffectiveValue v2 = arg2.eval(sheet);

        if (v1.getCellType() != CellType.NUMERIC || v2.getCellType() != CellType.NUMERIC)
            return new EffectiveValueImp(CellType.UNDEFINED, UNDEFINED_BOOLEAN);

        double num1 = v1.extractValueWithExpectation(Double.class);
        double num2 = v2.extractValueWithExpectation(Double.class);

        return new EffectiveValueImp(CellType.BOOLEAN,num1 >= num2);
    }

    @Override
    public CellType getFunctionResultType()
    {
        return CellType.BOOLEAN;
    }
}
