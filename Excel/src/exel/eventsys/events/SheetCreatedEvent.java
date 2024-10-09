package exel.eventsys.events;

public class SheetCreatedEvent {
    private String sheetName;
    private int cellHeight;
    private int cellWidth;
    private int numOfRows;
    private int numOfCols;

    public SheetCreatedEvent(String sheetName, int cellHeight, int cellWidth, int numOfRows, int numOfCols) {
        this.sheetName = sheetName;
        this.cellHeight = cellHeight;
        this.cellWidth = cellWidth;
        this.numOfRows = numOfRows;
        this.numOfCols = numOfCols;
    }

    public String getSheetName() {
        return sheetName;
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public int getCellWidth() {
        return cellWidth;
    }

    public int getNumOfCols() {
        return numOfCols;
    }

    public int getNumOfRows() {
        return numOfRows;
    }
}
