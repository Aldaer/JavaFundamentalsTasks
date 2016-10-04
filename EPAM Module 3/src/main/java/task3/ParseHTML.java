package task3;

import java.util.ArrayList;
import java.util.List;

import static task3.HTMLLoader.PicReference;

/**
 * Simple HTML parser with pic reference numbering check.
 * Pic reference is deemed "out-of order", if pic #N is referenced before the FIRST reference to pic #1..N-1:
 * 1 2 3 1 1 4 - OK,
 * 1 2 4 1 1 3 - out of order (4 before 3)
 */
public class ParseHTML {
    private static void analyzePicData(List<PicReference> pics) {
        ArrayList<Boolean> referenced = new ArrayList<>();

        for (PicReference pic : pics) {
            if (pic.num > referenced.size()) {
                referenced.ensureCapacity(pic.num);
                if (pic.num > referenced.size() + 1)
                    for (int i = referenced.size() + 1; i++ < pic.num; ) referenced.add(false);
                referenced.add(true);
            } else
                referenced.set(pic.num - 1, true);
            for (int i = 0; i < pic.num - 2; i++)
                if (!referenced.get(i)) {
                    System.out.print("*** ");
                    break;
                }
            System.out.println(pic.num + " -> " + pic.ref);
        }
    }

    public static void main(String[] args) {
        String HTMLfile;
        HTMLfile = HTMLLoader.loadHTMLFile();
        System.out.println("===== HTML-ссылки на изображения ===== (*** - не по порядку)");
        List<PicReference> picList = HTMLLoader.getPicReferences(HTMLfile, HTMLLoader.IMAGE_RESOURCE);
        analyzePicData(picList);

        System.out.println("\n===== Подписи к рисункам ===== (*** - не по порядку)");
        picList = HTMLLoader.getPicReferences(HTMLfile, HTMLLoader.FIGURE_AND_CAPTION);
        analyzePicData(picList);

        System.out.println("\n===== Упоминания в тексте ===== (*** - не по порядку)");
        picList = HTMLLoader.getPicReferences(HTMLfile, HTMLLoader.FIGURE_REFERENCE);
        analyzePicData(picList);
    }

}

