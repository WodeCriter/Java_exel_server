package engine.imp;

import engine.api.Engine;
import engine.util.file_man.load.imp.sysStateLoader;
import engine.util.file_man.load.imp.xmlFileLoader;
import engine.util.file_man.save.imp.sysStateSaver;
import engine.util.file_man.save.imp.xmlFileSaver;
import engine.effectivevalue.api.EffectiveValue;
import engine.expressions.imp.FunctionParser;
import engine.spreadsheet.api.ReadOnlySheet;
import engine.spreadsheet.api.Sheet;
import engine.spreadsheet.cell.api.ReadOnlyCell;
import engine.spreadsheet.cell.imp.ReadOnlyCellImp;
import engine.spreadsheet.cell.api.Cell;
import engine.spreadsheet.coordinate.Coordinate;
import engine.spreadsheet.imp.ReadOnlySheetImp;
import engine.spreadsheet.imp.SheetImp;
import engine.spreadsheet.range.Range;
import engine.spreadsheet.rowSorter.RowFilter;
import engine.spreadsheet.rowSorter.RowSorter;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EngineImp implements Engine
{
    private Sheet currentSheet;
    private ReadOnlySheet readOnlyCurrentSheet;
    private String filePath;

    public EngineImp() {
    }

    @Override
    public ReadOnlySheet createSheet(String sheetName, int rowNum , int colNum , int cellWidth , int cellHeight) {
        //Todo: check for validity of size

        // Create a new modifiable Sheet and its ReadOnly counterpart
        this.currentSheet = new SheetImp(cellHeight, cellWidth, colNum, rowNum, sheetName);
        this.readOnlyCurrentSheet = new ReadOnlySheetImp(currentSheet);
        return this.readOnlyCurrentSheet;
    }

    @Override
    public ReadOnlySheet createSortedSheetFromCords(String cord1, String cord2, List<String> columnsToSortBy)
    {
        Range range = new Range(new Coordinate(cord1), new Coordinate(cord2), currentSheet);
        RowSorter sorter = new RowSorter(range, currentSheet, columnsToSortBy.stream().map(Coordinate::calculateColIndex).toList());
        return sorter.getSheetAfterChange();
    }

    @Override
    public ReadOnlySheet createFilteredSheetFromCords(String cord1, String cord2, Map<String, List<String>> columnToValuesToFilterBy)
    {
        Range range = new Range(new Coordinate(cord1), new Coordinate(cord2), currentSheet);
        Map<Integer, List<EffectiveValue>> colNumToValueToFilerByMap = new HashMap<>();
        for (String columnStr : columnToValuesToFilterBy.keySet())
        {
            List<String> valuesToFilterByStr = columnToValuesToFilterBy.get(columnStr);
            List<EffectiveValue> valuesToFilterByEffectiveValue = new LinkedList<>();
            for (String value : valuesToFilterByStr)
            {
                List<String> singleton = new LinkedList<>();
                singleton.add(value);
                valuesToFilterByEffectiveValue.add(FunctionParser.IDENTITY.parse(singleton).eval(currentSheet));
            }
            int columnIndex = Coordinate.calculateColIndex(columnStr);
            colNumToValueToFilerByMap.put(columnIndex, valuesToFilterByEffectiveValue);
        }

        RowFilter filter = new RowFilter(range, currentSheet, colNumToValueToFilerByMap);
        return filter.getSheetAfterChange();
    }

    @Override
    public ReadOnlySheet loadSheet(String filePath) throws Exception {
        this.filePath = filePath;
        // parse the xml and create a sheet and a copy sheet object
        this.currentSheet = xmlFileLoader.loadSpreadsheet(filePath);
        this.readOnlyCurrentSheet = new ReadOnlySheetImp(currentSheet);
        return readOnlyCurrentSheet;
    }

    @Override
    public ReadOnlySheet loadSheet(InputStream fileContent) throws Exception {
        // parse the xml and create a sheet and a copy sheet object
        this.currentSheet = xmlFileLoader.loadSpreadsheet(fileContent);
        this.readOnlyCurrentSheet = new ReadOnlySheetImp(currentSheet);
        return readOnlyCurrentSheet;
    }

    @Override
    public void loadSysState(String filePath) throws Exception {
        this.filePath = filePath;
        // create a sheet object from the binary file
        this.currentSheet = sysStateLoader.loadSysState(filePath);
        this.readOnlyCurrentSheet = new ReadOnlySheetImp(currentSheet);
    }

    @Override
    public ReadOnlySheet getSheet() {
        return readOnlyCurrentSheet;
    }

    @Override
    public ReadOnlySheet getSheetOfVersion(int version) {
        Sheet verSheet = currentSheet.getSheetByVersion(version);
        return new ReadOnlySheetImp(verSheet);
    }

    @Override
    public List<Integer> getListOfVersionChanges() {
        return currentSheet.getNumOfChangesInEachVersion();
    }

    @Override
    public ReadOnlyCell getCellContents(String cellCoordinate) {
        if (currentSheet != null) {
            Cell cell = currentSheet.getCell(new Coordinate(cellCoordinate));
            return cell != null ? new ReadOnlyCellImp(cellCoordinate, cell.getOriginalValue(), cell.getEffectiveValue(), cell.getVersion(), cell.getDependsOn(), cell.getInfluencingOn()) : null;
        }
        return null;
    }

    @Override
    public void updateCellContents(String coordinate, String value) throws Exception
    {
        if (currentSheet == null) {
            throw new IllegalStateException("No sheet is currently loaded.");
        }
        //update the current sheet to a copy created inside
        currentSheet = currentSheet.updateCellValueAndCalculate(new Coordinate(coordinate), value); // Directly set the cell's value in the modifiable sheet
        //update your read only sheet based on the copy you just got
        readOnlyCurrentSheet = new ReadOnlySheetImp(currentSheet);
    }

    @Override
    public void saveXmlFile(String filePath){

        xmlFileSaver.saveSpreadsheet(this.currentSheet , filePath);
    }

    @Override
    public void saveSysStateFile(String filePath, String fileName) {
        if (fileName.isEmpty())
            fileName = currentSheet.getName() + "_systemState_v"+ currentSheet.getVersion();
        sysStateSaver.saveSheetState(filePath +"/"+ fileName + ".bin", this.currentSheet);
    }

    @Override
    public boolean hasSheet() {
        return this.currentSheet != null;
    }

    @Override
    public void clearSheet() {
        this.currentSheet = null;
    }

    @Override
    public void addNewRange(String rangeName, String topLeftCord, String bottomRightCord)
    {
        if (rangeName == null || rangeName.isEmpty())
            throw new IllegalArgumentException("Name must be given to the range.");

        currentSheet.addRange(rangeName, new Range(new Coordinate(topLeftCord), new Coordinate(bottomRightCord), currentSheet));
    }

    @Override
    public List<String> getCordsOfCellsInRange(String rangeName)
    {
        if (!currentSheet.isRangeInDatabase(rangeName))
            throw new IllegalArgumentException("The range \"" + rangeName + "\" does not exist.");

        Range range = currentSheet.getRange(rangeName);
        return range.getCordsStrInRange();
    }

    @Override
    public void deleteRange(String rangeName)
    {
        //todo: What happens when we try to delete a range that's currently used
        currentSheet.removeRange(rangeName);
    }

    public static List<String> getAllColumnsBetween2Cords(String cord1Str, String cord2Str)
    {
        if (Coordinate.isStringACellCoordinate(cord1Str) && Coordinate.isStringACellCoordinate(cord2Str))
        {
            Coordinate cord1 = new Coordinate(cord1Str);
            Coordinate cord2 = new Coordinate(cord2Str);

            if (cord1.getColIndex() > cord2.getColIndex())
            {
                Coordinate tmp = cord1;
                cord1 = cord2;
                cord2 = tmp;
            }

            List<String> cordsInBetween = new LinkedList<>();

            do
            {
                cordsInBetween.add(cord1.getCol());
                cord1 = cord1.getCordOnRight();
            } while (cord1.getColIndex() <= cord2.getColIndex());

            return cordsInBetween;
        }

        return null;
    }

    @Override
    public ReadOnlySheet changeCellWidth(int width){
        currentSheet.setCellWidth(width);
        readOnlyCurrentSheet = new ReadOnlySheetImp(currentSheet);
        return readOnlyCurrentSheet;
    }

    @Override
    public ReadOnlySheet changeCellHeight(int height){
        currentSheet.setCellHeight(height);
        readOnlyCurrentSheet = new ReadOnlySheetImp(currentSheet);
        return readOnlyCurrentSheet;
    }
}