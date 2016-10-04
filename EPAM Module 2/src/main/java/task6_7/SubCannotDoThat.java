package task6_7;

/**
 * This exception is thrown when a sub cannot perform requested action
 */
public class SubCannotDoThat extends Exception {
    public SubCannotDoThat() {
        super();
    }

    public SubCannotDoThat(String msg) {
        super(msg);
    }
}
