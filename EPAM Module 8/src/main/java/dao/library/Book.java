package dao.library;

import dao.DaoClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("UnnecessaryReturnStatement")
@Log4j2
@AllArgsConstructor
public class Book implements DaoClass {
    private static final Set<String> BOOK_FIELDS = new HashSet<>(Arrays.asList("AUTHOR", "TITLE"));
    private static final String TABLE = "BOOKS";

    @Getter
    private String author;
    @Getter
    private String title;

    public void setAuthor(String author) {
        this.author = DaoClass.trimToSize(author, 50);
    }

    public void setTitle(String title) {
        this.author = DaoClass.trimToSize(title, 255);
    }

    @Override
    public void setField(String name, Object value) {
        switch (name) {
            case "AUTHOR":
                author = (String) value;
                return;
            case "TITLE":
                title = (String) value;
                return;
        }
    }

    @Override
    public Object getField(String name) {
        switch (name) {
            case "AUTHOR":
                return author;
            case "TITLE":
                return title;
        }
        if (log != null) log.error("Trying to get unknown field: {}", name);
        return null;
    }

    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    public Set<String> getFieldNames() {
        return BOOK_FIELDS;
    }

    public Book() {
        this("", "");
    }
}
