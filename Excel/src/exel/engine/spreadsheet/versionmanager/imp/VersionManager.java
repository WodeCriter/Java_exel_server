package exel.engine.spreadsheet.versionmanager.imp;

import exel.engine.spreadsheet.cell.api.Cell;
import exel.engine.spreadsheet.coordinate.Coordinate;
import exel.engine.spreadsheet.imp.SheetImp;

import java.io.Serializable;
import java.util.*;

public class VersionManager implements Serializable
{
    private static final long serialVersionUID = 1L;
    private SheetImp baseSheet;
    private List<Map<Coordinate, String>> changesPerVersion = new LinkedList<>();

    public VersionManager(SheetImp initialSheet) {
        this.baseSheet = initialSheet;
        changesPerVersion.add(new HashMap<>()); // Initial version with no changes
    }

    public void recordChanges(List<Cell> changedCells) {
        //get a list of all chnaged cells
        Map<Coordinate, String> versionChanges = new HashMap<>();

        changedCells.forEach(Cell->versionChanges.put(Cell.getCoordinate(), Cell.getEffectiveValue().getValue().toString()));

        changesPerVersion.add(versionChanges);
    }

    public void setBaseSheet(SheetImp baseSheet, List<Cell> changedCells) {
        this.baseSheet = baseSheet;
        changesPerVersion = new LinkedList<>();
        recordChanges(changedCells);
    }

    public SheetImp getSheetByVersion(int version) {
        SheetImp versionSheet = baseSheet.copySheet(); // Start with the base state
        ListIterator<Map<Coordinate, String>> changesIterator = changesPerVersion.listIterator(1);

        for (int i = 1; i <= version && changesIterator.hasNext(); i++)
            applyChanges(versionSheet, changesIterator.next());

//        for (int i = 1; i <= version; i++) {
//            applyChanges(versionSheet, changesPerVersion.get(i));
//        }
        return versionSheet;
    }

    public List<Integer> getNumOfChangesInEachVersion()
    {
        List<Integer> numOfChanges = new LinkedList<>();
        changesPerVersion.forEach(changes->numOfChanges.add(changes.size()));
        return numOfChanges;
    }

    private void applyChanges(SheetImp sheet, Map<Coordinate, String> changes) {
        changes.forEach(sheet::setCell);
    }
}