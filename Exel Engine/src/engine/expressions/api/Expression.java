package engine.expressions.api;

import engine.effectivevalue.api.EffectiveValue;
import engine.spreadsheet.api.Sheet;
import engine.spreadsheet.cell.api.CellType;

public interface Expression {
    static final String UNDEFINED_STRING = "!UNDEFINED!";
    static final String UNDEFINED_NUMBER = "NaN";
    static final String UNDEFINED_BOOLEAN = "UNKNOWN";
    EffectiveValue eval(Sheet sheet);
    CellType getFunctionResultType();
}
