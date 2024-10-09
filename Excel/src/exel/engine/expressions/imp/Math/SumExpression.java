package exel.engine.expressions.imp.Math;

import exel.engine.effectivevalue.api.EffectiveValue;
import exel.engine.effectivevalue.imp.EffectiveValueImp;
import exel.engine.expressions.api.Expression;
import exel.engine.spreadsheet.api.Sheet;
import exel.engine.spreadsheet.cell.api.Cell;
import exel.engine.spreadsheet.cell.api.CellType;
import exel.engine.spreadsheet.range.Range;

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
