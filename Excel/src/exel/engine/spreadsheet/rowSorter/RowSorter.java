package exel.engine.spreadsheet.rowSorter;

import exel.engine.spreadsheet.api.ReadOnlySheet;
import exel.engine.spreadsheet.api.Sheet;
import exel.engine.spreadsheet.imp.ReadOnlySheetImp;
import exel.engine.spreadsheet.range.Range;

import java.util.*;
import java.util.stream.Collectors;

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
