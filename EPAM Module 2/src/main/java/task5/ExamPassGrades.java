package task5;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Таблица проходных баллов по экзаменам
 */
public class ExamPassGrades {
    private static Map<String, ExamMark> passGrades = new HashMap();

    public static void setGrade(String subj, ExamMark passGrade) {
        passGrades.put(subj, passGrade);
    }

    public static void clear() {
        passGrades.clear();
    }

    public static ExamMark getPassGrade(String subj) {
        return passGrades.getOrDefault(subj, null);
    }

    public static Set<String> getSubjectList() {
        return passGrades.keySet();
    }
}
