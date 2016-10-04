package IOHelp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Various handy wrappers
 */
public class IOHelper {
    /**
     * Shorthand wrapper for System.out.println(String arg)
     *
     * @param arg String to print
     */
    public static void writeln(String arg) {
        System.out.println(arg);
    }

    /**
     * Reads a String from System.in
     *
     * @return Obtained String
     */
    public static String readln() {
        String s = "";
        try {
            s = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    private IOHelper() {
    }

    /**
     * Re-binds read buffer to System.in. Used for testing, when System.in is redirected to another source.
     */
    static void resetInput() {
        br = new BufferedReader(new InputStreamReader(System.in));
    }

    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
}
