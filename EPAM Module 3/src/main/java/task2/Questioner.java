package task2;


import java.util.*;

/**
 * A sample bilingual quiz
 */
public class Questioner {
    List<String> questions;
    List<String> answers;

    private static Scanner console = new Scanner(System.in);
    private static final Locale RUS = new Locale("ru");
    private static final Locale ENG = Locale.ENGLISH;
    private Locale sLocale;

    public void displayQuestionList() {
        ResourceBundle resUI = ResourceBundle.getBundle("task2.interface", sLocale);
        System.out.println("\n" + resUI.getString("ui_hint1"));

        ResourceBundle qB = ResourceBundle.getBundle("task2.questions", sLocale);
        questions = new ArrayList<>();
        answers = new ArrayList<>();

        String qi;
        for (int i = 0; qB.containsKey(qi = "question" + ++i); ) {
            questions.add(qB.getString(qi));
            answers.add(qB.getString("answer" + i));
        }

        for (int i = 1; i <= this.questions.size(); i++) {
            System.out.println(i + "> " + this.questions.get(i - 1));
        }
    }

    public void localeSelector() {
        ResourceBundle ui_en = ResourceBundle.getBundle("task2.interface", ENG);
        ResourceBundle ui_ru = ResourceBundle.getBundle("task2.interface", RUS);

        System.out.println("1 - " + ui_en.getString("ui_id") + "; 2 - " + ui_ru.getString("ui_id"));

        switch (console.nextInt()) {
            case 1:
                sLocale = ENG;
                break;
            case 2:
                sLocale = RUS;
                break;
            default:
                sLocale = ENG;
        }

    }

    public int getNumber() {
        return console.nextInt();
    }

    public void displayAnswer(int res) {
        try {
            System.out.println(answers.get(res - 1));
        } catch (IndexOutOfBoundsException e) {
        }
    }
}
