package utils.dynamic;

public interface DynamicAnalysisEngine
{
    void pickCellForDynamicAnalysis(String coordinate);
    void changeCellValueForDynamicAnalysis(String newValue);
    void saveSheetAfterDynamicAnalysis();
    void returnSheetBackAfterDynamicAnalysis();
}
