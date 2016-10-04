package bureaucratic.stationery;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Various stationery items and materials.
 * Do not extend this class directly. Extend either {@link StationeryItem} or {@link Consumable}.
 */
public abstract class Stationery implements HasACost, Printable, Cloneable {
    final String name;

    public Stationery(String name) {
        this.name = name;
    }

    /**
     * Implement to make a concrete piece of stationery
     */
    abstract protected String getType();

    /**
     * Return empty string if not applicable to your class
     */
    abstract protected String getUnit();

    @Override
    public String toPrintableEntry(Category type) {
        switch (type) {
            case NAME:
                return name;
            case COST:
                return ((Integer) getCost()).toString();
            case TYPE:
                return getType();
            case UNIT:
                return getUnit();
        }
        return "";
    }

    /*
    public static final Comparator<? super Stationery> sorter(Category type) {
        return (o1, o2) -> {
            String s1 = o1.toPrintableEntry(type).trim();
            String s2 = o2.toPrintableEntry(type).trim();
            // Empty string is GREATER than any non-empty string -> gives more readable output
            if (s1.equals("") || s2.equals("")) return s2.compareTo(s1);
            try {       // Faster then regex check
                return Math.round((float)Math.signum(Double.valueOf(s1) - Double.valueOf(s2)));
            } catch (NumberFormatException e) {}

            return  s1.compareTo(s2); };
    }*/

    /**
     * Method to generate sorting comparators for Stationery. <br>
     * Valid numbers are compared as doubles, everything else as strings
     */
    public static final Comparator<? super Stationery> sorter(Category... types) {
        return (o1, o2) -> {
            if (types.length == 0) return 0;
            Category type = types[0];
            String s1 = o1.toPrintableEntry(type).trim();
            String s2 = o2.toPrintableEntry(type).trim();
            int resultForType;
            // Empty string is GREATER than any non-empty string -> gives more readable output
            if (s1.equals("") || s2.equals(""))
                resultForType = s2.compareTo(s1);
            else
                try {       // Faster then regex check
                    resultForType = Math.round((float) Math.signum(Double.valueOf(s1) - Double.valueOf(s2)));
                } catch (NumberFormatException e) {
                    resultForType = s1.compareTo(s2);
                }
            // Return comparison result. If zero, defer to next comparison category
            return (resultForType != 0) ? resultForType : sorter(Arrays.copyOfRange(types, 1, types.length)).compare(o1, o2);
        };
    }

}

