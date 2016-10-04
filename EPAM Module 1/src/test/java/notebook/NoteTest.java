package notebook;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertTrue;

public class NoteTest {
    @Test
    public void noteIsCreatedCorrectly() {
        Date dBefore = new Date();
        Note n = new Note("1", "2");
        Date dAfter = new Date();

        assertTrue((n.dateOfCreation().compareTo(dAfter) <= 0) && (n.dateOfCreation().compareTo(dBefore) >= 0));
        assertTrue(n.getHeader().equals("1"));
        assertTrue(n.getText().equals("2"));
    }

    @Test
    public void setContentsChangesContents() throws Exception {
        Note n = new Note("1", "2");

        Date dFirstEdit = n.lastEdit();

        Date dBefore = new Date();
        n.setContents("new1", "new2");
        Date dAfter = new Date();
        assertTrue(dBefore.compareTo(dFirstEdit) >= 0);
        assertTrue(dBefore.compareTo(n.lastEdit()) <= 0);
        assertTrue(dAfter.compareTo(n.lastEdit()) >= 0);

        assertTrue(n.getHeader().equals("new1"));
        assertTrue(n.getText().equals("new2"));
    }
}