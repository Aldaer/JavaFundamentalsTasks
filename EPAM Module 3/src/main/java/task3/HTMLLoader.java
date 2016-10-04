package task3;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Loads HTML file into String ensuring correct encoding and replacing "spacelike" tags with spaces
 */
public final class HTMLLoader {
    private static final Pattern CHARSET_TAG = Pattern.compile("charset=([\\w-]+)\\W");

    public static final Pattern FIGURE_AND_CAPTION = Pattern.compile("(?!\\()(?<sentence>[Рр]ис\\.\\s*(?<fignum>\\d+)\\.?\\s+[A-ZА-Я].*?[.!?])(?!\\s+[а-яa-z])");
    public static final Pattern FIGURE_REFERENCE = Pattern.compile("(?<sentence>[А-Я][^.?!]*?\\s+(\\(Рис.|рисун[а-я]{2,4})\\s+(?<fignum>\\d+)(\\(Рис\\.\\s|[^.])+[.?!])(?!\\s+[а-я])");
    public static final Pattern IMAGE_RESOURCE = Pattern.compile("<img[^>]+?src=\"(?<sentence>[^>]*?(?<fignum>\\d+).(?:jpg|png|tif?|gif)\")");

    private static final Pattern HTML_WHITESPACE = Pattern.compile("(\\s|&nbsp;|<span>)+");

    private static ResourceBundle config = ResourceBundle.getBundle("task3.config");

    public static class PicReference {
        public final String ref;
        public final int num;

        private PicReference(String ref, int num) {
            this.ref = ref;
            this.num = num;
        }
    }

    public static String loadHTMLFile() {
        String fileName = config.getString("input_file");

        // Canonical name of the charset
        String defaultEncoding = Charset.forName(config.getString("default_encoding")).name();

        StringBuilder SB = new StringBuilder(1024);
        char[] buf = new char[1024];

        String actualEncoding;
        boolean mustReRead = false;

        do {
            try (FileInputStream fis = new FileInputStream(fileName);
                 InputStreamReader isr = new InputStreamReader(fis, defaultEncoding)) {

                if (!mustReRead) {                                  // Pass 1: default encoding
                    System.out.println("Reading file using character encoding " + defaultEncoding);
                }

                int charsRead = isr.read(buf);
                SB.append(buf);

                if (!mustReRead) {
                    Matcher charsetMatcher = CHARSET_TAG.matcher(SB);
                    if (charsetMatcher.find()) {           // Found charset tag within the first 1024 chars, switching to another encoding
                        System.out.println("Found charset tag: " + charsetMatcher.group(0));
                        actualEncoding = Charset.forName(charsetMatcher.group(1)).name();       // Canonical name
                        if (actualEncoding.equals(defaultEncoding))
                            System.out.println("Charset correct");
                        else {
                            System.out.println("Switching to charset " + actualEncoding);
                            SB.setLength(0);                        // Restart reading with new charset
                            defaultEncoding = actualEncoding;
                            mustReRead = true;
                            continue;
                        }
                    }
                }

                mustReRead = false;                                 // No charset change
                while (charsRead > -1) {
                    charsRead = isr.read(buf);
                    SB.append(buf);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } while (mustReRead);

        System.out.println("Total chars read: " + SB.length());

        // Delete nonbreaking spaces and spans
        return HTML_WHITESPACE.matcher(SB).replaceAll(" ");
    }

    /**
     * Find and parse requested objects.
     *
     * @param text    String to be parsed
     * @param pattern Regex
     * @return List of objects
     */
    public static List<PicReference> getPicReferences(String text, Pattern pattern) {
        List<PicReference> pics = new LinkedList<>();

        Matcher m = pattern.matcher(text);
        while (m.find()) {
            String sentence = m.group("sentence");
            int num = Integer.parseInt(m.group("fignum"));
            pics.add(new PicReference(sentence, num));
        }
        return pics;

    }


}
