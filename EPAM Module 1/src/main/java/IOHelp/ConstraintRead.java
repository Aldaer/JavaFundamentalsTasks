package IOHelp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Prompts the user to enter a value within a given set of constraints. Asks to re-enter if outside bounds.<p>
 * <b>Class T must implement {@code T .valueOf(String)}, otherwise user gets null instead of return value.</b>
 *
 * @author Artem Lodygin
 */
public strictfp class ConstraintRead<T extends Number & Comparable> {
    private T minval;
    private T maxval;
    private boolean strict;
    private Method vof;                 // To call valueOf(String)

    /**
     * Creates a new instance of ConstraintRead class. Class {@code T1} must implement {@code T1 valueOf(String)}.
     *
     * @param minval Minimum bound for acceptable value
     * @param maxval Maximum bound for acceptable value
     * @param strict Are the bounds strict or non-strict
     * @return Returns created object, null if type T1 is unacceptable
     */
    public static <T1 extends Number & Comparable> ConstraintRead<T1> createNew(T1 minval, T1 maxval, boolean strict) {
        Method valueof;
        try {
            valueof = minval.getClass().getMethod("valueOf", new Class[]{String.class});
        } catch (NoSuchMethodException e) {
            return null;                        // Unacceptable type T1
        }
        ConstraintRead<T1> cr = new ConstraintRead<>(minval, maxval, strict);
        cr.vof = valueof;
        return cr;
    }

    private ConstraintRead(T minval, T maxval, boolean strict) {
        this.minval = minval;
        this.maxval = maxval;
        this.strict = strict;
    }

    /**
     * @param minval Minimum acceptable value
     */
    public void setMinval(T minval) {
        this.minval = minval;
    }

    /**
     * @param maxval Maximum acceptable value
     */
    public void setMaxval(T maxval) {
        this.maxval = maxval;
    }

    /**
     * @param strict Are the bounds strict or non-strict
     */
    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    /**
     * Obtains a value from System.in and checks for constraints. If unacceptable, asks for re-entry.
     *
     * @param hint Hint for the user on expected value. Please provide meaningful hint!
     * @return Obtained value.
     */
    public T getParamFromUser(String hint) {
        try {
            do {
                System.out.print("Введите " + hint + ": ");

                String s = IOHelper.readln();
                try {
                    // Cannot use T.valueOf, since it's not inherited from Number or Comparable.
                    // Have to invoke some black magic!
                    Comparable v = (Comparable) vof.invoke(this, s);
                    if (((strict && (v.compareTo(minval) > 0) && (v.compareTo(maxval) < 0)) || (!strict && (v.compareTo(minval) >= 0) && (v.compareTo(maxval) <= 0))))
                        return (T) (v);
                } catch (InvocationTargetException e) { /* Entered value incompatible with type T */ }

                System.out.println("Недопустимое значение!");
            } while (true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();                                           // Suppress compiler panic
            return null;
        }
    }
}