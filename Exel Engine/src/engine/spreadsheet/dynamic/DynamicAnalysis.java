package engine.spreadsheet.dynamic;

import engine.spreadsheet.api.ReadOnlySheet;
import engine.spreadsheet.api.Sheet;
import engine.spreadsheet.cell.api.Cell;
import engine.spreadsheet.cell.imp.CellImp;
import engine.spreadsheet.imp.ReadOnlySheetImp;

import java.util.List;

public class DynamicAnalysis
{
    private List<Cell> cellsToAnalyze;
    private String pickedCellOriginalValue;
    private List<CellImp> cellDependsOn;
    private Sheet sheet;

    public DynamicAnalysis(Cell cell, Sheet sheet){
        cellsToAnalyze = cell.orderCellsForCalculation();
        cellDependsOn = cell.getDependsOn();
        pickedCellOriginalValue = cell.getOriginalValue();
        this.sheet = sheet;
    }

    public ReadOnlySheet updateCellsValuesForAnalyzing(String newValue) {
        //newValue should not be a function!!
        Cell firstCell = cellsToAnalyze.getFirst();

        firstCell.setCellOriginalValue(newValue);
        cellsToAnalyze.forEach(Cell::calculateEffectiveValue);
        return new ReadOnlySheetImp(sheet);
    }

    public Sheet saveCellChanges(){
        Cell firstCell = cellsToAnalyze.getFirst();
        firstCell.stopCellFromDepending(cellDependsOn);
        firstCell.clearDependsOn();
        sheet.increaseVersionAndUpdateChangedCells(cellsToAnalyze);
        return sheet;
    }

//    public void returnCellsBackToNormal(){
//        updateCellsValuesForAnalyzing(pickedCellOriginalValue);
//    }
}
