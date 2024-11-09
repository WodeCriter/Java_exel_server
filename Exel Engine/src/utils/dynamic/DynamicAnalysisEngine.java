package utils.dynamic;

import engine.spreadsheet.api.ReadOnlySheet;

import java.util.Set;

public interface DynamicAnalysisEngine
{
    void pickCellForDynamicAnalysis(Set<String> coordinates);
    ReadOnlySheet changeCellValueForDynamicAnalysis(String Coordinate, String newValue);
    ReadOnlySheet saveSheetAfterDynamicAnalysis(String sender);
    ReadOnlySheet returnSheetBackAfterDynamicAnalysis();
}
