package bureaucratic;

import bureaucratic.stationery.Printable;
import bureaucratic.stationery.Stationery;

import java.util.*;

import static bureaucratic.stationery.Printable.Category.*;

/**
 * Class to keep account of stationery issued to an employee
 */
public class EmployeeWorkplace {
    private List<Stationery> workplaceSupply = new ArrayList<>();

    public void add(Stationery item) {
        workplaceSupply.add(item);
    }

    public void add(Collection<Stationery> items) {
        workplaceSupply.addAll(items);
    }

    public void clear() {
        workplaceSupply.clear();
    }

    public int totalCost() {
        return workplaceSupply.stream().mapToInt(Stationery::getCost).sum();
    }

    public void printAsATable() {
        System.out.println("\u001B[34m" + Printable.generateTableRow(printLayout, tableHeader) + "\u001B[0m");
        workplaceSupply.forEach(v -> System.out.println(Printable.generateTableRow(printLayout, v)));
        System.out.println("\u001B[32m" + Printable.generateTableRow(printLayout, tableFooter) + "\u001B[0m");
    }

    public void printAsASortedTable(Stationery.Category... sortMode) {
        Collections.sort(workplaceSupply, Stationery.sorter(sortMode));
        printAsATable();
    }

    public void printAsASortedTable(boolean reverse, Stationery.Category... sortMode) {
        if (reverse) Collections.sort(workplaceSupply, Stationery.sorter(sortMode).reversed());
        else Collections.sort(workplaceSupply, Stationery.sorter(sortMode));
        printAsATable();
    }

    private static LinkedHashMap<Stationery.Category, Integer> printLayout;


    private static Printable tableHeader = Enum::name;

    private Printable tableFooter = cat -> {
        switch (cat) {
            case COST:
                return Integer.toString(totalCost());
            case TYPE:
                return "Total";
            default:
                return "";
        }
    };

    static {
        printLayout = new LinkedHashMap<>();
        printLayout.put(TYPE, 12);
        printLayout.put(NAME, 12);
        printLayout.put(SIZE, 6);
        printLayout.put(COLOR, 10);
        printLayout.put(UNIT, 8);
        printLayout.put(QTY, 8);
        printLayout.put(PRICE, 8);
        printLayout.put(COST, 10);
    }
}
