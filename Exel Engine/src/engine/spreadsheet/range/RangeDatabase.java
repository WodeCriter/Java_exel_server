package engine.spreadsheet.range;

import java.io.Serializable;
import java.util.*;

public class RangeDatabase implements Serializable
{
    private final Map<String, Range> ranges;

    public RangeDatabase()
    {
        ranges = new HashMap<>();
    }

    public void addRange(String rangeName, Range range)
    {
        if (isRangeInDatabase(rangeName))
        {
            throw new RuntimeException("There's already a range named \"" + rangeName + "\".");
        }
        else
            ranges.put(rangeName, range);
    }

    public boolean isRangeInDatabase(String rangeName)
    {
        return ranges.containsKey(rangeName);
    }

    public void removeRange(String rangeName)
    {
        if (getRange(rangeName).isRangeUsed())
            throw new RuntimeException("Cannot remove range \"" + rangeName + "\" as it is currently used.");
        ranges.remove(rangeName);
    }

    public Range getRange(String rangeName)
    {
        if (!isRangeInDatabase(rangeName))
            throw new RuntimeException("The range \"" + rangeName + "\" does not exist.");
        return ranges.get(rangeName);
    }

    public Range getRangeAndCountUse(String rangeName)
    {
        Range range = getRange(rangeName);
        range.countUseOfRange();
        return range;
    }

    public List<ReadOnlyRange> getReadOnlyRanges(){
        List<ReadOnlyRange> readOnlyRanges = new LinkedList<>();
        for (String rangeName : ranges.keySet())
        {
            Range range = getRange(rangeName);
            readOnlyRanges.add(new ReadOnlyRange(range, rangeName));
        }

        return readOnlyRanges;
    }

    public String getRangeName(Range range){
        if (ranges.containsValue(range))
            for (String rangeName : ranges.keySet())
            {
                if (ranges.get(rangeName).equals(range))
                    return rangeName;
            }
        throw new RuntimeException("Cannot find range \"" + range + "\" in range database.");
    }

    public Map<String, Range> getNameAndRangesMap(){return ranges;}
}
