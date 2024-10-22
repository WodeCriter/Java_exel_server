package engine.expressions.imp.Math;

import engine.effectivevalue.api.EffectiveValue;
import engine.effectivevalue.imp.EffectiveValueImp;
import engine.expressions.api.Expression;
import engine.spreadsheet.api.Sheet;
import engine.spreadsheet.cell.api.CellType;

public class ModExpression implements Expression
{
    private Expression left;
    private Expression right;

    public ModExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public EffectiveValue eval(Sheet sheet) {
        EffectiveValue leftValue = left.eval(sheet);
        EffectiveValue rightValue = right.eval(sheet);

        if (leftValue.getCellType() != CellType.NUMERIC || rightValue.getCellType() != CellType.NUMERIC )
            return new EffectiveValueImp(CellType.UNDEFINED, UNDEFINED_NUMBER);

        double result = leftValue.extractValueWithExpectation(Double.class) % rightValue.extractValueWithExpectation(Double.class);

        return new EffectiveValueImp(CellType.NUMERIC, result);
    }

    @Override
    public CellType getFunctionResultType()
    {
        return CellType.NUMERIC;
    }
}
