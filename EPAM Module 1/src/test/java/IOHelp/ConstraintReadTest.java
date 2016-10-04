package IOHelp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConstraintReadTest {
    private static final byte[] inputBuffer = new byte[2000];
    private final static ByteArrayInputStream FAKE_IN = new ByteArrayInputStream(inputBuffer);
    private final static ByteArrayOutputStream FAKE_OUT = new ByteArrayOutputStream();

    final static PrintStream REAL_OUT = System.out;
    final static InputStream REAL_IN = System.in;

    private void fillInputBuffer(String[] arrs) {
        String concat = "";
        for (String s : arrs) {
            concat += s + "\r\n";
        }
        System.arraycopy(concat.getBytes(), 0, inputBuffer, 0, concat.getBytes().length);
    }

    private String[] readFakeSystemOut() {
        return FAKE_OUT.toString().split("\r\n");
    }

    @Before
    public void setUp() throws Exception {
        FAKE_IN.reset();
        FAKE_OUT.reset();
        System.setIn(FAKE_IN);
        System.setOut(new PrintStream(FAKE_OUT));
        IOHelper.resetInput();
    }

    @After
    public void tearDown() throws Exception {
        System.setOut(REAL_OUT);
        System.setIn(REAL_IN);
    }

    @Test
    public void aNewObjectIsCreated() throws Exception {
        ConstraintRead<Integer> cri;
        cri = ConstraintRead.createNew(0, 1, true);
        assertFalse(cri == null);
    }

    @Test
    public void aNewObjectIsCreatedOnlyForAcceptableClasses() throws Exception {

        class StupidNumber extends Number implements Comparable<StupidNumber> {
            protected final int i;

            public StupidNumber(int i) {
                this.i = i;
            }

            public int compareTo(StupidNumber o) {
                return (i - o.i);
            }

            @Override
            public int intValue() {
                return i;
            }

            @Override
            public long longValue() {
                return i;
            }

            @Override
            public float floatValue() {
                return i;
            }

            @Override
            public double doubleValue() {
                return i;
            }
        }       // Test class that DOESN'T implement valueOf

        class CleverNumber extends StupidNumber {
            CleverNumber(int i) {
                super(i);
            }

            public CleverNumber valueOf(String s) {
                return new CleverNumber(Integer.valueOf(s));
            }
        }                                     // Test class that DOES implement valueOf

        ConstraintRead<StupidNumber> crs;
        crs = ConstraintRead.createNew(new StupidNumber(0), new StupidNumber(1), true);
        assertTrue(crs == null);
        ConstraintRead<CleverNumber> crc;
        crc = ConstraintRead.createNew(new CleverNumber(0), new CleverNumber(1), true);
        assertFalse(crc == null);
    }

    @Test
    public void aHintIsPresentedCorrectly() throws Exception {
        fillInputBuffer(new String[]{"1.0"});
        String expectedResult = "Введите d: ";

        ConstraintRead<Double> crd = ConstraintRead.createNew(0.1d, 100.0d, true);
        crd.getParamFromUser("d");

        String actualResult = readFakeSystemOut()[0];
        assertTrue(actualResult.equals(expectedResult));
    }

    @Test
    public void userCanEnterAValidValue() throws Exception {
        fillInputBuffer(new String[]{"1.17e1"});

        ConstraintRead<Double> crd = ConstraintRead.createNew(0.1d, 100.0d, true);

        double d = crd.getParamFromUser("11.7");

        assertTrue(d == 1.17e1d);
    }

    @Test
    public void userIsRequiredToReenterIncorrectValue() throws Exception {
        fillInputBuffer(new String[]{"aaa", "1.0", "-1", "8"});
        String[] expectedResult = {"Введите x: Недопустимое значение!", "Введите x: Недопустимое значение!", "Введите x: Недопустимое значение!", "Введите x: "};

        ConstraintRead<Integer> cri = ConstraintRead.createNew(0, 1000, false);
        int x = cri.getParamFromUser("x");

        String[] actualResult = readFakeSystemOut();
        assertTrue(Arrays.equals(actualResult, expectedResult));
        assertTrue(x == 8);
    }

    @Test
    public void setMinvalAndSetsMaxvalWorkProperly() throws Exception {
        fillInputBuffer(new String[]{"0.1f", "0.1f", "0.2f", "12", "10", "12"});
        String[] expectedResult = {"Введите x: Введите y: Недопустимое значение!", "Введите y: Введите z: Недопустимое значение!", "Введите z: Введите t: "};

        ConstraintRead<Float> crd = ConstraintRead.createNew(0.0f, 10.0f, false);
        float x = crd.getParamFromUser("x");

        assertTrue(x == 0.1f);

        crd.setMinval(0.2f);
        float y = crd.getParamFromUser("y");
        assertTrue(y == 0.2f);

        float z = crd.getParamFromUser("z");
        assertTrue(z == 10f);

        crd.setMaxval(20f);
        float t = crd.getParamFromUser("t");
        assertTrue(t == 12f);

        String[] actualResult = readFakeSystemOut();
        assertTrue(Arrays.equals(actualResult, expectedResult));
    }

    @Test
    public void strictAndNonStrictBordersWorkProperly() throws Exception {
        fillInputBuffer(new String[]{"0", "1", "0", "1e20f", "9e19f", "1e20f", "0.2d", "0.2d", "0.3d", ""}); // i FAIL PASS PASS f FAIL PASS PASS d PASS FAIL PASS

        ConstraintRead<Integer> cri = ConstraintRead.createNew(0, 10, true);
        int i = cri.getParamFromUser("");           // 0 then 1
        assertTrue(i == 1);

        cri.setStrict(false);
        i = cri.getParamFromUser("");               // 0
        assertTrue(i == 0);

        ConstraintRead<Float> crf = ConstraintRead.createNew(0f, 1e20f, true);
        float f = crf.getParamFromUser("");         // 1e20 then 9e19
        assertTrue(f == 9e19f);

        crf.setStrict(false);
        f = crf.getParamFromUser("");               // 1e20
        assertTrue(f == 1e20f);

        ConstraintRead<Double> crd = ConstraintRead.createNew(0.2d, 1d, false);
        double d = crd.getParamFromUser("");        // 0.2d
        assertTrue(d == 0.2d);

        crd.setStrict(true);
        d = crd.getParamFromUser("");               // 0.2d then 0.3d
        assertTrue(d == 0.3d);
    }

}