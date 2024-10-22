package engine.expressions.imp.Math;

import engine.effectivevalue.api.EffectiveValue;
import engine.effectivevalue.imp.EffectiveValueImp;
import engine.expressions.api.Expression;
import engine.spreadsheet.api.Sheet;
import engine.spreadsheet.cell.api.CellType;

public class AbsExpression implements Expression
{
    private Expression exp;

    public AbsExpression(Expression exp) {
        this.exp = exp;
    }

    @Override
    public EffectiveValue eval(Sheet sheet) {
        EffectiveValue expValue = exp.eval(sheet);

        if (expValue.getCellType() != CellType.NUMERIC)
            return new EffectiveValueImp(CellType.UNDEFINED, UNDEFINED_NUMBER);

        Double result = Math.abs(expValue.extractValueWithExpectation(Double.class));
        return new EffectiveValueImp(CellType.NUMERIC, result);
    }

    @Override
    public CellType getFunctionResultType()
    {
        return CellType.NUMERIC;
    }
}
