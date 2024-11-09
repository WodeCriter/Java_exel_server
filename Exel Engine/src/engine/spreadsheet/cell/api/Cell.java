package engine.spreadsheet.cell.api;

//import exel.engine.spreadsheet.api.EffectiveValue;
import engine.effectivevalue.api.EffectiveValue;
import engine.spreadsheet.coordinate.Coordinate;
import engine.spreadsheet.cell.imp.CellImp;

import java.util.List;

public interface Cell extends Comparable<Cell>
{
    void stopCellFromDepending(List<CellImp> dependsOn);

    String getCoordinateStr();

    Coordinate getCoordinate();

    void setCoordinateRowNum(int row);

    String getOriginalValue();
    void setCellOriginalValue(String value);
    EffectiveValue getEffectiveValue();
    void calculateEffectiveValue();
    void setVersion(int version);
    int getVersion();
    List<CellImp> getDependsOn();

    void setDependsOn(List<CellImp> dependsOn);

    void clearDependsOn();

    List<CellImp> getInfluencingOn();

    List<Cell> orderCellsForCalculation();

    String getEditor();

    void setEditor(String editorName);
}
