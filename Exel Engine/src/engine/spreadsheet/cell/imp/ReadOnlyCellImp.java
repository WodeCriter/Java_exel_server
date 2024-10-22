package engine.spreadsheet.cell.imp;

import engine.effectivevalue.api.EffectiveValue;
import engine.spreadsheet.cell.api.ReadOnlyCell;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReadOnlyCellImp implements ReadOnlyCell {

    private final String coordinate;
    private final String originalValue;
    private final String effectiveValue;
    private final int version;
    private final List<String> dependsOn;
    private final List<String> influencingOn;

    public ReadOnlyCellImp(String coordinate, String originalValue, EffectiveValue effectiveValue,
                           int version, List<CellImp> dependsOn, List<CellImp> influencingOn) {
        this.coordinate = coordinate;
        this.originalValue = originalValue;
        this.effectiveValue = effectiveValue.toString();
        this.version = version;
        // Convert CellImp lists to ReadOnlyCellImp lists
        // Convert CellImp lists to list of strings containing cell coordinates
        this.dependsOn = dependsOn == null ? Collections.emptyList() :
                dependsOn.stream().map(CellImp::getCoordinateStr).collect(Collectors.toList());
        this.influencingOn = influencingOn == null ? Collections.emptyList() :
                influencingOn.stream().map(CellImp::getCoordinateStr).collect(Collectors.toList());
    }

    @Override
    public String getCoordinate() {
        return coordinate;
    }

    @Override
    public String getOriginalValue() {
        return originalValue;
    }

    @Override
    public String getEffectiveValue() {
        return effectiveValue;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public List<String> getDependsOn() {
        return dependsOn;
    }

    @Override
    public List<String> getInfluencingOn() {
        return influencingOn;
    }
}
