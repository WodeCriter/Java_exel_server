package engine.imp;

import engine.api.Engine;
import engine.spreadsheet.dynamic.DynamicAnalysis;
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
import jakarta.xml.bind.JAXBException;
import utils.perms.Permission;
import utils.perms.PermissionRequest;
import utils.perms.Status;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EngineImp implements Engine
{
    private Sheet currentSheet;
    private ReadOnlySheet readOnlyCurrentSheet;
    private DynamicAnalysis dynamicAnalysisHelper;

    private final String engineName;
    private final String ownerName;
    private Map<String, Permission> permissions;
    private Set<PermissionRequest> allRequestsEverMade;
    private Set<PermissionRequest> allPendingRequests;

    public EngineImp(String engineName, String ownerName, InputStream fileContent) throws JAXBException {
        permissions = new ConcurrentHashMap<>();
        allRequestsEverMade = new LinkedHashSet<>();
        allPendingRequests = new LinkedHashSet<>();

        permissions.put(ownerName, Permission.OWNER);
        //allRequestsEverMade.add(new PermissionRequest(ownerName, Permission.OWNER,Status.ACCEPTED, engineName));
        this.engineName = engineName;
        this.ownerName = ownerName;

        loadSheet(fileContent);
    }

    public Permission getUserPermission(String userName) {
        return permissions.getOrDefault(userName, Permission.NONE);
    }

    public Boolean requestForPermission(String requestSender, Permission requestedPermission){
        if (getUserPermission(requestSender) == Permission.OWNER || getUserPermission(requestSender) == requestedPermission)
            return false;
        else
        {
            PermissionRequest newRequest = new PermissionRequest(requestSender, requestedPermission, engineName);
            allPendingRequests.add(newRequest);
            return allRequestsEverMade.add(newRequest);
        }
    }

    //public Boolean requestForPermission(String requestSender, Permission requestedPermission, Sheet sheet){}

    public void approvePendingRequest(PermissionRequest request) {
        processRequest(request, true);
    }

    public void denyPendingRequest(PermissionRequest request) {
        processRequest(request, false);
    }

    private void processRequest(PermissionRequest request, boolean isApproved) {
        if (request.status() != Status.PENDING)
            throw new IllegalArgumentException("The given request is not pending.");

        PermissionRequest copyRequest = request.getCopy();
        if (allRequestsEverMade.remove(copyRequest))
        {
            allPendingRequests.remove(copyRequest);
            if (isApproved)
            {
                copyRequest.approveRequest();
                permissions.put(copyRequest.getSender(), copyRequest.permission());
            }
            else
                copyRequest.denyRequest();
            allRequestsEverMade.add(copyRequest);
        }
        else
            throw new IllegalArgumentException("There's no such request");
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Boolean removePermission(String username){
        return permissions.remove(username) != null;
    }

    public void pickCellForDynamicAnalysis(String coordinate){
        Sheet copySheet = currentSheet.copySheet();
        Cell pickedCell = copySheet.getCell(new Coordinate(coordinate));
        dynamicAnalysisHelper = new DynamicAnalysis(pickedCell, copySheet);
    }

    public ReadOnlySheet changeCellValueForDynamicAnalysis(String newValue){
        if (dynamicAnalysisHelper == null)
            throw new IllegalArgumentException("Illegal call. Need to pick cell first.");
        return dynamicAnalysisHelper.updateCellsValuesForAnalyzing(newValue);
    }

    public ReadOnlySheet saveSheetAfterDynamicAnalysis(String editorName){
        if (dynamicAnalysisHelper == null)
            throw new IllegalArgumentException("Illegal call. Need to pick cell first.");
        currentSheet = dynamicAnalysisHelper.saveCellChanges(editorName);
        readOnlyCurrentSheet = new ReadOnlySheetImp(currentSheet);
        dynamicAnalysisHelper = null;

        return readOnlyCurrentSheet;
    }

    public ReadOnlySheet returnSheetBackAfterDynamicAnalysis(){
        dynamicAnalysisHelper = null;
        return readOnlyCurrentSheet;
    }

    @Override
    public Set<PermissionRequest> getAllPendingRequests(){
        return allPendingRequests;
    }

    @Override
    public Set<PermissionRequest> getAllRequestsEverMade(){
        return allRequestsEverMade;
    }
    //?

    @Override
    public ReadOnlySheet createSheet(String sheetName, int rowNum , int colNum , int cellWidth , int cellHeight) {
        //Todo: check for validity of size

        // Create a new modifiable Sheet and its ReadOnly counterpart
        this.currentSheet = new SheetImp(cellHeight, cellWidth, colNum, rowNum, sheetName);
        this.readOnlyCurrentSheet = new ReadOnlySheetImp(currentSheet);
        return this.readOnlyCurrentSheet;
    }
    //Client

    @Override
    public ReadOnlySheet createSortedSheetFromCords(String cord1, String cord2, List<String> columnsToSortBy)
    {
        Range range = new Range(new Coordinate(cord1), new Coordinate(cord2), currentSheet);
        RowSorter sorter = new RowSorter(range, currentSheet, columnsToSortBy.stream().map(Coordinate::calculateColIndex).toList());
        return sorter.getSheetAfterChange();
    }
    //Client

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
    //redundent

    @Override
    public ReadOnlySheet loadSheet(String filePath) throws Exception {
        // parse the xml and create a sheet and a copy sheet object
        this.currentSheet = xmlFileLoader.loadSpreadsheet(filePath);
        this.readOnlyCurrentSheet = new ReadOnlySheetImp(currentSheet);
        return readOnlyCurrentSheet;
    }
    //server

    @Override
    public ReadOnlySheet loadSheet(InputStream fileContent) throws JAXBException {
        // parse the xml and create a sheet and a copy sheet object
        this.currentSheet = xmlFileLoader.loadSpreadsheet(fileContent);
        this.readOnlyCurrentSheet = new ReadOnlySheetImp(currentSheet);
        return readOnlyCurrentSheet;
    }
    //redundent

    @Override
    public void loadSysState(String filePath) throws Exception {
        // create a sheet object from the binary file
        this.currentSheet = sysStateLoader.loadSysState(filePath);
        this.readOnlyCurrentSheet = new ReadOnlySheetImp(currentSheet);
    }
    //both

    @Override
    public ReadOnlySheet getSheet() {
        return readOnlyCurrentSheet;
    }

    @Override
    public ReadOnlySheet getSheetOfVersion(int version) {
        Sheet verSheet = currentSheet.getSheetByVersion(version);
        return new ReadOnlySheetImp(verSheet);
    }
    //

    @Override
    public List<Integer> getListOfVersionChanges() {
        return currentSheet.getNumOfChangesInEachVersion();
    }

    @Override
    public ReadOnlyCell getCellContents(String cellCoordinate) {
        if (currentSheet != null) {
            Cell cell = currentSheet.getCell(new Coordinate(cellCoordinate));
            return cell != null ? new ReadOnlyCellImp(cell) : null;
        }
        return null;
    }

    @Override
    public void updateCellContents(String coordinate, String value, String editorName) throws Exception
    {
        if (currentSheet == null) {
            throw new IllegalStateException("No sheet is currently loaded.");
        }
        currentSheet.updateCellValueAndVersion(new Coordinate(coordinate), value, editorName); // Directly set the cell's value in the modifiable sheet
        readOnlyCurrentSheet = new ReadOnlySheetImp(currentSheet);
    }

    @Override
    public void saveXmlFile(String filePath){

        xmlFileSaver.saveSpreadsheet(this.currentSheet , filePath);
    }

    @Override
    public int getNumOfCols(){
        return currentSheet.getNumOfCols();
    }

    @Override
    public int getNumOfRows(){
        return currentSheet.getNumOfRows();
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
        readOnlyCurrentSheet = new ReadOnlySheetImp(currentSheet);
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

    @Override
    public int getSheetVersion(){
        return currentSheet.getVersion();
    }
}