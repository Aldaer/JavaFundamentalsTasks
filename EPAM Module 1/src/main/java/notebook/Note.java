package notebook;

import java.util.Date;

/**
 * A single note to be stored in a Notebook.
 */
class Note {
    private String noteHeader;
    private String noteText;
    private final Date createdAt;
    private Date changedAt;
    private int id;

    Note() {
        createdAt = new Date();
        changedAt = createdAt;
    }

    /**
     * @param header Initial header of the note
     * @param text   Initial text of the note
     */
    Note(String header, String text) {
        this();
        noteHeader = header;
        noteText = text;
    }

    /**
     * Changes the contents of the note. Pass {@code null} to leave unchanged.
     *
     * @param newHeader New header
     * @param newText   New text
     */
    void setContents(String newHeader, String newText) {
        boolean edited = false;
        if ((null != newHeader) && !noteHeader.equals(newHeader)) {
            edited = true;
            noteHeader = newHeader;
        }
        if ((null != newText) && !noteText.equals(newText)) {
            edited = true;
            noteText = newText;
        }
        if (edited) changedAt = new Date();         // No changes
    }

    /**
     * @return Text of the note
     */
    String getText() {
        return noteText;
    }

    /**
     * @return Header of the note
     */
    String getHeader() {
        return noteHeader;
    }

    /**
     * @return Date/time of creation of the note
     */
    Date dateOfCreation() {
        return createdAt;
    }

    /**
     * @return Date/time of last change of the note
     */
    Date lastEdit() {
        return changedAt;
    }

    /**
     * Prints the note to System.out
     */
    void display() {
        System.out.println("--" + noteHeader + "--");
        System.out.println(noteText);
        System.out.println("== Создана: " + createdAt);
        System.out.println("== Последнее изменение: " + changedAt);
    }
}
