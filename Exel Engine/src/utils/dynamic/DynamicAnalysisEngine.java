package utils.dynamic;

import engine.spreadsheet.api.ReadOnlySheet;

public interface DynamicAnalysisEngine
{
    void pickCellForDynamicAnalysis(String coordinate);
    ReadOnlySheet changeCellValueForDynamicAnalysis(String newValue);
    ReadOnlySheet saveSheetAfterDynamicAnalysis(String sender);
    ReadOnlySheet returnSheetBackAfterDynamicAnalysis();
}
