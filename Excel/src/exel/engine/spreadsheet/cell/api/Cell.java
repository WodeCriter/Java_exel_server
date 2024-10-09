package exel.engine.spreadsheet.cell.api;

//import exel.engine.spreadsheet.api.EffectiveValue;
import exel.engine.effectivevalue.api.EffectiveValue;
import exel.engine.spreadsheet.cell.imp.CellImp;
import exel.engine.spreadsheet.coordinate.Coordinate;

import java.util.List;

public interface Cell extends Comparable<Cell>
{
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
    List<CellImp> getInfluencingOn();
}
