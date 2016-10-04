package task1;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import task1.io.XMLObjectReader;
import task1.io.XMLObjectReaderTest;

import java.io.FileReader;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.DoubleStream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TransferManagerTest {
    static String inputName;
    static XMLObjectReader reader;

    Deque<TransferInfo> transfers;
    Set<Account> initialState = new HashSet<>();
    Set<Account> expectedState = new HashSet<>();

    static long[] accountNumbers;
    static double[] startingBalance;
    static double[] finalBalance;
    static double totalBalance;

    @BeforeClass
    public static void loadConfig() throws Exception {
        ClassLoader cldr = XMLObjectReaderTest.class.getClassLoader();
        Properties props = new Properties();
        String propsFile = URLDecoder.decode(cldr.getResource("config.properties").getPath(), "UTF-8");
        System.out.println("Resource file: " + propsFile);
        props.load(new FileReader(propsFile));
        String schemaName = URLDecoder.decode(cldr.getResource(props.getProperty("schema_file")).getPath(), "UTF-8");
        inputName = URLDecoder.decode(cldr.getResource(props.getProperty("input_file")).getPath(), "UTF-8");
        reader = XMLObjectReader.initializeReader(schemaName);

        accountNumbers = Arrays.stream(props.getProperty("known_accounts").split(",")).mapToLong(Long::valueOf).toArray();
        startingBalance = Arrays.stream(props.getProperty("starting_balance").split(",")).mapToDouble(Double::valueOf).toArray();
        finalBalance = Arrays.stream(props.getProperty("final_balance").split(",")).mapToDouble(Double::valueOf).toArray();
        totalBalance = DoubleStream.of(startingBalance).sum();
    }

    @Before
    public void loadTransfersFromXML() {
        transfers = reader.parseIntoDeque(inputName, "task1", TransferInfo::new, new LinkedList<>());
        initialState.clear();
        expectedState.clear();
        for (int i = 0; i < accountNumbers.length; i++) {
            initialState.add(new Account(accountNumbers[i], startingBalance[i]));
            expectedState.add(new Account(accountNumbers[i], finalBalance[i]));
        }
    }

    @Test
    public void testTransfersAreLoadedCorrectly() {
        assertThat(transfers.size(), is(10));
    }

    @Test
    public void testTransfersBySyncManager() throws Exception {
        TransferManagerSync tm = new TransferManagerSync(initialState);
        tm.loadTransfers(transfers);
        tm.run();
        tm.waitForCompletion();
        Set<Account> finalState = tm.getSnapshot();

        finalState.forEach(acc -> {
            double expectedBalance = expectedState.parallelStream().filter(expacc -> expacc.getNumber() == acc.getNumber()).findAny().get().getBalance();
            assertThat(acc.getBalance(), is(expectedBalance));
        });
    }

    @Test
    public void testGetSnapshotBySyncManager() throws Exception {
        TransferManagerSync tm = new TransferManagerSync(initialState);
        tm.loadTransfers(transfers);
        tm.run();

        for (int i = 0; i < 3; i++) {
            Set<Account> snapShot = tm.getSnapshot();
            double currentTotal = snapShot.parallelStream().mapToDouble(Account::getBalance).sum();
            assertThat(currentTotal, is(totalBalance));

            TimeUnit.MILLISECONDS.sleep(200);
        }
    }

    @Test
    public void testTransfersByConcurrentManager() throws Exception {
        TransferManagerConcurrent tm = new TransferManagerConcurrent(initialState);
        tm.loadTransfers(transfers);
        tm.run();
        tm.waitForCompletion();
        Set<Account> finalState = tm.getSnapshot();

        finalState.forEach(acc -> {
            double expectedBalance = expectedState.parallelStream().filter(expacc -> expacc.getNumber() == acc.getNumber()).findAny().get().getBalance();
            assertThat(acc.getBalance(), is(expectedBalance));
        });
    }

    @Test
    public void testGetSnapshotByConcurrentManager() throws Exception {
        TransferManagerConcurrent tm = new TransferManagerConcurrent(initialState);
        tm.loadTransfers(transfers);
        tm.run();

        for (int i = 0; i < 3; i++) {
            Set<Account> snapShot = tm.getSnapshot();
            double currentTotal = snapShot.parallelStream().mapToDouble(Account::getBalance).sum();
            assertThat(currentTotal, is(totalBalance));

            TimeUnit.MILLISECONDS.sleep(200);
        }
    }


    private String x(String y) {
        return null;
    }


}