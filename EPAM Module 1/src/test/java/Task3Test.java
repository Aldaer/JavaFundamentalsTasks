import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class Task3Test {

    @Parameterized.Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                {0.1, 1.1, 0.1, 11},
                {0.2, 2.21, 0.2, 11},
                {10, 10, 50, 1},
                {1, -1, 0.1, 0}
        });
    }

    private final double a, b, h;
    private final long expected;

    public Task3Test(double a, double b, double h, long result) {
        this.a = a;
        this.b = b;
        this.h = h;
        expected = result;
    }

    @Test
    public void correctNumberOfFunctionValuesIsPrinted() throws Exception {
        assertTrue(Task3.printFunctionValues(a, b, h) == expected);
    }

}