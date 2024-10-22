package engine.spreadsheet.cell.api;


import java.util.List;

public interface ReadOnlyCell {
    /**
     * Gets the coordinate of the cell (e.g., "A1").
     * @return the coordinate of the cell.
     */
    String getCoordinate();

    /**
     * Gets the original value of the cell.
     * @return the original value of the cell.
     */
    String getOriginalValue();

    /**
     * Gets the effective value of the cell after any calculations or evaluations.
     * @return the effective value of the cell.
     */
    String getEffectiveValue();

    /**
     * Gets the version of the cell, typically indicating how many times it has been modified.
     * @return the version of the cell.
     */
    int getVersion();

    /**
     * Gets the list of cells that this cell depends on.
     * @return the list of cells this cell depends on.
     */
    List<String> getDependsOn();

    /**
     * Gets the list of cells that are influenced by this cell.
     * @return the list of cells influenced by this cell.
     */
    List<String> getInfluencingOn();
}