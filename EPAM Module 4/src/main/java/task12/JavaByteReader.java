package task12;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ResourceBundle;

/**
 * Reads .java file and counts keywords using byte streams only. UTF-8, UTF-16 and ASCII encodings are supported.
 */

// FEFF = UTF16-BE
// FFFE = UTF16-LE
// Anything else: 1 ANSI char = 1 byte
public class JavaByteReader {
    private final static int BUF_SIZE = 10240;

    static byte[] growBuf = new byte[BUF_SIZE];

    private static int readJavaFile(String filename) {
        int growCursor = 0;
        try (FileInputStream fis = new FileInputStream(filename)) {
            while (true) {
                if (growBuf.length - growCursor < BUF_SIZE) {
                    byte[] newBuf = new byte[growBuf.length * 2];
                    System.arraycopy(growBuf, 0, newBuf, 0, growCursor);
                    growBuf = newBuf;
                }
                int readBytes = fis.read(growBuf, growCursor, BUF_SIZE);
                if (readBytes <= 0) break;
                growCursor += readBytes;
            }
        } catch (IOException e) {
            return 0;
        }
        return growCursor;
    }

    private static void writeTextFile(String filename, String text) {
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            byte[] buf = text.getBytes();

            fos.write(buf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        ResourceBundle config = ResourceBundle.getBundle("task12/config_b");

        String[] javaKeywords = config.getString("java_keywords").split(",");

        String inpFile = config.getString("inputfile");
        int fSize = readJavaFile(inpFile);

        if (fSize == 0) {
            System.out.println("Input file empty or not found");
            return;
        }
        String charset = "UTF8";
        if (fSize > 1) {
            int UTFmarker = ((growBuf[0] & 0xFF) << 8) + (growBuf[1] & 0xFF);

            if ((UTFmarker == 0xFFFE) || (UTFmarker == 0xFEFF)) charset = "UTF16";
        }

        ByteBuffer BB = ByteBuffer.wrap(growBuf, 0, fSize);
        String javaFile = Charset.forName(charset).decode(BB).toString();

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
