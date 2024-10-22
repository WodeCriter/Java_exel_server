package engine.expressions.imp.Math;

import engine.effectivevalue.api.EffectiveValue;
import engine.effectivevalue.imp.EffectiveValueImp;
import engine.expressions.api.Expression;
import engine.spreadsheet.api.Sheet;
import engine.spreadsheet.cell.api.Cell;
import engine.spreadsheet.cell.api.CellType;

import java.util.List;

public class SumExpression implements Expression
{
    String rangeName;

    public SumExpression(String rangeName)
    {
        if (rangeName == null || rangeName.isEmpty())
            throw new IllegalArgumentException("Range must have a name");
        this.rangeName = rangeName;
    }

    @Override
    public EffectiveValue eval(Sheet sheet)
    {
        if (!sheet.isRangeInDatabase(rangeName))
            throw new IllegalArgumentException("Range does not exist in the database.");

        List<Cell> cells = sheet.getRange(rangeName).getCellsInRange();
        double sum = 0;

        for (Cell cell : cells)
        {
            EffectiveValue value = cell.getEffectiveValue();
            if (value.getCellType() == CellType.NUMERIC)
                sum += value.extractValueWithExpectation(Double.class);
        }

        return new EffectiveValueImp(CellType.NUMERIC, sum);
    }

    @Override
    public CellType getFunctionResultType()
    {
        return CellType.NUMERIC;
    }
}
