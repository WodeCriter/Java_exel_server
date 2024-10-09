package exel.engine.effectivevalue.imp;

import exel.engine.effectivevalue.api.EffectiveValue;
import exel.engine.spreadsheet.cell.api.CellType;

import java.io.Serializable;

public class EffectiveValueImp implements EffectiveValue, Serializable {
    private static final long serialVersionUID = 1L;
    private CellType cellType;
    private Object value;

    public EffectiveValueImp(CellType cellType, Object value) {
        this.cellType = cellType;
        this.value = value;
    }

    @Override
    public CellType getCellType() {
        return cellType;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public <T> T extractValueWithExpectation(Class<T> type) {
        if (cellType.isAssignableFrom(type)) {
            return type.cast(value);
        }
        // error handling... exception ? return null ?
        return null;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!o.getClass().isAssignableFrom(EffectiveValueImp.class))
            return false;

        return equals((EffectiveValue) o);
    }

    private boolean equals(EffectiveValue other)
    {
        if (!cellType.equals(other.getCellType()))
            return false;

        return value.equals(other.getValue());
    }

    @Override
    public int compareTo(EffectiveValue o)
    {
        if (this.cellType == CellType.NUMERIC && o.getCellType() == CellType.NUMERIC)
        {
            double thisNum = extractValueWithExpectation(Double.class);
            double otherNum = o.extractValueWithExpectation(Double.class);
            return Double.compare(thisNum, otherNum);
        }
        else if (this.cellType == CellType.STRING && o.getCellType() == CellType.STRING)
        {
            String thisStr = extractValueWithExpectation(String.class);
            String otherStr = o.extractValueWithExpectation(String.class);
            return thisStr.compareTo(otherStr);
        }
        else if (this.cellType == CellType.BOOLEAN && o.getCellType() == CellType.BOOLEAN)
        {
            boolean thisBool = extractValueWithExpectation(Boolean.class);
            boolean otherBool = o.extractValueWithExpectation(Boolean.class);
            return Boolean.compare(thisBool, otherBool);
        }
        else if (this.cellType == CellType.UNDEFINED && o.getCellType() == CellType.UNDEFINED)
        {
            return 0;
        }

        else if (this.cellType == CellType.UNDEFINED)
            return -1;
        else if (this.cellType == CellType.STRING)
            return 1;
        else if (o.getCellType() == CellType.UNDEFINED)
            return 1;
        else if (o.getCellType() == CellType.STRING)
            return -1;
        else if (this.cellType == CellType.BOOLEAN && o.getCellType() == CellType.NUMERIC)
            return -1;
        else if (this.cellType == CellType.NUMERIC && o.getCellType() == CellType.BOOLEAN)
            return 1;

        return 0;
    }

    @Override
    public String toString()
    {
        if (cellType == CellType.NUMERIC)
        {
            double d = extractValueWithExpectation(Double.class);
            if (d == Math.floor(d))
                return Integer.toString((int) d);
            else
                return Double.toString(d);
        }
        else
            return value.toString();
    }
}

