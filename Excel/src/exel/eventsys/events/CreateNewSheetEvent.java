package exel.eventsys.events;

public class CreateNewSheetEvent {
    private final String sheetName;
    private final int height;
    private final int width;
    private final int cols;
    private final int rows;

    public CreateNewSheetEvent(String sheetName, int height, int width, int cols, int rows) {
        this.sheetName = sheetName;
        this.height = height;
        this.width = width;
        this.cols = cols;
        this.rows = rows;
    }
    public String getSheetName() {
        return sheetName;
    }

    // Getter for height
    public int getHeight() {
        return height;
    }

    // Getter for width
    public int getWidth() {
        return width;
    }

    // Getter for number of columns
    public int getCols() {
        return cols;
    }

    // Getter for number of rows
    public int getRows() {
        return rows;
    }
}