package engine.expressions.imp.Math;

import engine.effectivevalue.api.EffectiveValue;
import engine.effectivevalue.imp.EffectiveValueImp;
import engine.expressions.api.Expression;
import engine.spreadsheet.api.Sheet;
import engine.spreadsheet.cell.api.CellType;

public class PowExpression implements Expression
{
    private Expression left;
    private Expression right;

    public PowExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public EffectiveValue eval(Sheet sheet) {
        EffectiveValue leftValue = left.eval(sheet);
        EffectiveValue rightValue = right.eval(sheet);

        if (leftValue.getCellType() != CellType.NUMERIC || rightValue.getCellType() != CellType.NUMERIC )
            return new EffectiveValueImp(CellType.UNDEFINED, UNDEFINED_NUMBER);

        double leftNum = leftValue.extractValueWithExpectation(Double.class);
        double rightNum = rightValue.extractValueWithExpectation(Double.class);

        if (leftNum == 0 && rightNum == 0)
            throw new RuntimeException("0 to the power of 0 is an undefined expression");

        double result = Math.pow(leftNum, rightNum);
        return new EffectiveValueImp(CellType.NUMERIC, result);
    }

    @Override
    public CellType getFunctionResultType()
    {
        return CellType.NUMERIC;
    }
}
