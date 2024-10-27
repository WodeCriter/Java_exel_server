package engine.util.sheet;

import engine.spreadsheet.cell.api.ReadOnlyCell;
import engine.spreadsheet.api.ReadOnlySheet;
import engine.spreadsheet.cell.imp.ReadOnlyCellImp;
import engine.spreadsheet.range.ReadOnlyRange;

import java.util.ArrayList;
import java.util.List;

public class SheetUtils {

    public static List<ReadOnlyCell> getCellsInRange(ReadOnlySheet sheet, ReadOnlyRange range) {
        List<ReadOnlyCell> cellsInRange = new ArrayList<>();
        String topLeftCoord = range.getTopLeftCord();
        String bottomRightCoord = range.getBottomRightCord();

        List<String> coordinates = getCoordinatesInRange(topLeftCoord, bottomRightCoord);

        for (String coordinate : coordinates) {
            ReadOnlyCell cell = sheet.getCell(coordinate);
            cellsInRange.add(cell);
        }
        return cellsInRange;
    }

    public static List<String> getCoordinatesInRange(String topLeftCoord, String bottomRightCoord) {
        int[] topLeft = coordinateToIndices(topLeftCoord);
        int[] bottomRight = coordinateToIndices(bottomRightCoord);

        int startRow = topLeft[0];
        int endRow = bottomRight[0];

        int startCol = topLeft[1];
        int endCol = bottomRight[1];

        List<String> coordinates = new ArrayList<>();

        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                String coord = indicesToCoordinate(row, col);
                coordinates.add(coord);
            }
        }
        return coordinates;
    }

    private static int[] coordinateToIndices(String coordinate) {
        int idx = 0;
        while (idx < coordinate.length() && Character.isLetter(coordinate.charAt(idx))) {
            idx++;
        }
        String columnLetters = coordinate.substring(0, idx).toUpperCase();
        String rowNumbers = coordinate.substring(idx);

        int colIndex = columnLetterToIndex(columnLetters);
        int rowIndex = rowNumberToIndex(rowNumbers);

        return new int[]{rowIndex, colIndex};
    }

    private static int columnLetterToIndex(String columnLetters) {
        int colIndex = 0;
        for (int i = 0; i < columnLetters.length(); i++) {
            colIndex *= 26;
            colIndex += (columnLetters.charAt(i) - 'A' + 1);
        }
        return colIndex - 1; // zero-based index
    }

    private static int rowNumberToIndex(String rowNumbers) {
        return Integer.parseInt(rowNumbers) - 1; // zero-based index
    }

    private static String indicesToCoordinate(int rowIndex, int colIndex) {
        String colLetters = indexToColumnLetter(colIndex);
        String rowNumbers = indexToRowNumber(rowIndex);
        return colLetters + rowNumbers;
    }

    private static String indexToColumnLetter(int colIndex) {
        StringBuilder sb = new StringBuilder();
        colIndex += 1;
        while (colIndex > 0) {
            int rem = (colIndex - 1) % 26;
            sb.insert(0, (char) (rem + 'A'));
            colIndex = (colIndex - 1) / 26;
        }
        return sb.toString();
    }

    private static String indexToRowNumber(int rowIndex) {
        return Integer.toString(rowIndex + 1);
    }
}