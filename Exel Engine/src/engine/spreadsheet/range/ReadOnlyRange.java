package engine.spreadsheet.range;

import java.io.Serializable;

public class ReadOnlyRange implements Serializable {
    private String topLeftCord, bottomRightCord, rangeName;

    public ReadOnlyRange(String topLeftCord, String bottomRightCord, String rangeName) {
        this.topLeftCord = topLeftCord;
        this.bottomRightCord = bottomRightCord;
        this.rangeName = rangeName;
    }

    public ReadOnlyRange(Range range, String rangeName) {
        this(range.getTopLeft().toString(), range.getBottomRight().toString(), rangeName);
    }

    public String getTopLeftCord() {
        return topLeftCord;
    }

    public void setTopLeftCord(String topLeftCord) {
        this.topLeftCord = topLeftCord;
    }

    public String getBottomRightCord() {
        return bottomRightCord;
    }

    public void setBottomRightCord(String bottomRightCord) {
        this.bottomRightCord = bottomRightCord;
    }

    public String getRangeName() {
        return rangeName;
    }

    public void setRangeName(String rangeName) {
        this.rangeName = rangeName;
    }
}
