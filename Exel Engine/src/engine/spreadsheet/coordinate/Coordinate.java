package engine.spreadsheet.coordinate;

import java.io.Serializable;
import java.util.Objects;

public class Coordinate implements Serializable
{
    private String col;
    private int row;
    private int colIndex;

    public Coordinate(String col, int row)
    {
        setCol(col);
        this.row = row;
    }

    public Coordinate(String coordinate)
    {
        if (!isStringACellCoordinate(coordinate))
            throw new IllegalArgumentException("The given input is not a valid cell coordinate");

        char[] charArray = coordinate.toCharArray();
        int separator = 0;
        while (Character.isLetter(charArray[separator]))
            separator++;
        //Now separator is the index where the digit sequence begins

        setCol(coordinate.substring(0, separator));
        row = Integer.parseInt(coordinate.substring(separator));
    }

    private Coordinate(String col, int row, int colIndex)
    {
        this.col = col;
        this.row = row;
        this.colIndex = colIndex;
    }

    @Override
    public String toString()
    {
        return col + row;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Coordinate that = (Coordinate) o;

        if (!Objects.equals(row, that.row))
            return false;
        return Objects.equals(col, that.col);
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    public static Boolean isStringACellCoordinate(String input)
    {
        if (input == null || input.isEmpty()) {
            return false;
        }

        input = input.trim();
        int length = input.length();
        int i = 0;

        // Check for the presence of at least one letter at the start
        while (i < length && Character.isLetter(input.charAt(i))) {
            i++;
        }

        // There should be at least one letter and one digit
        if (i == 0 || i == length) {
            return false;
        }

        // Check that the rest of the string is digits
        while (i < length && Character.isDigit(input.charAt(i))) {
            i++;
        }

        // If we've parsed through all characters, it's a valid coordinate
        return i == length;
    }

    // Convert the column letters to a column index (0-based).
    public static int calculateColIndex(String col)
    {
        int colIndex = 0;
        for (int j = 0; j < col.length(); j++)
            colIndex = colIndex * 26 + (Character.toUpperCase(col.charAt(j)) - 'A' + 1);
        return colIndex;
    }

    private int calculateColIndex()
    {
        return calculateColIndex(col);
    }

    private static String calculateColFromIndex(int colIndex)
    {
        if (colIndex <= 0)
            throw new IllegalArgumentException("Invalid column index");

        StringBuilder base26 = new StringBuilder();
        while (colIndex > 0)
        {
            colIndex--;
            base26.append((char)('A' + colIndex % 26));
            colIndex /= 26;
        }

        // The conversion builds the number from least significant to most significant, so reverse it
        return base26.reverse().toString();
    }

    public String getCol()
    {
        return col;
    }

    public int getRow()
    {
        return row;
    }

    public int getColIndex()
    {
        return colIndex;
    }

    public void setCol(String col)
    {
        if (!col.equals(this.col))
        {
            this.col = col;
            this.colIndex = calculateColIndex();
        }
    }

    public void setRow(int row)
    {
        this.row = row;
    }

    public Coordinate getCordBelow()
    {
        return new Coordinate(getCol(), getRow() + 1, colIndex);
    }

    public Coordinate getCordAbove()
    {
        if (getRow() == 1)
            return null;
        return new Coordinate(getCol(), getRow() - 1, colIndex);
    }

    public Coordinate getCordOnLeft()
    {
        if (colIndex == 1)
            return null;
        return new Coordinate(calculateColFromIndex(colIndex - 1), getRow(), colIndex - 1);
    }

    public Coordinate getCordOnRight()
    {
        return new Coordinate(calculateColFromIndex(colIndex + 1), getRow(), colIndex + 1);
    }

//    @Override
//    public int compareTo(Object o)
//    {
//        if (this.equals(o))
//            return 0;
//        if (o == null || (o.getClass() != Coordinate.class && o.getClass() != String.class))
//            throw new ClassCastException("Cannot compare type Coordinate with type " + o.getClass());
//
//        Coordinate other;
//        if (o instanceof String)
//            other = new Coordinate((String) o);
//        else
//            other = (Coordinate) o;
//
//        return -1; //temporary
//    }
}
