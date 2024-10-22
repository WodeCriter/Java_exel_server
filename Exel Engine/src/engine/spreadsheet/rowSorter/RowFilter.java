package engine.spreadsheet.rowSorter;

import engine.effectivevalue.api.EffectiveValue;
import engine.spreadsheet.range.Range;
import engine.spreadsheet.api.ReadOnlySheet;
import engine.spreadsheet.api.Sheet;
import engine.spreadsheet.cell.api.ReadOnlyCell;
import engine.spreadsheet.imp.ReadOnlySheetImp;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RowFilter extends RowSomething
{
    private Map<Integer, List<EffectiveValue>> colNumToValueToFilerByMap;

    public RowFilter(Range range, Sheet sheet, Map<Integer, List<EffectiveValue>> colNumToValueToFilerByMap)
    {
        super(range, sheet);
        this.colNumToValueToFilerByMap = colNumToValueToFilerByMap;
        setRows(convertCellsListToRowsList(null));
    }

    @Override
    public void changeSheet()
    {
        setRows(getRows().stream().filter(this::doesEveryColumnInRowHaveOneOfTheRequestedValues).toList());
    }

    private boolean doesColumnHaveOneOfTheRequestedValues(Row cellRow, int cellColumn){
        List<EffectiveValue> effectiveValues = colNumToValueToFilerByMap.get(cellColumn);
        for (EffectiveValue value : effectiveValues)
        {
            if (cellRow.checkIfCellInRowHasEffectiveValue(cellColumn, value))
                return true;
        }
        return false;
    }

    private boolean doesEveryColumnInRowHaveOneOfTheRequestedValues(Row cellRow){
        for (int column : colNumToValueToFilerByMap.keySet())
        {
            if (!doesColumnHaveOneOfTheRequestedValues(cellRow, column))
                return false;
        }
        return true;
    }

    @Override
    public void returnSheetBackToNormal()
    {

    }

    @Override
    public ReadOnlySheet getSheetAfterChange()
    {
        changeSheet();
        return new ReadOnlySheetImp(getSheet().getVersion(), convertRowsListToReadOnlyCellsList(), getSheet().getName(),
                getSheet().getNumOfCols(), getRows().size(), getSheet().getCellWidth(), getSheet().getCellHeight(),
                getSheet().getReadOnlyRanges());
    }

    private List<ReadOnlyCell> convertRowsListToReadOnlyCellsList()
    {
        List<ReadOnlyCell> readOnlyCells = new LinkedList<>();
        for (int row = 0; row < getRows().size(); row++)
        {
            readOnlyCells.addAll(getRows().get(row).getReadOnlyCellsInRowWithChangedRowNum(row + 1));
        }
        return readOnlyCells;
    }
}
