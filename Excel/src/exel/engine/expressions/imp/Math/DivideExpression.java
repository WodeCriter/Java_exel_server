package exel.engine.expressions.imp.Math;

import exel.engine.effectivevalue.api.EffectiveValue;
import exel.engine.effectivevalue.imp.EffectiveValueImp;
import exel.engine.expressions.api.Expression;
import exel.engine.spreadsheet.api.Sheet;
import exel.engine.spreadsheet.cell.api.CellType;

public class DivideExpression implements Expression {
    private Expression left;
    private Expression right;

    public DivideExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public EffectiveValue eval(Sheet sheet) {
        EffectiveValue leftValue = left.eval(sheet);
        EffectiveValue rightValue = right.eval(sheet);

        if (leftValue.getCellType() != CellType.NUMERIC || rightValue.getCellType() != CellType.NUMERIC)
            return new EffectiveValueImp(CellType.UNDEFINED, UNDEFINED_NUMBER);

        double rightNum = rightValue.extractValueWithExpectation(Double.class);
        if (rightNum == 0)
            throw new RuntimeException("Cannot divide by zero");

        double result = leftValue.extractValueWithExpectation(Double.class) / rightNum;
        return new EffectiveValueImp(CellType.NUMERIC, result);
    }

    @Override
    public CellType getFunctionResultType()
    {
        return CellType.NUMERIC;
    }
}
