package exel.engine.expressions.imp;

import exel.engine.effectivevalue.api.EffectiveValue;
import exel.engine.effectivevalue.imp.EffectiveValueImp;
import exel.engine.expressions.api.Expression;
import exel.engine.spreadsheet.api.Sheet;
import exel.engine.spreadsheet.cell.api.Cell;
import exel.engine.spreadsheet.cell.api.CellType;
import exel.engine.spreadsheet.coordinate.Coordinate;

public class RefExpression implements Expression
{
    private final Coordinate coordinate;
    private CellType type;

    public RefExpression(String coordinate)
    {
        this.coordinate = new Coordinate(coordinate.toUpperCase());
        type = CellType.UNDEFINED;
    }

    @Override
    public EffectiveValue eval(Sheet sheet)
    {
        Cell cell = sheet.getCell(coordinate);

        if (cell.getEffectiveValue().getValue() == "")
        {
            type = CellType.STRING;
            return new EffectiveValueImp(CellType.UNDEFINED, UNDEFINED_STRING);
        }

        EffectiveValue value = cell.getEffectiveValue();
        type = value.getCellType();
        return value;
    }

    @Override
    public CellType getFunctionResultType()
    {
        return type;
    }
}
