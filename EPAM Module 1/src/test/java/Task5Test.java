import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;


public class Task5Test {
    @Test
    public void aMatrixIsConstructedProperly() throws Exception {
        int[][] actual = Task5.getMatrix(1);
        int[][] expected = {{1}};
        boolean arraysCoincide = true;
        for (int i = 0; i < expected.length; i++) arraysCoincide &= Arrays.equals(actual[i], expected[i]);
        assertTrue(arraysCoincide);

        arraysCoincide = true;
        actual = Task5.getMatrix(3);
        expected = new int[][]{{1, 0, 1}, {0, 1, 0}, {1, 0, 1}};
        for (int i = 0; i < expected.length; i++) arraysCoincide &= Arrays.equals(actual[i], expected[i]);
        assertTrue(arraysCoincide);

    }

}