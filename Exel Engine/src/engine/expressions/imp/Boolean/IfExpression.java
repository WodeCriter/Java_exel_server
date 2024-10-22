package engine.expressions.imp.Boolean;

import engine.effectivevalue.api.EffectiveValue;
import engine.effectivevalue.imp.EffectiveValueImp;
import engine.expressions.api.Expression;
import engine.spreadsheet.api.Sheet;
import engine.spreadsheet.cell.api.CellType;

public class IfExpression implements Expression
{
    private Expression condition;
    private Expression then;
    private Expression elseExp;
    private CellType type;

    public IfExpression(Expression condition, Expression then, Expression elseExp) {
        this.condition = condition;
        this.then = then;
        this.elseExp = elseExp;
        this.type = CellType.UNDEFINED;
    }

    @Override
    public EffectiveValue eval(Sheet sheet) {
        EffectiveValue conditionResult = condition.eval(sheet);
        EffectiveValue thenResult = then.eval(sheet);
        EffectiveValue elseResult = elseExp.eval(sheet);

        if (conditionResult.getCellType() != CellType.BOOLEAN)
            return new EffectiveValueImp(CellType.UNDEFINED, UNDEFINED_STRING);

        Boolean conditionBool = conditionResult.extractValueWithExpectation(Boolean.class);
        EffectiveValue toReturn = conditionBool ? thenResult : elseResult;
        type = toReturn.getCellType();
        return toReturn;
    }

    @Override
    public CellType getFunctionResultType() {
        return type;
    }
}
