package engine.api;

import engine.spreadsheet.api.ReadOnlySheet;
import engine.spreadsheet.cell.api.ReadOnlyCell;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface Engine {
    /**
     * Creates a new spreadsheet.
     */
    ReadOnlySheet createSheet(String sheetName, int rowNum , int colNum , int cellWidth , int cellHeight);

    ReadOnlySheet createSortedSheetFromCords(String cord1, String cord2, List<String> columnsToSortBy);

    ReadOnlySheet createFilteredSheetFromCords(String cord1, String cord2, Map<String, List<String>> columnToValuesToFilterBy);

    /**
     * Loads a spreadsheet from an XML file.
     * @param filePath The path to the XML file.
     * @throws Exception if there is an issue loading the file.
     */
    ReadOnlySheet loadSheet(String filePath) throws Exception;


    ReadOnlySheet loadSheet(InputStream fileContent) throws Exception;

    void loadSysState(String filePath) throws Exception;

    /**
     * Retrieves the contents of the entire sheet for display purposes.
     * @return A list of lists representing the spreadsheet, where each inner list is a row of cell values.
     *
     */
    ReadOnlySheet getSheet();


    ReadOnlySheet getSheetOfVersion(int version);

    List<Integer> getListOfVersionChanges();

    /**
     * Retrieves the contents of a specific cell.
     * @param cellCoordinate The column letter of the cell.
     * @return The contents of the specified cell.
     */
    ReadOnlyCell getCellContents(String cellCoordinate);

    /**
     * Updates the contents of a specific cell.
     * @param coordinate The coordinate of the cell.
     * @param value The new value for the cell.
     * @throws IllegalStateException if there is an issue with updating the cell
     */
    void updateCellContents(String coordinate, String value) throws Exception;


    /**
     * Saves the current spreadsheet to a file.
     * @param filePath The path where the file should be saved.
     * @throws Exception if there is an issue saving the file.
     */
    void saveXmlFile(String filePath);

    //method to save system state files
    void saveSysStateFile(String filePath, String fileName);

    // Method to check if the engine has a loaded sheet
    boolean hasSheet();

    // Method to clear the current sheet
    void clearSheet();

    void addNewRange(String rangeName, String topLeftCord, String bottomRightCord);

    List<String> getCordsOfCellsInRange(String rangeName);

    void deleteRange(String rangeName);

    ReadOnlySheet changeCellWidth(int width);
    public ReadOnlySheet changeCellHeight(int height);
}