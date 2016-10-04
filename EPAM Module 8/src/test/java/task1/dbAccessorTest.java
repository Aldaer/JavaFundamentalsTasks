package task1;

import dao.library.Author;
import dao.library.Book;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;

public class dbAccessorTest {
    static DBAccessor dba;

    @BeforeClass
    public static void setUp() throws Exception {
        dba = new DBAccessor(Paths.get("src/test/resources/dbconfig.properties"));
        String createTableRequest = Files.readAllLines(Paths.get("src/test/resources/CreateTables.sql")).
                stream().collect(Collectors.joining());
        assertThat(dba.runSQLBatch(createTableRequest.split(";")), is(true));
        String addDataRequest = Files.readAllLines(Paths.get("src/test/resources/InsertStartingData.sql")).
                stream().collect(Collectors.joining());
        assertThat(dba.runSQLBatch(addDataRequest.split(";")), is(true));
    }

    @Test
    public void testConnection() throws Exception {
        assertNotNull(dba);
        assertThat(dba.testConnection(), is(true));
    }

    @Test
    public void testBookList() throws Exception {
        String selectBooks = Files.readAllLines(Paths.get("src/test/resources/SelectAllBooks.sql")).
                stream().collect(Collectors.joining());

        List<Book> completeBookList = dba.loadListFromDB(Book::new, selectBooks);
        completeBookList.forEach(book -> System.out.printf("Автор: %s; название: %s\n", book.getAuthor(), book.getTitle()));
        assertThat(completeBookList.size(), is(3));
    }

    @Test
    public void selectBooksByAuthorLastName() throws Exception {
        String authorLastNamePattern = "Тол%";

        String selectBooksByAuthor = Files.readAllLines(Paths.get("src/test/resources/SelectBooksByAuthorLastname.sql")).
                stream().collect(Collectors.joining());

        List<Book> partialBookList = dba.loadListFromDB(Book::new, selectBooksByAuthor, authorLastNamePattern);
        partialBookList.forEach(book -> System.out.printf("Автор: %s; название: %s\n", book.getAuthor(), book.getTitle()));
        assertThat(partialBookList.size(), is(2));
    }

    @Test
    public void addNewAuthor() throws Exception {
        Author twain = new Author("Марк", "Твен", "", "М. Твен", Date.valueOf("1835-11-30"));
        String addNewAuthor = Files.readAllLines(Paths.get("src/test/resources/InsertNewAuthor.sql")).
                stream().collect(Collectors.joining());
        String partialAuthorList = Files.readAllLines(Paths.get("src/test/resources/SelectAuthorsByShortname.sql")).
                stream().collect(Collectors.joining());


        dba.addObjectToDB(twain, addNewAuthor);

        List<Author> mTwain = dba.loadListFromDB(Author::new, partialAuthorList, "%Твен%");
        mTwain.forEach(author -> System.out.printf("%s %s %s [%s, род. %tY]",
                author.getFirstName(),
                author.getMiddleNames(),
                author.getLastName(),
                author.getShortName(),
                author.getBirthDate()));

        assertThat(mTwain.size(), is(1));
    }
}