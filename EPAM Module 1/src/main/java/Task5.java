import IOHelp.ConstraintRead;

import java.util.Arrays;

/**
 * Task 5 for module 1 (JAVA.SE.01 Java Fundamentals)<p>
 * EPAM Training
 */
public class Task5 {
    public static int[][] getMatrix(int n) {
        int[][] a = new int[n][n];

        for (int i = 0; i < n; i++) {
            a[i][i] = 1;
            a[i][n - i - 1] = 1;
        }
        return a;
    }

    public static void main(String[] args) {
        ConstraintRead<Integer> constr = ConstraintRead.createNew(0, Integer.MAX_VALUE, true);// new ConstraintRead<>(0, Integer.MAX_VALUE, true);

        int n = constr.getParamFromUser("размер матрицы n");
        int[][] a = getMatrix(n);

        System.out.println("Полученный массив:\n" + Arrays.deepToString(a));
    }
}
