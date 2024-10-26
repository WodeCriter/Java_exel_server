package exel.eventsys.events.cell;

public class CellStyleUpdateEvent {
    private String coordinate;
    private String backgroundColor; // Can be null if not updating
    private String textColor;       // Can be null if not updating
    private String alignment;       // "left", "center", "right", or null
    private boolean clearStyle;     // True if styles should be cleared

    public CellStyleUpdateEvent(String coordinate, String backgroundColor, String textColor, String alignment, boolean clearStyle) {
        this.coordinate = coordinate;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.alignment = alignment;
        this.clearStyle = clearStyle;
    }

    // Getters
    public String getCoordinate() { return coordinate; }
    public String getBackgroundColor() { return backgroundColor; }
    public String getTextColor() { return textColor; }
    public String getAlignment() { return alignment; }
    public boolean isClearStyle() { return clearStyle; }
}
