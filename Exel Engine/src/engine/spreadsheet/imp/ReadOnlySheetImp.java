package engine.spreadsheet.imp;

import engine.spreadsheet.cell.imp.ReadOnlyCellImp;
import engine.spreadsheet.range.ReadOnlyRange;
import engine.spreadsheet.api.Sheet;
import engine.spreadsheet.cell.api.ReadOnlyCell;
import engine.spreadsheet.api.ReadOnlySheet;

import java.util.Collections;
import java.util.List;

public class ReadOnlySheetImp implements ReadOnlySheet {
    private final int version;
    private final List<ReadOnlyCell> cells;
    private final String name;
    private final int numOfCols;
    private final int numOfRows;
    private final int cellWidth;
    private final int cellHeight;
    private final List<ReadOnlyRange> ranges;

    public ReadOnlySheetImp(int version, List<ReadOnlyCell> cells, String name,
                            int numOfCols, int numOfRows, int cellWidth, int cellHeight, List<ReadOnlyRange> ranges) {
        this.version = version;
        this.cells = Collections.unmodifiableList(cells); // Ensure the list is unmodifiable
        this.name = name;
        this.numOfCols = numOfCols;
        this.numOfRows = numOfRows;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.ranges = Collections.unmodifiableList(ranges);
    }

    public ReadOnlySheetImp(Sheet sheet) {
        this(sheet.getVersion(), sheet.getReadOnlyCells(), sheet.getName(),
                sheet.getNumOfCols(), sheet.getNumOfRows(), sheet.getCellWidth(), sheet.getCellHeight(), sheet.getReadOnlyRanges());
    }

    //Need for Gson to work with this class
    private ReadOnlySheetImp() {
        this.version = 1;
        this.cells = Collections.emptyList();
        this.name = "";
        this.numOfCols = 0;
        this.numOfRows = 0;
        this.cellWidth = 0;
        this.cellHeight = 0;
        this.ranges = Collections.emptyList();
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public ReadOnlyCell getCell(String coordinate) {
        return cells.stream()
                .filter(cell -> cell.getCoordinate().equals(coordinate))
                .findFirst()
                .orElse(new ReadOnlyCellImp(coordinate)); // Return null if no cell is found with the coordinate
    }

    @Override
    public List<ReadOnlyCell> getCells() {
        return cells;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getNumOfCols() {
        return numOfCols;
    }

    @Override
    public int getNumOfRows() {
        return numOfRows;
    }

    @Override
    public int getCellWidth() {
        return cellWidth;
    }

    @Override
    public int getCellHeight() {
        return cellHeight;
    }

    @Override
    public List<ReadOnlyRange> getRanges() {
        return ranges;
    }
}
