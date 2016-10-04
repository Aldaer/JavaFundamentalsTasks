package bureaucratic.stationery;

import bureaucratic.EmployeeWorkplace;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;

import static bureaucratic.stationery.Printable.Category.*;
import static org.junit.Assert.assertTrue;

/**
 * Test class for tasks 2-4 (Stationery)
 * <p>
 * To get nice colored tables, run tests one by one, not in a pack.
 */
public class BureaucracyTest {
    static EmployeeWorkplace wp;

    @BeforeClass
    public static void setUpWorkplace() throws Exception {
        wp = new EmployeeWorkplace();
    }

    @Before
    public void clearWorkplace() throws Exception {
        wp.clear();
    }

    @Test
    public void testEmptyStationeryTable() throws Exception {
        wp.printAsATable();
        assertTrue(wp.totalCost() == 0);
    }

    /**
     * "Starter kit" used in tasks 3 and 4
     * Total cost: 101
     */
    private Collection<Stationery> starterKit() {
        Collection<Stationery> stKit = new LinkedList<>();
        stKit.add(new Pen("Staedtler", "Black", 12).setQuantity(3));
        stKit.add(new Pen("Staedtler", "Red", 12).setQuantity(1));
        stKit.add(new Pencil("Koh-i-Noor", "Black", 3).setQuantity(5));
        stKit.add(new Eraser("Maped", 5).setQuantity(1));
        stKit.add(new Paper("DoubleA", 20).setAmount(0.4));
        stKit.add(new Folder("Regal", 'M', 25).setQuantity(1));
        return stKit;
    }

    @Test
    public void testStarterKit() throws Exception {
        wp.add(starterKit());
        System.out.println("Starter kit of stationery items:");
        wp.printAsATable();
        assertTrue(wp.totalCost() == 101);
    }

    @Test
    public void testSortingModes() throws Exception {
        wp.add(starterKit());
        wp.add(new Toner("HP", 800).setAmount(0.02));
        wp.add(new Pen("Staedtler", "Black", 15).setQuantity(2));

        System.out.println("Sorting by item name:");
        wp.printAsASortedTable(NAME);

        System.out.println("\nSorting by item cost:");
        wp.printAsASortedTable(COST);

        System.out.println("\nSorting by item cost in REVERSE:");
        wp.printAsASortedTable(true, COST);

        System.out.println("\nSorting by item size:");
        wp.printAsASortedTable(SIZE);
    }

    @Test
    public void testMultisorting() throws Exception {
        wp.add(starterKit());
        wp.add(new Ink("Canon", 50).setAmount(0.12));
        wp.add(new Pen("Staedtler", "Black", 15).setQuantity(2));
        wp.add(new Folder("Regal", 'L', 30).setQuantity(1));

        System.out.println("Sorting by size, THEN type, THEN price");
        wp.printAsASortedTable(SIZE, TYPE, PRICE);

        System.out.println("\nSorting by size, THEN type, THEN color");
        wp.printAsASortedTable(SIZE, TYPE, COLOR);
    }
}
