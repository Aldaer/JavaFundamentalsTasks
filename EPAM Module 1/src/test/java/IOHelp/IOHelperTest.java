package IOHelp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;

import static IOHelp.IOHelper.*;
import static org.junit.Assert.assertTrue;

public class IOHelperTest {
    private final byte[] inputBuffer = new byte[2000];
    private final ByteArrayInputStream fakeIn = new ByteArrayInputStream(inputBuffer);
    private final ByteArrayOutputStream fakeOut = new ByteArrayOutputStream();

    final PrintStream realOut = System.out;
    final InputStream realIn = System.in;

    private void fillInputBuffer(String[] arrs) {
        String concat = "";
        for (String s : arrs) {
            concat += s + "\r\n";
        }
        System.arraycopy(concat.getBytes(), 0, inputBuffer, 0, concat.getBytes().length);
    }

    private String[] readFakeSystemOut() {
        return fakeOut.toString().split("\r\n");
    }

    @Before
    public void setUp() throws Exception {
        System.setOut(new PrintStream(fakeOut));
        System.setIn(fakeIn);
        resetInput();
    }

    @After
    public void tearDown() throws Exception {
        System.setOut(realOut);
        System.setIn(realIn);
    }

    @Test
    public void writelnWritesToSystemOut() throws Exception {
        assertTrue(fakeOut.toString().equals(""));
        writeln("test1");
        writeln("test2");
        String[] expectedResult = {"test1", "test2"};
        String[] actualResult = readFakeSystemOut();
        assertTrue(Arrays.equals(actualResult, expectedResult));
    }

    @Test
    public void readlnReadsFromSystemIn() throws Exception {
        fillInputBuffer(new String[]{"test1", "test2"});

        String s1 = readln();
        String s2 = readln();
        assertTrue(s1.equals("test1"));
        assertTrue(s2.equals("test2"));
    }

}