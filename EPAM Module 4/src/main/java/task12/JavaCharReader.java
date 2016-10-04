package task12;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Reads .java file and counts keywords using character streams only.
 */

public class JavaCharReader {
    private final static int BUF_SIZE = 10240;

    static char[] growBuf = new char[BUF_SIZE];

    private static int readJavaFile(String filename) {
        int growCursor = 0;
        try (FileReader fr = new FileReader(filename)) {
            while (true) {
                if (growBuf.length - growCursor < BUF_SIZE) {
                    char[] newBuf = new char[growBuf.length * 2];
                    System.arraycopy(growBuf, 0, newBuf, 0, growCursor);
                    growBuf = newBuf;
                }
                int readChars = fr.read(growBuf, growCursor, BUF_SIZE);
                if (readChars <= 0) break;
                growCursor += readChars;
            }
        } catch (IOException e) {
            return 0;
        }
        return growCursor;
    }

    private static void writeTextFile(String filename, String text) {
        try (FileWriter fw = new FileWriter(filename)) {
            fw.write(text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        ResourceBundle config = ResourceBundle.getBundle("task12/config_c");

        String[] javaKeywords = config.getString("java_keywords").split(",");

        String inpFile = config.getString("inputfile");
        int fSize = readJavaFile(inpFile);

        if (fSize == 0) {
            System.out.println("Input file empty or not found");
            return;
        }

        String javaFile = new String(growBuf, 0, fSize);

        StringBuilder SB = new StringBuilder(1000);
        SB.append("File \"" + inpFile + "\" contains the following Java keywords:\r\n");

        for (String kw : javaKeywords) {
            int found = 0;

            int x = -1;
            while ((x = javaFile.indexOf(kw, x + 1)) > -1) found++;
            if (found > 0) SB.append(kw + " -- " + found + "\r\n");
        }

        writeTextFile(config.getString("outputfile"), SB.toString());
    }
}
