package bureaucratic.stationery;

import java.util.LinkedHashMap;

/**
 * Anything that can be printed in a table can do so by implementing this interface
 */
@FunctionalInterface
public interface Printable {
    /**
     * Must return empty string if this column type is not applicable,
     * or a representation of this object's data corresponding to the category
     */
    String toPrintableEntry(Category type);

    /**
     * Layout members are column type and width.
     */
    static String generateTableRow(LinkedHashMap<Category, Integer> layout, Printable item) {
        StringBuilder fs = new StringBuilder(100);
        for (Category lItem : layout.keySet()) {
            String sf = "%" + lItem.fmtSpec + layout.get(lItem) + "s";
            fs.append(String.format(sf, item.toPrintableEntry(lItem)));
        }
        return fs.toString();
    }

    /**
     * Enum used to provide data to Printable interface. "-" makes a field left-justified.
     */
    enum Category {
        TYPE("-"), NAME("-"), SIZE, COLOR, UNIT, QTY, PRICE, COST;
        private String fmtSpec = "";

        Category() {
        }

        Category(String additionalFormat) {
            fmtSpec = additionalFormat;
        }
    }
}
