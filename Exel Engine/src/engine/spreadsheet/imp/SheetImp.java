package engine.spreadsheet.imp;

import engine.spreadsheet.api.Sheet;
import engine.spreadsheet.cell.api.Cell;
import engine.spreadsheet.cell.api.ReadOnlyCell;
import engine.spreadsheet.cell.imp.CellImp;
import engine.spreadsheet.cell.imp.ReadOnlyCellImp;
import engine.spreadsheet.coordinate.Coordinate;
import engine.spreadsheet.range.Range;
import engine.spreadsheet.range.RangeDatabase;
import engine.spreadsheet.range.ReadOnlyRange;
import engine.spreadsheet.versionmanager.imp.VersionManager;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SheetImp implements Sheet, Serializable
{
    private static final long serialVersionUID = 1L;
    private Map<Coordinate, CellImp> activeCells;
    private String sheetName;
    private int version;
    private VersionManager versionManager;
    private int cellHeight;
    private int cellWidth;
    private int numOfCols;
    private int numOfRows;
    private RangeDatabase rangeDatabase;

    public SheetImp(int cellHeight, int cellWidth, int numOfCols, int numOfRows, String sheetName)
    {
        this.activeCells = new ConcurrentHashMap<>();
        this.sheetName = sheetName;
        this.cellHeight = cellHeight;
        this.cellWidth = cellWidth;
        this.numOfCols = numOfCols;
        this.numOfRows = numOfRows;
        this.version = 1;
        this.rangeDatabase = new RangeDatabase();
        this.versionManager = new VersionManager(this.copySheet());
    }

    @Override
    public String getName()
    {
        return sheetName;
    }

    @Override
    public List<Cell> getCells()
    {
        return new ArrayList<>(activeCells.values());
    }

    @Override
    public List<ReadOnlyCell> getReadOnlyCells() {
        return activeCells.values().stream()
                .map(this::convertCellToReadOnlyCell).collect(Collectors.toList());
    }

    private ReadOnlyCell convertCellToReadOnlyCell(Cell cell) {
        return new ReadOnlyCellImp(cell);
    }

    @Override
    public int getVersion()
    {
        return version;
    }

    @Override
    public Sheet getSheetByVersion(int version)
    {
        return versionManager.getSheetByVersion(version);
    }

    @Override
    public List<Integer> getNumOfChangesInEachVersion()
    {
        return versionManager.getNumOfChangesInEachVersion();
    }

    public void rebase(){
       this.versionManager.setBaseSheet(this.copySheet(), this.getCells());
    }

    @Override
    public CellImp getCell(Coordinate coordinate)
    {
        CellImp cellToReturn = activeCells.get(coordinate);
        if (cellToReturn == null)
            cellToReturn = (CellImp) setCell(coordinate, "");

        return cellToReturn;
    }

    @Override
    public int getMaxNumOfCells()
    {
        return numOfCols * numOfRows;
    }

    @Override
    public boolean isCellActive(Coordinate coordinate)
    {
        return activeCells.containsKey(coordinate);
    }

    @Override
    public Cell setCell(String coordinate, String value) throws IllegalArgumentException
    {
        return setCell(new Coordinate(coordinate), value);
    }

    @Override
    public Cell setCell(Coordinate coordinate, String value) throws IllegalArgumentException
    {
        if (isCoordinateInRange(coordinate))
        {
            Cell cell = activeCells.computeIfAbsent(coordinate, cord -> new CellImp(cord, this));
            cell.setCellOriginalValue(value);
            return cell;
        }
        else {
            throw new IllegalArgumentException("Cell Coordinate outside of range");
        }
    }

    public int getCellHeight()
    {
        return cellHeight;
    }

    public int getCellWidth()
    {
        return cellWidth;
    }

    public void setCellWidth(int width) {this.cellWidth = width;}
    public void setCellHeight(int height) {this.cellHeight = height;}

    public int getNumOfCols()
    {
        return numOfCols;
    }

    public int getNumOfRows()
    {
        return numOfRows;
    }

    @Override
    public Sheet updateCellValueAndCalculate(Coordinate coordinate, String newValue) throws Exception
    {
        CellImp cell = getCell(coordinate);
        if (cell == null) throw new IllegalArgumentException("Cell " + coordinate + " not found in map.");
        List<Cell> orderedCells = cell.setOriginalValueIfPossible(newValue);

        version++;
        orderedCells.forEach(Cell::calculateEffectiveValue);
        orderedCells.forEach(orderedCell -> orderedCell.setVersion(version));
        versionManager.recordChanges(orderedCells);
        passVersionManager(versionManager);

        return this;
    }

    public SheetImp copySheet() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.close();

            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            return (SheetImp) ois.readObject();
        } catch (Exception e) {
            // deal with the runtime error that was discovered as part of invocation
            return this;
        }
    }

    @Override
    public Boolean isCoordinateInRange(Coordinate coordinate) {
        if (coordinate == null)
            return false; // Early return for null or empty string input.

        // Split the string into the alphabetic part and the numeric part.
        int column = coordinate.getColIndex();
        int row = coordinate.getRow();

        // Check if the column index and row index are within the allowed range.
        return column > 0 && column <= this.numOfCols && row > 0 && row <= this.numOfRows;
    }

    private void passVersionManager(VersionManager newMan){
        this.versionManager = newMan;
    }



    @Override
    public void addRange(String rangeName, Range range)
    {
        rangeDatabase.addRange(rangeName, range);
    }

    @Override
    public boolean isRangeInDatabase(String rangeName)
    {
        return rangeDatabase.isRangeInDatabase(rangeName);
    }

    @Override
    public void removeRange(String rangeName)
    {
        rangeDatabase.removeRange(rangeName);
    }

    @Override
    public Range getRange(String rangeName)
    {
        return rangeDatabase.getRange(rangeName);
    }

    @Override
    public Range getRangeAndCountUse(String rangeName)
    {
        return rangeDatabase.getRangeAndCountUse(rangeName);
    }

    @Override
    public List<ReadOnlyRange> getReadOnlyRanges(){
        return rangeDatabase.getReadOnlyRanges();
    }

    @Override
    public Map<String, Range> getNameAndRangesMap() {return rangeDatabase.getNameAndRangesMap();}

//    @Override
//    public String getRangeName(Range range){
//        return rangeDatabase.getRangeName(range);
//    }
}



