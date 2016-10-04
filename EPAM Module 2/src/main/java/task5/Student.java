package task5;

import java.util.*;

import static java.util.Collections.unmodifiableList;

/**
 * Студент и его зачетка
 */
public class Student {

    private Student(String name) {
        this.name = name;
    }

    public final String name;
    public final Map<String, ExamMark> gradeBook = new HashMap();

    /**
     * true, если данный студент изучал этот предмет (и сдавал экзамен)
     */
    public boolean studied(String subj) {
        return gradeBook.containsKey(subj);
    }

    /**
     * Пустой optional, если:<ul>
     * <li>Для предмета не установлен проходной балл </li>
     * <li>Студент не сдавал данный экзамен</li></ul>
     */
    public Optional<Boolean> passed(String subj) {
        if (!studied(subj)) return Optional.empty();
        return Optional.ofNullable(gradeBook.get(subj).pass(ExamPassGrades.getPassGrade(subj)));
    }

    private static List<Student> studentList = new ArrayList<>();

    /**
     * Возвращает студента по имени или создает нового
     */
    public static Student addNewStudent(String name) {
        for (Student stud : studentList) {
            if (stud.name.equals(name)) return stud;
        }
        return studentList.add(new Student(name)) ? studentList.get(studentList.size() - 1) : null;
    }

    public static void clear() {
        studentList.clear();
    }

    public static List<Student> list() {
        return unmodifiableList(studentList);
    }
}
