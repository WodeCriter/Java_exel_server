package engine.expressions.imp.Math;

import engine.effectivevalue.api.EffectiveValue;
import engine.effectivevalue.imp.EffectiveValueImp;
import engine.expressions.api.Expression;
import engine.spreadsheet.api.Sheet;
import engine.spreadsheet.cell.api.Cell;
import engine.spreadsheet.cell.api.CellType;

import java.util.List;

public class AverageExpression implements Expression
{
    String rangeName;

    public AverageExpression(String rangeName)
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
        boolean numericCellExist = false;

        for (Cell cell : cells)
        {
            EffectiveValue value = cell.getEffectiveValue();
            if (value.getCellType() == CellType.NUMERIC)
            {
                sum += value.extractValueWithExpectation(Double.class);
                numericCellExist = true;
            }
        }

        if (!numericCellExist)
            throw new IllegalArgumentException("Range does not contain cells with numeric values.");
        return new EffectiveValueImp(CellType.NUMERIC, sum/cells.size());
    }

    @Override
    public CellType getFunctionResultType()
    {
        return CellType.NUMERIC;
    }
}
