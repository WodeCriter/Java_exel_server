package exel.engine.spreadsheet.api;

import exel.engine.spreadsheet.cell.api.Cell;
import exel.engine.spreadsheet.cell.api.ReadOnlyCell;
import exel.engine.spreadsheet.coordinate.Coordinate;
import exel.engine.spreadsheet.range.Range;
import exel.engine.spreadsheet.range.ReadOnlyRange;

import java.util.List;
import java.util.Map;

public interface Sheet {
    int getVersion();
    Cell getCell(Coordinate coordinate);

    int getMaxNumOfCells();

    boolean isCellActive(Coordinate coordinate);

    Cell setCell(String coordinate, String value) throws IllegalArgumentException;
    List<Cell> getCells();
    String getName();
    int getNumOfCols();
    int getNumOfRows();
    int getCellWidth();
    void setCellWidth(int width);
    void setCellHeight(int width);

    Cell setCell(Coordinate coordinate, String value) throws IllegalArgumentException;

    int getCellHeight();
    Sheet updateCellValueAndCalculate(Coordinate coordinate, String newValue) throws Exception;
    Sheet getSheetByVersion(int version);
    List<Integer> getNumOfChangesInEachVersion();
    List<ReadOnlyCell> getReadOnlyCells();

    Boolean isCoordinateInRange(Coordinate coordinate);

    void addRange(String rangeName, Range range);
    boolean isRangeInDatabase(String rangeName);
    void removeRange(String rangeName);
    Range getRange(String rangeName);
    Range getRangeAndCountUse(String rangeName);
    List<ReadOnlyRange> getReadOnlyRanges();
    Map<String, Range> getNameAndRangesMap();
}
