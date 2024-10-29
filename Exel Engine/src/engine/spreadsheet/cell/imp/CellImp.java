package engine.spreadsheet.cell.imp;

import java.io.Serializable;
import java.util.*;

import engine.effectivevalue.api.EffectiveValue;
import engine.spreadsheet.cell.api.Cell;
import engine.spreadsheet.coordinate.Coordinate;
import engine.spreadsheet.range.Range;
import engine.expressions.imp.FunctionParser;
import engine.spreadsheet.imp.SheetImp;


public class CellImp implements Cell, Serializable {

    private static final long serialVersionUID = 1L;
    private Coordinate coordinate;
    private String originalValue;
    private EffectiveValue effectiveValue;
    private final SheetImp sheet;
    private int version;
    private List<CellImp> dependsOn;
    private List<CellImp> influencingOn;
    private Range rangeUsed;


    public CellImp(String coordinate, String originalValue, SheetImp sheet) {
        this.coordinate = new Coordinate(coordinate);
        this.originalValue = originalValue;
        this.sheet = sheet;
        this.version = 1;
        this.influencingOn = new LinkedList<>();
        calculateEffectiveValue();
        this.dependsOn = makeCellDependent(originalValue);
        rangeUsed = null;
    }

    public CellImp(String coordinate, SheetImp sheet){
        this(new Coordinate(coordinate), sheet);
    }

    public CellImp(Coordinate coordinate, SheetImp sheet){
        this.coordinate = coordinate;
        this.originalValue = "";
        this.sheet = sheet;
        this.version = 1;
        this.influencingOn = new LinkedList<>();
        this.dependsOn = new LinkedList<>();
    }

    private List<CellImp> makeCellDependent(String newValue){
        List<CellImp> newDependsOn = new LinkedList<>();
        List<Coordinate> influencingCellsCords = FunctionParser.getCellCordsInOriginalValue(newValue);
        for (Coordinate cellCord : influencingCellsCords)
        {
            CellImp influencingCell = sheet.getCell(cellCord);
            newDependsOn.add(influencingCell);
            influencingCell.influencingOn.add(this);
        }
        return newDependsOn;
    }

    private void makeCellDependent(List<CellImp> dependsOn)
    {
        dependsOn.forEach(influencingCell -> influencingCell.influencingOn.add(this));
    }

    private void stopCellFromDepending(List<CellImp> dependsOn) {
        for (CellImp influencingCell : dependsOn)
            influencingCell.influencingOn.remove(this);
    }

    @Override
    public String getCoordinateStr() {
        return coordinate.toString();
    }

    @Override
    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public void setCoordinateRowNum(int row){
        coordinate = new Coordinate(coordinate.getCol(), row);
        //coordinate.setRow(row);
    }

    @Override
    public String getOriginalValue() {
        return originalValue;
    }

    @Override
    public void setCellOriginalValue(String value) {
        this.originalValue = value;
    }

    @Override
    public EffectiveValue getEffectiveValue() {
        calculateEffectiveValue();
        return effectiveValue;
    }

    @Override
    public void calculateEffectiveValue() {
        effectiveValue = FunctionParser.parseExpression(originalValue).eval(sheet);
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public void setVersion(int version)
    {
        this.version = version;
    }

    @Override
    public List<CellImp> getDependsOn() {
        return dependsOn;
    }

    @Override
    public List<CellImp> getInfluencingOn() {
        return influencingOn;
    }

    public List<Cell> setOriginalValueIfPossible(String newValue) throws Exception
    {
        if (originalValue.equals(newValue))
            return orderCellsForCalculation();

        Exception possibleException = FunctionParser.isStringAValidOriginalValue(newValue);
        if (possibleException != null) throw possibleException;

        stopCellFromDepending(dependsOn);
        List<CellImp> newDependsOn = makeCellDependent(newValue);
        List<Cell> orderedCells;

        try
        {
            orderedCells = orderCellsForCalculation();
        }
        catch (Exception e)
        {
            stopCellFromDepending(newDependsOn);
            makeCellDependent(dependsOn);
            throw e;
        }

        dependsOn = newDependsOn;
        originalValue = newValue;
        updateRangeIfNeeded();
        return orderedCells;
    }

    private void updateRangeIfNeeded(){
        if (rangeUsed != null)
        {
            rangeUsed.removeUseOfRange();
            rangeUsed = null;
        }

        String rangeInOriginalValue = FunctionParser.getRangeInValue(originalValue);
        if (rangeInOriginalValue != null)
        {
            rangeUsed = sheet.getRange(rangeInOriginalValue);
            rangeUsed.countUseOfRange();
        }
    }

    private List<Cell> orderCellsForCalculation()
    {
        List<Cell> orderedCells = new LinkedList<>();
        Map<Cell, Boolean> coloredCells = new HashMap<>();

        orderCellsForCalculationHelper(this, coloredCells, orderedCells);
        return orderedCells;
    }

    private void orderCellsForCalculationHelper(Cell cell, Map<Cell, Boolean> coloredCells, List<Cell> orderedCells)
    {
        Boolean GREY = true, BLACK = false, WHITE = null; //DFS Colors
        coloredCells.put(cell, GREY); //color Cell Grey

        for (Cell dependentCell : cell.getInfluencingOn())
        {
            Boolean color = coloredCells.get(dependentCell);
            if (color == WHITE)
                orderCellsForCalculationHelper(dependentCell, coloredCells, orderedCells);
            if (color == GREY)
                throw new IllegalArgumentException("Dependency circle found.");
        }

        coloredCells.put(cell, BLACK); //color Cell Black
        orderedCells.addFirst(cell);
    }

    @Override
    public int compareTo(Cell o)
    {
        return effectiveValue.compareTo(o.getEffectiveValue());
    }
}
