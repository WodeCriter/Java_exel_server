package engine.spreadsheet.coordinate;

import engine.spreadsheet.range.Range;

import java.util.Iterator;

public class CoordinateIterator implements Iterator<Coordinate>
{
    private static final boolean LEFT = true, RIGHT = false;

    private final Range range;
    private Coordinate current;
    private boolean direction;

    public CoordinateIterator(Coordinate startingCords, Range range)
    {
        this.current = startingCords;
        this.range = range;
        direction = RIGHT;
    }

    @Override
    public boolean hasNext()
    {
        return current != null;
//        if (direction == LEFT)
//        {
//            if (sheet.isCoordinateInRange(current.getCordOnLeft()))
//                return true;
//            else
//                return sheet.isCoordinateInRange(current.getCordBelow());
//        }
//        else
//        {
//            if (sheet.isCoordinateInRange(current.getCordOnRight()))
//                return true;
//            else
//                return sheet.isCoordinateInRange(current.getCordBelow());
//        }
    }

    @Override
    public Coordinate next()
    {
        Coordinate next = current;

        if (direction == LEFT)
            updateCurrent(current.getCordOnLeft());
        else
            updateCurrent(current.getCordOnRight());

        return next;
    }

    private void updateCurrent(Coordinate leftOrRight)
    {
        if (range.isCoordinateInRange(leftOrRight))
            current = leftOrRight;
        else
        {
            Coordinate below = current.getCordBelow();
            if (range.isCoordinateInRange(below))
            {
                changeDirection();
                current = below;
            }
            else
                current = null;
        }
    }

    private void changeDirection()
    {
        direction = !direction;
    }
}
