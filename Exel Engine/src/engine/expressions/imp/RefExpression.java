package engine.expressions.imp;

import engine.expressions.api.Expression;
import engine.effectivevalue.api.EffectiveValue;
import engine.effectivevalue.imp.EffectiveValueImp;
import engine.spreadsheet.api.Sheet;
import engine.spreadsheet.cell.api.Cell;
import engine.spreadsheet.cell.api.CellType;
import engine.spreadsheet.coordinate.Coordinate;

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
