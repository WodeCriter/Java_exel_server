package engine.spreadsheet.cell.imp;

import engine.effectivevalue.api.EffectiveValue;
import engine.spreadsheet.cell.api.Cell;
import engine.spreadsheet.cell.api.ReadOnlyCell;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReadOnlyCellImp implements ReadOnlyCell {

    private String coordinate;
    private String originalValue;
    private String effectiveValue;
    private int version;
    private List<String> dependsOn;
    private List<String> influencingOn;
    private String editorName;

    public ReadOnlyCellImp(String coordinate, String originalValue, EffectiveValue effectiveValue,
                           int version, List<CellImp> dependsOn, List<CellImp> influencingOn, String editorName) {
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
        this.editorName = editorName;
    }

    public ReadOnlyCellImp(String coordinate, String originalValue, String effectiveValue,
                           int version, List<String> dependsOn, List<String> influencingOn, String editorName){
        this.coordinate = coordinate;
        this.originalValue = originalValue;
        this.effectiveValue = effectiveValue;
        this.version = version;
        this.dependsOn = dependsOn;
        this.influencingOn = influencingOn;
        this.editorName = editorName;
    }

    public ReadOnlyCellImp(Cell cell){
        this(cell.getCoordinateStr(), cell.getOriginalValue(), cell.getEffectiveValue(), cell.getVersion(),
                cell.getDependsOn(), cell.getInfluencingOn(), cell.getEditor());
    }

    public ReadOnlyCellImp(){
        this.coordinate = "";
        this.originalValue = "";
        this.effectiveValue = "";
        this.version = 1;
        this.dependsOn = Collections.emptyList();
        this.influencingOn = Collections.emptyList();
        this.editorName = "";
    }

    public ReadOnlyCellImp(String coordinate){
        this(coordinate, "", "", 1, Collections.emptyList(), Collections.emptyList(),"");
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

    @Override
    public String getEditorName() {
        return editorName;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ReadOnlyCell))
            return false;

        ReadOnlyCell other = (ReadOnlyCell) o;
        return coordinate.equals(other.getCoordinate()) && originalValue.equals(other.getOriginalValue())
                && effectiveValue.equals(other.getEffectiveValue()) && version == other.getVersion()
                && dependsOn.equals(other.getDependsOn()) && influencingOn.equals(other.getInfluencingOn())
                && editorName.equals(other.getEditorName());
    }
}
