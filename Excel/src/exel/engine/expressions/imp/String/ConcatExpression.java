package exel.engine.expressions.imp.String;

import exel.engine.effectivevalue.api.EffectiveValue;
import exel.engine.effectivevalue.imp.EffectiveValueImp;
import exel.engine.expressions.api.Expression;
import exel.engine.spreadsheet.api.Sheet;
import exel.engine.spreadsheet.cell.api.CellType;

public class ConcatExpression implements Expression
{
    private final Expression left, right;

    public ConcatExpression(Expression left, Expression right)
    {
        this.left = left;
        this.right = right;
    }

    @Override
    public EffectiveValue eval(Sheet sheet)
    {
        EffectiveValue leftValue = left.eval(sheet);
        EffectiveValue rightValue = right.eval(sheet);

        if (leftValue.getCellType() != CellType.STRING || rightValue.getCellType() != CellType.STRING )
            return new EffectiveValueImp(CellType.UNDEFINED, UNDEFINED_STRING);

        String leftStr = leftValue.extractValueWithExpectation(String.class);
        String rightStr = rightValue.extractValueWithExpectation(String.class);

        return new EffectiveValueImp(CellType.STRING, leftStr + rightStr);
    }

    @Override
    public CellType getFunctionResultType()
    {
        return CellType.STRING;
    }
}
