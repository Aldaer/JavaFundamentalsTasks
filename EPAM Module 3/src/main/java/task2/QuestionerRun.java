package task2;

public class QuestionerRun {
    /**
     * Sorry, cannot do console input in JUnit
     */

    public static void main(String[] args) {
        Questioner q = new Questioner();
        q.localeSelector();


        int res;
        do {
            q.displayQuestionList();
            res = q.getNumber();
            q.displayAnswer(res);
        } while (res > 0);
    }
}