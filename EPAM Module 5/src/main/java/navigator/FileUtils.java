package navigator;

import java.io.*;

/**
 * Various methods to work with text files. All exceptions are procesed here.
 */
class FileUtils {
    static String exceptionMessage = "";
    static String systemExceptionMessage = "";

    static String loadTextFile(File f) {
        char[] buf = new char[10240];
        try (FileReader fr = new FileReader(f)) {
            StringBuilder text = new StringBuilder();
            int charsRead = fr.read(buf);
            while (charsRead > -1) {
                text.append(buf, 0, charsRead);
                charsRead = fr.read(buf);
            }
            exceptionMessage = "";
            return text.toString();
        } catch (FileNotFoundException e) {
            exceptionMessage = "file_not_found";
            systemExceptionMessage = e.getMessage();
        } catch (IOException e) {
            exceptionMessage = "io_exception";
            systemExceptionMessage = e.getMessage();
        }
        return "";
    }

    static File createTextFile(String path) {
        systemExceptionMessage = "";
        File newF = new File(path);
        try {
            exceptionMessage = newF.createNewFile() ? "" : "file_exists";
            return newF;
        } catch (IOException e) {
            exceptionMessage = "io_exception";
            systemExceptionMessage = e.getMessage();
        } catch (SecurityException e) {
            exceptionMessage = "sec_exception";
            systemExceptionMessage = e.getMessage();
        }
        return null;
    }

    static void saveTextFile(File currentTextFile, String text) {
        try (FileWriter fw = new FileWriter(currentTextFile)) {
            fw.write(text);
        } catch (IOException e) {
            exceptionMessage = "io_exception";
            systemExceptionMessage = e.getMessage();
        }
    }

    static void deleteFile(File currentTextFile) {
        try {
            if (!currentTextFile.delete()) exceptionMessage = "cannot_delete";
        } catch (SecurityException e) {
            exceptionMessage = "sec_exception";
            systemExceptionMessage = e.getMessage();
        }
    }
}
