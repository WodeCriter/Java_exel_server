package exel.engine.spreadsheet.api;

import exel.engine.spreadsheet.cell.api.ReadOnlyCell;
import exel.engine.spreadsheet.range.ReadOnlyRange;

import java.util.List;

public interface ReadOnlySheet {
    /**
     * Gets the version of the sheet.
     * @return the version number of the sheet.
     */
    int getVersion();

    /**
     * Gets a read-only view of a cell at a given coordinate.
     * @param coordinate the coordinate of the cell, typically as a string (e.g., "A1").
     * @return the read-only cell at the specified coordinate.
     */
    ReadOnlyCell getCell(String coordinate);

    /**
     * Gets a list of all cells in the sheet in a read-only format.
     * @return a list of read-only cells.
     */
    List<ReadOnlyCell> getCells();

    /**
     * Gets the name of the sheet.
     * @return the name of the sheet.
     */
    String getName();

    /**
     * Gets the number of columns in the sheet.
     * @return the number of columns.
     */
    int getNumOfCols();

    /**
     * Gets the number of rows in the sheet.
     * @return the number of rows.
     */
    int getNumOfRows();

    /**
     * Gets the width of a typical cell in the sheet.
     * @return the width of a cell.
     */
    int getCellWidth();

    /**
     * Gets the height of a typical cell in the sheet.
     * @return the height of a cell.
     */
    int getCellHeight();


    List<ReadOnlyRange> getRanges();
}
