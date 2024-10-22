package engine.util.file_man.STLConverter.imp;

import engine.spreadsheet.coordinate.Coordinate;
import engine.spreadsheet.imp.SheetImp;
import engine.spreadsheet.range.Range;
import engine.util.jaxb.classes.*;
import engine.spreadsheet.cell.api.Cell;
import engine.spreadsheet.api.Sheet;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

public class STLConverter {

    // Convert from project class to STL class
    /**
     * Converts a project Sheet object to an STLSheet object.
     *
     * @param sheet the Sheet to convert
     * @return the converted STLSheet
     */
    public static STLSheet toSTLSheet(Sheet sheet) {
        STLSheet stlSheet = new STLSheet();
        stlSheet.setName(sheet.getName());

        // Initialize and set up the layout
        STLLayout stlLayout = createSTLLayout(sheet);
        stlSheet.setSTLLayout(stlLayout);

        // Convert and set cells
        STLCells stlCells = convertCells(sheet.getCells());
        stlSheet.setSTLCells(stlCells);

        // Convert and set ranges
        if (sheet.getNameAndRangesMap() != null && !sheet.getNameAndRangesMap().isEmpty()) {
            STLRanges stlRanges = convertRanges(sheet.getNameAndRangesMap());
            stlSheet.setSTLRanges(stlRanges);
        }

        return stlSheet;
    }

    /**
     * Creates and configures the STLLayout based on the Sheet.
     *
     * @param sheet the source Sheet
     * @return the configured STLLayout
     */
    private static STLLayout createSTLLayout(Sheet sheet) {
        STLSize stlSize = new STLSize();
        stlSize.setColumnWidthUnits(sheet.getCellWidth());
        stlSize.setRowsHeightUnits(sheet.getCellHeight());

        STLLayout stlLayout = new STLLayout();
        stlLayout.setSTLSize(stlSize);
        stlLayout.setRows(sheet.getNumOfRows());
        stlLayout.setColumns(sheet.getNumOfCols());

        return stlLayout;
    }

    /**
     * Converts a list of Cell objects to STLCells.
     *
     * @param cells the list of Cells to convert
     * @return the STLCells containing converted STLCell objects
     */
    private static STLCells convertCells(List<Cell> cells) {
        STLCells stlCells = new STLCells();
        List<STLCell> convertedCells = cells.stream()
                .map(STLConverter::convertCell)
                .collect(Collectors.toList());
        stlCells.getSTLCell().addAll(convertedCells);
        return stlCells;
    }

    /**
     * Converts a single Cell to an STLCell.
     *
     * @param cell the Cell to convert
     * @return the converted STLCell
     */
    private static STLCell convertCell(Cell cell) {
        STLCell stlCell = new STLCell();
        Coordinate coordinate = cell.getCoordinate();

        stlCell.setColumn(coordinate.getCol());
        stlCell.setRow(coordinate.getRow());
        stlCell.setSTLOriginalValue(cell.getOriginalValue());

        return stlCell;
    }

    /**
     * Converts a map of range names to Range objects to STLRanges.
     *
     * @param rangesMap the map of range names to Range objects to convert
     * @return the STLRanges containing converted STLRange objects
     */
    private static STLRanges convertRanges(Map<String, Range> rangesMap) {
        STLRanges stlRanges = new STLRanges();
        List<STLRange> convertedRanges = rangesMap.entrySet().stream()
                .map(entry -> convertRange(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        stlRanges.getSTLRange().addAll(convertedRanges);
        return stlRanges;
    }

    /**
     * Converts a single Range with its name to an STLRange.
     *
     * @param rangeName the name of the Range
     * @param range     the Range to convert
     * @return the converted STLRange
     */
    private static STLRange convertRange(String rangeName, Range range) {
        STLRange stlRange = new STLRange();
        stlRange.setName(rangeName);

        STLBoundaries stlBoundaries = new STLBoundaries();
        stlBoundaries.setFrom(range.getTopLeft().toString()); // Assuming Coordinate has a proper toString method
        stlBoundaries.setTo(range.getBottomRight().toString());
        stlRange.setSTLBoundaries(stlBoundaries);

        return stlRange;
    }

    // Convert from STL class to project class
    public static Sheet fromSTLSheet(STLSheet stlSheet) {
        // Derive values from STLSheet
        int cellHeight = stlSheet.getSTLLayout().getSTLSize().getRowsHeightUnits();
        int cellWidth = stlSheet.getSTLLayout().getSTLSize().getColumnWidthUnits();  // Default or derived value
        int numOfCols = stlSheet.getSTLLayout().getColumns();
        int numOfRows = stlSheet.getSTLLayout().getRows();
        String sheetName = stlSheet.getName();
        List<STLRange> stlRanges = null;
        if(stlSheet.getSTLRanges() != null)
            stlRanges = stlSheet.getSTLRanges().getSTLRange();

        // Create a new SheetImp instance with derived or default values
        SheetImp sheet = new SheetImp(cellHeight, cellWidth, numOfCols, numOfRows, sheetName);

        // Loop through each STLCell in the STLCells of the STLSheet and set them inside the sheet object
        for (STLCell stlCell : stlSheet.getSTLCells().getSTLCell()) {
            // get values for each
            String coordinate = stlCell.getColumn() + stlCell.getRow();
            String originalVal = stlCell.getSTLOriginalValue();
            // Add cell to the sheet
            sheet.setCell(coordinate, originalVal);
        }
        if (stlRanges != null) {
            for (STLRange stlRange : stlRanges) {
                STLBoundaries bounds = stlRange.getSTLBoundaries();
                Range range = new Range(new Coordinate(bounds.getFrom()), new Coordinate(bounds.getTo()), sheet );
                sheet.addRange(stlRange.getName(),range );
            }
        }
        sheet.rebase();
        return sheet;
    }
}
