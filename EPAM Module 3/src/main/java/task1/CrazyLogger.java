package task1;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Crazy logger class to log crazy events. All events are logged according to <b>UTC time</b>.
 */
public class CrazyLogger {
    /**
     * Message separator. Cannot be included in message text.
     */
    public static final String SEP_TOKEN = ";";

    private final StringBuilder LOG = new StringBuilder(1000);
    private TimeZone serverTZ = Calendar.getInstance().getTimeZone();
    private static final ZoneId UTC_ID = ZoneId.of("UTC");

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy : HH-mm");

    /**
     * Posts an event into the log. Empty and null messages are ignored.
     * Messages containing {@link task1.CrazyLogger#SEP_TOKEN} are deemed illegal and raise an IllegalArgumentException.
     *
     * @param message   Message to log
     * @param timeStamp Time of the event
     */
    public void logEvent(@Nullable String message, @NotNull Calendar timeStamp) {
        if ((null == message) || (message.equals(""))) return;
        if (message.contains(SEP_TOKEN)) throw new IllegalArgumentException();

        ZonedDateTime zdt = ZonedDateTime.ofInstant(timeStamp.toInstant(), UTC_ID);
        LOG.append(zdt.format(dtf) + " - " + message + SEP_TOKEN);
    }

    /**
     * Posts a message into the log marking it with current time. Empty and null messages are ignored.
     * Messages containing {@code SEP_TOKEN} are deemed illegal and raise an IllegalArgumentException.
     *
     * @param message Message to log
     */
    public void logEvent(@Nullable String message) {
        logEvent(message, GregorianCalendar.getInstance());
    }

    /**
     * Dumps the entire log to specified PrintWriter.
     *
     * @param pw Output writer
     */
    public void dumpLog(@NotNull PrintWriter pw) {
        pw.print(LOG.toString());
        pw.flush();
    }

    /**
     * Clears the entire log.
     */
    public void clearLog() {
        LOG.setLength(0);
    }

    /**
     * Prints events filteres by the specified criteria.
     *
     * @param pw          Output writer
     * @param eventFilter Substring to be found in event text. Empty filter means no filtering
     * @param from        Starting time
     * @param to          Ending time
     */
    public void printFilteredEvents(@NotNull PrintWriter pw, @NotNull String eventFilter, @NotNull Calendar from, @NotNull Calendar to) {
        long tFrom = from.getTimeInMillis();
        long tTo = to.getTimeInMillis();
        String[] splitLog = LOG.toString().split(SEP_TOKEN);

        Arrays.stream(splitLog).filter(s -> s.contains(eventFilter)).forEach(s -> {
            String timeStamp = s.substring(0, 18);
            ZonedDateTime zEvent = ZonedDateTime.of(LocalDateTime.parse(timeStamp, dtf), UTC_ID);  // ZonedDateTime.of() bug workaround
            long tEvent = zEvent.toInstant().toEpochMilli();
            if ((tEvent - tFrom) * (tEvent - tTo) <= 0) pw.print(s + SEP_TOKEN + "\n\r");
        });
        pw.flush();
    }

    /**
     * Prints events filteres by the specified criteria.
     *
     * @param pw          Output writer
     * @param eventFilter Substring to be found in event text. Empty filter means no filtering
     */
    public void printFilteredEvents(@NotNull PrintWriter pw, @NotNull String eventFilter) {
        String[] splitLog = LOG.toString().split(SEP_TOKEN);
        Arrays.stream(splitLog).filter(s -> s.contains(eventFilter)).map(s -> s + SEP_TOKEN + "\n\r").forEach(pw::print);
        pw.flush();
    }

}
