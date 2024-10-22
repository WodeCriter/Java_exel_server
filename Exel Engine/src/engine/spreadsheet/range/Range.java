package engine.spreadsheet.range;

import engine.spreadsheet.coordinate.Coordinate;
import engine.spreadsheet.coordinate.CoordinateIterator;
import engine.spreadsheet.api.Sheet;
import engine.spreadsheet.cell.api.Cell;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Range implements Serializable
{
    private Coordinate topLeft, bottomRight;
    private final Sheet sheet;
    private int numOfUsages;

    public Range(Coordinate cellCord1, Coordinate cellCord2, Sheet sheet)
    {
        if (!sheet.isCoordinateInRange(cellCord1) || !sheet.isCoordinateInRange(cellCord2))
            throw new IllegalArgumentException("Not all coordinates given are in range.");
        topLeft = cellCord1;
        bottomRight = cellCord2;
        handleInvalidCellsInput();
        this.sheet = sheet;
        numOfUsages = 0;
    }

    public int getNumOfCellsInRange()
    {
        return (bottomRight.getRow() - topLeft.getRow() + 1) * (bottomRight.getColIndex() - topLeft.getColIndex() + 1);
    }

    private void handleInvalidCellsInput()
    {
        if (topLeft.getRow() <= bottomRight.getRow())
        {
            if (topLeft.getColIndex() > bottomRight.getColIndex())
            {
                String leftCol = topLeft.getCol();
                topLeft.setCol(bottomRight.getCol());
                bottomRight.setCol(leftCol);
            }
        }
        else
        {
            if (topLeft.getColIndex() <= bottomRight.getColIndex())
            {
                int leftRow = topLeft.getRow();
                topLeft.setRow(bottomRight.getRow());
                bottomRight.setRow(leftRow);
            }
            else
            {
                Coordinate tmp = topLeft;
                topLeft = bottomRight;
                bottomRight = tmp;
            }
        }
    }

    public Boolean isCoordinateInRange(Coordinate coordinate) {
        if (coordinate == null)
            return false; // Early return for null or empty string input.

        int column = coordinate.getColIndex();
        int row = coordinate.getRow();

        // Check if the column index and row index are within the allowed range.
        return column >= topLeft.getColIndex() &&
                column <= bottomRight.getColIndex() &&
                row >= topLeft.getRow() &&
                row <= bottomRight.getRow();
    }

    public List<Cell> getCellsInRange()
    {
//        if (getNumOfCellsInRange() < sheet.getMaxNumOfCells()/2)
//            return getCellsInRangeUsingIterator();
//        else
//            return sheet.getCells().stream().filter(cell -> isCoordinateInRange(cell.getCoordinate())).toList();
        return getCellsInRangeUsingIterator();
    }

    private List<Cell> getCellsInRangeUsingIterator()
    {
        List<Cell> cells = new LinkedList<>();
        CoordinateIterator iterator = new CoordinateIterator(topLeft, this);
        while (iterator.hasNext())
        {
            Coordinate coordinate = iterator.next();
            if (sheet.isCellActive(coordinate))
                cells.add(sheet.getCell(coordinate));
        }
        return cells;
    }

    public List<String> getCordsStrInRange()
    {
        List<String> cords = new LinkedList<>();
        CoordinateIterator iterator = new CoordinateIterator(topLeft, this);
        while (iterator.hasNext())
        {
            cords.add(iterator.next().toString());
        }
        return cords;
    }

    public Coordinate getTopLeft()
    {
        return topLeft;
    }

    public Coordinate getBottomRight()
    {
        return bottomRight;
    }

    public void countUseOfRange()
    {
        numOfUsages++;
    }

    //todo: Need to call this method when an expression that uses this range is removed
    public void removeUseOfRange()
    {
        numOfUsages--;
    }

    public boolean isRangeUsed()
    {
        return numOfUsages > 0;
    }

//    public String getRange(){
//        return sheet.getRan
//    }
}
