package task1.io;

import org.junit.BeforeClass;
import org.junit.Test;
import task1.TransferInfo;

import java.io.FileReader;
import java.net.URLDecoder;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class XMLObjectReaderTest {
    static String schemaName;
    static String inputName;

    static XMLObjectReader reader;

    @BeforeClass
    public static void loadConfig() throws Exception {
        ClassLoader cldr = XMLObjectReaderTest.class.getClassLoader();
        Properties props = new Properties();
        String propsFile = URLDecoder.decode(cldr.getResource("config.properties").getPath(), "UTF-8");
        System.out.println("Resource file: " + propsFile);
        props.load(new FileReader(propsFile));
        schemaName = URLDecoder.decode(cldr.getResource(props.getProperty("schema_file")).getPath(), "UTF-8");
        inputName = URLDecoder.decode(cldr.getResource(props.getProperty("input_file")).getPath(), "UTF-8");
        reader = XMLObjectReader.initializeReader(schemaName);
    }


    @Test
    public void testReadXML() throws Exception {
        Deque<TransferInfo> tr = reader.parseIntoDeque(inputName, "task1", TransferInfo::new, new LinkedList<>());
        tr.forEach(System.out::println);
    }

    @SuppressWarnings("WeakerAccess")
    private class TestClass {
        public int intField;
        Integer IntField;
        protected double doubleField;
        private Double DoubleField;
        private String stringField;
    }

    @Test
    public void testObjectFieldFiller() throws Exception {
        TestClass test = new TestClass();
        FieldFiller<TestClass> ff = new FieldFiller<>(new TestClass());
        ff.fill(test, "intField", "123");
        ff.fill(test, "IntField", "124");
        ff.fill(test, "doubleField", "17.2");
        ff.fill(test, "DoubleField", "18.2");
        ff.fill(test, "stringField", "something");
        assertThat(test.intField, is(123));
        assertThat(test.intField, is(123));
        assertThat(test.IntField, is(124));
        assertThat(test.doubleField, is(17.2));
        assertThat(test.DoubleField, is(18.2));
        assertThat(test.stringField, is("something"));
    }
}