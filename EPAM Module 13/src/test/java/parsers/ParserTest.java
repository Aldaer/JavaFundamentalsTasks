package parsers;

import model.Food;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ParserTest {
    static File inputFile;

    @BeforeClass
    public static void setUp() throws Exception {
        inputFile = new File("src/test/resources/menu.xml");
    }


    @Test
    public void parseMenuTestSAX() throws Exception {

        Map<Integer, Food> menu = ParseBySAX.parseMenu(inputFile);
        assertThat(menu.get(1).getCalories(), is(650));
        assertThat(menu.get(2).getName(), is("Strawberry Belgian Waffles"));
    }

    @Test
    public void parseMenuTestStAX() throws Exception {

        Map<Integer, Food> menu = ParseByStAX.parseMenu(inputFile);
        assertThat(menu.get(3).getCalories(), is(900));
        assertThat(menu.get(4).getName(), is("French Toast"));
    }

    @Test
    public void parseMenuTestDOM() throws Exception {
        Map<Integer, Food> menu = ParseByDOM.parseMenu(inputFile);
        assertThat(menu.get(2).getCalories(), is(900));
        assertThat(menu.get(3).getDescription(), is("light Belgian waffles covered with an assortment of fresh berries and\n" +
                "            whipped cream"));
    }
}