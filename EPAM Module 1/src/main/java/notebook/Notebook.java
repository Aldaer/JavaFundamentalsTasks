package notebook;

import java.util.ArrayList;

/**
 * A Notebook to store Notes.
 */
public class Notebook {
    private ArrayList<Note> notes;

    public Notebook() {
        notes = new ArrayList<Note>();
    }

    /**
     * @return Total number of notes in the notebook
     */
    public int numberOfNotes() {
        return notes.size();
    }

    /**
     * Creates and adds a new note to the notebook.
     *
     * @param header Header of the note
     * @param text   Text of the note
     * @return Index of the note
     */
    public int addNote(String header, String text) {
        notes.add(new Note(header, text));
        return notes.size() - 1;
    }

    /**
     * Deletes a note from the notebook.
     *
     * @param index Index of note to delete
     * @return true if successful, false if not found
     */
    public boolean deleteNote(int index) {
        if ((index < 0) || (index >= notes.size())) return false;
        notes.remove(index);
        return true;
    }

    /**
     * Changes text of the note.
     *
     * @param index   Index of note to edit
     * @param newText New text of the note
     * @return true if successful, false if not found
     */
    public boolean editNote(int index, String newHeader, String newText) {
        if ((index < 0) || (index >= notes.size())) return false;

        Note n = notes.get(index);
        n.setContents(newHeader, newText);
        return true;
    }

    /**
     * Prints a note to System.out.
     *
     * @param index Index of the note to print
     */
    public void displayNote(int index) {
        if ((index < 0) || (index >= notes.size())) {
            System.out.println("Запись не найдена!");
            return;
        }
        notes.get(index).display();
    }

    /**
     * Prints all notes in the notebook.
     */
    public void displayAll() {
        System.out.println("-------------------");

        for (Note n : notes) {
            n.display();
            System.out.println("-------------------");
        }
    }

    /**
     * Deletes all notes in the notebook.
     */
    public void deleteAll() {
        notes.clear();
    }

    /**
     * Built-in test
     */
    class Test {
    }

    public static void main(String[] args) {
        Notebook nbk = new Notebook();

        System.out.println("Добавляем 3 заметки");
        nbk.addNote("Заголовок 1", "Заметка №1");
        nbk.addNote("Заголовок 2", "Заметка №2");
        nbk.addNote("Заголовок 3", "Заметка №3");

        nbk.displayAll();

        System.out.println("Ждем...");
        try {
            Thread.sleep(2000);                 //1000 milliseconds is one second.
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Удаляем №2, редактируем №3");
        nbk.deleteNote(1);
        nbk.editNote(1, null, "Заметка №3 изменена");
        nbk.displayAll();
    }
}
