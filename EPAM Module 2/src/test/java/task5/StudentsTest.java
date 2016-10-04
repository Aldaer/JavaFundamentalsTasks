package task5;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static task5.ExamMark.Type.*;

/**
 * Создаем и группируем студентов
 */
public class StudentsTest {
    static int testNo;

    @BeforeClass
    public static void resetCounter() {
        testNo = 0;
    }

    @Before
    public void CreateData() {
        testNo++;

        ExamPassGrades.setGrade("Физика", new ExamMark(INT).setMark("4"));
        ExamPassGrades.setGrade("Химия", new ExamMark(INT).setMark("3"));
        ExamPassGrades.setGrade("Литература", new ExamMark(LETTER).setMark("C"));

        Student st = Student.addNewStudent("Вася");
        st.gradeBook.put("Физика", new ExamMark(INT).setMark("3"));
        st.gradeBook.put("Химия", new ExamMark(INT).setMark("4"));
        st.gradeBook.put("Сепуление", new ExamMark(FLOAT).setMark("99.9"));

        st = Student.addNewStudent("Петя");
        st.gradeBook.put("Физика", new ExamMark(INT).setMark("5"));
        st.gradeBook.put("Химия", new ExamMark(INT).setMark("5"));
        st.gradeBook.put("Литература", new ExamMark(LETTER).setMark("D"));

        st = Student.addNewStudent("Маша");
        st.gradeBook.put("Физика", new ExamMark(INT).setMark("4"));
        st.gradeBook.put("Литература", new ExamMark(LETTER).setMark("A"));
        st.gradeBook.put("Сепуление", new ExamMark(FLOAT).setMark("23.7"));
    }

    @Test
    public void testMarksFail() throws Exception {
        assertFalse(Student.list().get(0).passed("Физика").get());
    }

    @Test
    public void testMarksPass() throws Exception {
        assertTrue(Student.list().get(1).passed("Химия").get());
    }

    @Test
    public void testMarksDidntTake() throws Exception {
        assertFalse(Student.list().get(2).passed("Химия").isPresent());
    }

    @Test
    public void selectByExam() throws Exception {
        String goodM, badM, resetM;
        if (testNo == 1) {
            goodM = "\u001B[32m";                  // Цветовые обозначения работают только при запуске теста поштучно
            badM = "\u001B[31m";
            resetM = "\u001B[30m";
        } else {
            goodM = "+  ";                         // При запуске тестов батареей заменить на ч/б обозначения
            badM = "-  ";
            resetM = "";
        }

        for (String subj : ExamPassGrades.getSubjectList()) {
            System.out.println("Экзамен: " + subj + ", проходной балл: " + ExamPassGrades.getPassGrade(subj));
            for (Student stud : Student.list()) {
                if (stud.studied(subj)) {                                                   // Студент сдавал экзамен
                    if (stud.passed(subj).get()) System.out.print(goodM);            // Сдал
                    else System.out.print(badM);                                     // Не сдал
                    System.out.println(stud.name + " -- " + stud.gradeBook.get(subj) + resetM);
                }
            }
        }
        System.out.println("\nДобавляем новый экзамен...\n");
        ExamPassGrades.setGrade("Сепуление", new ExamMark(FLOAT).setMark("50.0"));

        System.out.println("Экзамен: Сепуление, проходной балл: " + ExamPassGrades.getPassGrade("Сепуление"));
        Student.list().stream().filter(stud -> stud.studied("Сепуление")).forEach(stud -> {
            if (stud.passed("Сепуление").get()) System.out.print(goodM);
            else System.out.print(badM);
            System.out.println(stud.name + " -- " + stud.gradeBook.get("Сепуление") + resetM);
        });
    }
}
