package engine.util;

import utils.perms.Permission;

public class FileData
{
    private String filename;
    private String ownerName;
    private int numOfCols, numOfRows;
    private Permission userPermission;

    public FileData(String filename, String ownerName, int numOfCols, int numOfRows) {
        this.filename = filename;
        this.ownerName = ownerName;
        this.numOfCols = numOfCols;
        this.numOfRows = numOfRows;
    }

    public String getFilename() {
        return filename;
    }

    public Permission getUserPermission() {
        return userPermission;
    }

    public void setUserPermission(Permission userPermission) {
        this.userPermission = userPermission;
    }

    public int getNumOfCols() {
        return numOfCols;
    }

    public int getNumOfRows() {
        return numOfRows;
    }

    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public String toString() {
        return filename;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FileData))
            return false;
        FileData obj = (FileData) o;

        return filename.equals(obj.filename)
                && ownerName.equals(obj.ownerName)
                && numOfCols == obj.numOfCols
                && numOfRows == obj.numOfRows
                && userPermission.equals(obj.userPermission);
    }

    public int compareTo(String fileName) {
        return this.filename.compareTo(fileName);
    }
}
