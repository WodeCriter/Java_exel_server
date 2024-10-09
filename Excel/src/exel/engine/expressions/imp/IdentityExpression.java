package exel.engine.expressions.imp;

import exel.engine.effectivevalue.api.EffectiveValue;
import exel.engine.effectivevalue.imp.EffectiveValueImp;
import exel.engine.expressions.api.Expression;
import exel.engine.spreadsheet.api.Sheet;
import exel.engine.spreadsheet.cell.api.CellType;

public class IdentityExpression implements Expression
{
    private final Object value;
    private final CellType type;

    public IdentityExpression(Object value, CellType type)
    {
        this.value = value;
        this.type = type;
    }

    @Override
    public EffectiveValue eval(Sheet sheet)
    {
        return new EffectiveValueImp(type, value);
    }

    @Override
    public CellType getFunctionResultType()
    {
        return type;
    }
}
