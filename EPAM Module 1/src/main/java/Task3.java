import IOHelp.ConstraintRead;

import static java.lang.Math.tan;

/**
 * Task 3 for module 1 (JAVA.SE.01 Java Fundamentals)<p>
 * EPAM Training
 */
public class Task3 {
    /**
     * Calculates function value f(x) = tg(2x)-3
     *
     * @param x Argument
     * @return Value
     */
    private static double f(double x) {
        return tan(2 * x) - 3;
    }

    /**
     * Prints function values on interval [a, b] with step h
     *
     * @param a Starting point
     * @param b Ending point
     * @param h Step
     * @return Number of values printed
     */
    static long printFunctionValues(double a, double b, double h) {
        double x = a;
        long i;
        for (i = 0; x <= b; x = a + ++i * h) System.out.printf("%f %f\n", x, f(x));
        return i;
    }

    public static void main(String[] args) {
        System.out.println("JAVA.SE.01 задание 3");

        ConstraintRead<Double> constr = ConstraintRead.createNew(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, true);
        double a = constr.getParamFromUser("a");
        constr.setMinval(a);
        constr.setStrict(false);
        double b = constr.getParamFromUser("b [b>=a]");
        constr.setMinval(0.0d);
        constr.setStrict(true);
        double h = constr.getParamFromUser("h [h>0]");

        System.out.printf("    x        F(x)\n");
        printFunctionValues(a, b, h);
    }
}
