package exel.engine.expressions.imp.Boolean.Logic;

import exel.engine.effectivevalue.api.EffectiveValue;
import exel.engine.effectivevalue.imp.EffectiveValueImp;
import exel.engine.expressions.api.Expression;
import exel.engine.spreadsheet.api.Sheet;
import exel.engine.spreadsheet.cell.api.CellType;

public class NotExpression implements Expression {
    private Expression exp;

    public NotExpression(Expression exp)
    {
        this.exp = exp;
    }

    @Override
    public EffectiveValue eval(Sheet sheet)
    {
        EffectiveValue value = exp.eval(sheet);

        if (value.getCellType() != CellType.BOOLEAN)
            return new EffectiveValueImp(CellType.UNDEFINED, UNDEFINED_BOOLEAN);

        boolean bool = value.extractValueWithExpectation(Boolean.class);
        return new EffectiveValueImp(CellType.BOOLEAN, !bool);
    }

    @Override
    public CellType getFunctionResultType()
    {
        return CellType.BOOLEAN;
    }
}
