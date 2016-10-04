/**
 * Task 4 for module 1 (JAVA.SE.01 Java Fundamentals)<p>
 * EPAM Training
 */
public class Task4 {
    private static double[] getArrayForTask() {             // Можно заменить на получение массива любым другим способом
        return new double[]{0.3, 1.7, 12.3, -0.2, 5.2, -37.3, 12.6, 11.0};
    }

    /**
     * Calculates maximum pair of the array Max(a1 + a2n, a2 + a2n-1, ..., an + an+1)
     * Array must have 2n, n>0 elements.
     *
     * @param a Input array
     * @return Max pair value, null if array unacceptable
     */
    public static Double maxValue(double[] a) {
        if ((a.length < 1) || ((a.length % 2) > 0)) return null;

        int n = a.length / 2;
        double maxv = a[0] + a[2 * n - 1];
        for (int i = 1; i < n; i++) {
            double v = (a[i] + a[2 * n - i - 1]);
            if (v > maxv) maxv = v;
        }
        return maxv;
    }

    public static void main(String[] args) {
        double[] a = getArrayForTask();

        System.out.printf("Искомый максимум: %f\n", maxValue(a));
    }
}
