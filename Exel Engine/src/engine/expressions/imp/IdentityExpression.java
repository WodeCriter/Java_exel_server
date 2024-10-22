package engine.expressions.imp;

import engine.expressions.api.Expression;
import engine.effectivevalue.api.EffectiveValue;
import engine.effectivevalue.imp.EffectiveValueImp;
import engine.spreadsheet.api.Sheet;
import engine.spreadsheet.cell.api.CellType;

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
