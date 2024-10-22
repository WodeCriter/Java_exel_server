package engine.spreadsheet.rowSorter;

import engine.spreadsheet.api.ReadOnlySheet;
import engine.spreadsheet.api.Sheet;
import engine.spreadsheet.cell.api.Cell;
import engine.spreadsheet.range.Range;

import java.util.*;

public abstract class RowSomething
{
    private final Range range;
    private List<Row> rows;
    private int minRowNum, maxRowNum;
    private Sheet sheet;

    public RowSomething(Range range, Sheet sheet)
    {
        this.range = range;
        findMinAndMaxRowNum();
        this.sheet = sheet;
    }

    private void findMinAndMaxRowNum()
    {
        minRowNum = getRange().getTopLeft().getRow();
        maxRowNum = getRange().getBottomRight().getRow();
    }

    protected Range getRange(){
        return range;
    }

    protected List<Row> getRows() {
        return rows;
    }

    protected void setRows(List<Row> rows){
        this.rows = rows;
    }

    protected int getMinRowNum(){
        return minRowNum;
    }

    protected int getMaxRowNum(){
        return maxRowNum;
    }

    protected Sheet getSheet()
    {
        return sheet;
    }

    private List<Row> convertCellsListToRowsList(List<Cell> cells, List<Integer> colsToSortFrom)
    {
        Map<Integer, List<Cell>> fromRowNumToListOfCellsInRow = new HashMap<>();

        for (Cell cell : cells)
        {
            int rowNum = cell.getCoordinate().getRow();
            fromRowNumToListOfCellsInRow.computeIfAbsent(rowNum, k -> new LinkedList<>()).add(cell);
        }

        List<Row> rowList = new ArrayList<>(maxRowNum - minRowNum + 1);
        for (int row = minRowNum; row <= maxRowNum; row++)
        {
            List<Cell> cellsInRow = fromRowNumToListOfCellsInRow.get(row);

            if (cellsInRow != null)
                rowList.add(new Row(cellsInRow, colsToSortFrom));
        }

        return rowList;
    }

    protected List<Row> convertCellsListToRowsList(List<Integer> colsToSortFrom)
    {
        return convertCellsListToRowsList(range.getCellsInRange(), colsToSortFrom);
    }

    protected void fixCellCoordinatesAfterChange()
    {
        int rowNum = getMinRowNum();
        for (Row row : getRows())
        {
            row.changeRowNum(rowNum);
            rowNum++;
        }
    }

    public abstract void changeSheet();
    public abstract void returnSheetBackToNormal();
    public abstract ReadOnlySheet getSheetAfterChange();
}
