package engine.spreadsheet.rowSorter;

import engine.spreadsheet.api.ReadOnlySheet;
import engine.spreadsheet.api.Sheet;
import engine.spreadsheet.imp.ReadOnlySheetImp;
import engine.spreadsheet.range.Range;

import java.util.*;

public class RowSorter extends RowSomething
{
    private final List<Integer> colsToSortFrom;

    public RowSorter(Range range, Sheet sheet, List<Integer> colsToSortFrom)
    {
        super(range, sheet);
        this.colsToSortFrom = colsToSortFrom;
        setRows(convertCellsListToRowsList(colsToSortFrom));
    }

    @Override
    public void changeSheet() //sorts
    {
        getRows().sort(Row::compareTo);
        fixCellCoordinatesAfterChange();
    }

    @Override
    public void returnSheetBackToNormal()
    {
        getRows().forEach(Row::setBackToOriginalRowNum);
    }

    @Override
    public ReadOnlySheet getSheetAfterChange()
    {
        changeSheet();
        ReadOnlySheet toReturn = new ReadOnlySheetImp(getSheet());
        returnSheetBackToNormal();
        return toReturn;
    }
}
