import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class Task2Test {
    private final double epsilon;
    private final int n;

    public Task2Test(double eps, int n) {
        this.epsilon = eps;
        this.n = n;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> Epsilon_N() {
        Object[][] testData = {
                {1.0d, 1}, {0.25d, 2}, {0.1d, 3}, {1e-2d, 10}
        };
        return Arrays.asList(testData);
    }


    @Test
    public void sequenceIsCalculatedProperly() throws Exception {
        assertTrue(Task2.calculateSequence(epsilon) == n);
    }

}