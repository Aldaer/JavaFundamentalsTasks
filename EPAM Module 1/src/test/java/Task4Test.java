import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class Task4Test {
    @Test
    public void maxValueWorksProperly() throws Exception {
        double[] array1 = {1, 1};
        double[] array2 = {};
        double[] array3 = {1.24};
        double[] array4 = {1.2, 2.3, 3.4, 4.5, 5.8, 6.7};

        assertTrue(Task4.maxValue(array1) == 2.0);
        assertTrue(Task4.maxValue(array2) == null);
        assertTrue(Task4.maxValue(array3) == null);
        assertTrue(Task4.maxValue(array4) == 8.1);
    }

}