package utils.dynamic;

import engine.spreadsheet.api.ReadOnlySheet;

public interface DynamicAnalysisEngine
{
    void pickCellForDynamicAnalysis(String coordinate);
    ReadOnlySheet changeCellValueForDynamicAnalysis(String newValue);
    void saveSheetAfterDynamicAnalysis();
    void returnSheetBackAfterDynamicAnalysis();
}
