package task3;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ResourceBundle;

/**
 * Converts cyrillic from UTF-8 to UTF-16
 */
public class TxtConverter {
    final static String FROM_ENCODING = "UTF8";
    final static String TO_ENCODING = "UTF16";

    private static String readFile(String filename, String encoding) {
        final int BUF_SIZE = 10240;

        char[] growBuf = new char[BUF_SIZE];

        int growCursor = 0;
        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(filename), Charset.forName(encoding))) {
            while (true) {
                if (growBuf.length - growCursor < BUF_SIZE) {
                    char[] newBuf = new char[growBuf.length * 2];
                    System.arraycopy(growBuf, 0, newBuf, 0, growCursor);
                    growBuf = newBuf;
                }
                int readChars = isr.read(growBuf, growCursor, BUF_SIZE);
                if (readChars <= 0) break;
                growCursor += readChars;
            }
        } catch (IOException e) {
            return null;
        }
        return String.copyValueOf(growBuf, 0, growCursor);
    }

    private static void writeFile(String filename, String text, String encoding) {
        try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(filename), encoding)) {
            osw.write(text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        ResourceBundle res = ResourceBundle.getBundle("task3/config");
        String inputFileName = res.getString("inputfile");
        String textFile = readFile(inputFileName, FROM_ENCODING);
        if (textFile == null) {
            System.out.println("Error reading file " + inputFileName);
            return;
        }
        System.out.println("Successfully read " + textFile.length() + " characters from " + inputFileName);
        String outputFileName = res.getString("outputfile");
        writeFile(outputFileName, textFile, TO_ENCODING);
        System.out.println("Successfully written output file " + outputFileName);
    }
}
