package task1;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static task1.CrazyLogger.SEP_TOKEN;

/**
 * Log tester
 */
public class CrazyLoggerTest {
    CrazyLogger log;
    ByteArrayOutputStream buf = new ByteArrayOutputStream(20000);

    @Before
    public void setUp() throws Exception {
        log = new CrazyLogger();

        // Generate 15 normal and 8 important events

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -15);

        log.logEvent("Normal event - 15 hrs ago", cal);

        for (int i = 1; ++i <= 15; ) {
            cal.add(Calendar.HOUR, 1);
            log.logEvent("Normal event " + i, cal);
        }

        cal.add(Calendar.HOUR, -15);
        for (int i = 0; ++i <= 8; ) {
            cal.add(Calendar.MINUTE, 112);
            log.logEvent("Important event " + i, cal);
        }
    }

    @Test
    public void testAllEvents() throws Exception {
        PrintWriter pw = new PrintWriter(buf);
        log.dumpLog(pw);

        System.out.println();
        System.out.println(buf);

        byte[] b = buf.toByteArray();
        int records = 0;
        byte token = SEP_TOKEN.getBytes()[0];
        for (int i = 0; i < b.length; i++)
            if (b[i] == token) records++;

        assertEquals(23, records);
    }

    @Test
    public void testNormalEvents() throws Exception {
        PrintWriter pw = new PrintWriter(buf);
        log.printFilteredEvents(pw, "Normal");

        System.out.println();
        System.out.println(buf);

        byte[] b = buf.toByteArray();
        int records = 0;
        byte token = SEP_TOKEN.getBytes()[0];
        for (int i = 0; i < b.length; i++)
            if (b[i] == token) records++;

        assertEquals(15, records);
    }

    @Test
    public void testImportantEventsDuringLast3Hours() throws Exception {
        PrintWriter pw = new PrintWriter(buf);
        Calendar cal2 = Calendar.getInstance();
        Calendar cal1 = Calendar.getInstance();
        cal1.add(Calendar.HOUR, -3);
        log.printFilteredEvents(pw, "Important", cal1, cal2);

        System.out.println();
        System.out.println(buf);

        byte[] b = buf.toByteArray();
        int records = 0;
        byte token = SEP_TOKEN.getBytes()[0];
        for (int i = 0; i < b.length; i++)
            if (b[i] == token) records++;

        assertEquals(2, records);
    }

}