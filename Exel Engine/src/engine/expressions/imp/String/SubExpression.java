package engine.expressions.imp.String;

import engine.effectivevalue.api.EffectiveValue;
import engine.effectivevalue.imp.EffectiveValueImp;
import engine.expressions.api.Expression;
import engine.spreadsheet.api.Sheet;
import engine.spreadsheet.cell.api.CellType;

public class SubExpression implements Expression
{
    private final Expression source;
    private Expression startIndex, endIndex;

    public SubExpression(Expression source, Expression startIndex, Expression endIndex)
    {
        this.source = source;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public EffectiveValue eval(Sheet sheet)
    {
        EffectiveValue sourceValue = source.eval(sheet);
        EffectiveValue startValue = this.startIndex.eval(sheet);
        EffectiveValue endValue = this.endIndex.eval(sheet);

        if (sourceValue.getCellType() != CellType.STRING || startValue.getCellType() != CellType.NUMERIC || endValue.getCellType() != CellType.NUMERIC)
            return new EffectiveValueImp(CellType.UNDEFINED, UNDEFINED_STRING);

        String sourceStr = sourceValue.extractValueWithExpectation(String.class);
        int startIndex = startValue.extractValueWithExpectation(Double.class).intValue();
        int endIndex = endValue.extractValueWithExpectation(Double.class).intValue();

        if (startIndex > endIndex)
        {
            int tmp = startIndex;
            startIndex = endIndex;
            endIndex = tmp;
        }
        endIndex++;

        if (startIndex < 0 || endIndex < 0 || startIndex >= sourceStr.length() || endIndex >= sourceStr.length())
            return new EffectiveValueImp(CellType.UNDEFINED, UNDEFINED_STRING);

        return new EffectiveValueImp(CellType.STRING, sourceStr.substring(startIndex, endIndex));
    }

    @Override
    public CellType getFunctionResultType()
    {
        return CellType.STRING;
    }
}
