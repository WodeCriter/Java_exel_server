package engine.spreadsheet.api;

import engine.spreadsheet.cell.api.Cell;
import engine.spreadsheet.cell.api.ReadOnlyCell;
import engine.spreadsheet.coordinate.Coordinate;
import engine.spreadsheet.imp.SheetImp;
import engine.spreadsheet.range.Range;
import engine.spreadsheet.range.ReadOnlyRange;

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
    void updateCellValueAndVersion(Coordinate coordinate, String newValue, String editorName) throws Exception;
    Sheet getSheetByVersion(int version);
    List<Integer> getNumOfChangesInEachVersion();
    List<ReadOnlyCell> getReadOnlyCells();

    void increaseVersionAndUpdateChangedCells(List<Cell> changedCells, String editorName);

    SheetImp copySheet();

    Boolean isCoordinateInRange(Coordinate coordinate);

    void addRange(String rangeName, Range range);
    boolean isRangeInDatabase(String rangeName);
    void removeRange(String rangeName);
    Range getRange(String rangeName);
    Range getRangeAndCountUse(String rangeName);
    List<ReadOnlyRange> getReadOnlyRanges();
    Map<String, Range> getNameAndRangesMap();
}
