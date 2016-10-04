import IOHelp.ConstraintRead;

/**
 * Task 2 for module 1 (JAVA.SE.01 Java Fundamentals)<p>
 * EPAM Training
 */
public class Task2 {
    private static boolean checkM(double val, double epsilon) {                 // Условие M
        return val < epsilon;
    }

    private static long sqr(int v) {
        return v * v;
    }

    public static int calculateSequence(double eps) {
        double ai;
        int i = 0;
        do {
            ai = 1.0d / sqr(++i + 1);
            System.out.printf("a%d = %f\n", i, ai);
        } while (!checkM(ai, eps));

        System.out.printf("n = %d\n", i);
        return i;
    }

    public static void main(String[] args) {
        System.out.println("JAVA.SE.01 задание 2");

        ConstraintRead<Double> constr = ConstraintRead.createNew(0.0d, Double.POSITIVE_INFINITY, true);

        double eps = constr.getParamFromUser("\u03B5 [\u03B5>0]");

        calculateSequence(eps);
    }
}
