package task1.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

/**
 * Reads data from XML file into a queue
 */
public class XMLObjectReader {
    private static final Logger LOG = LogManager.getLogger();

    private static SAXParserFactory saxFactory;
    private static SchemaFactory saxSchemaFactory;

    static {
        saxFactory = SAXParserFactory.newInstance();
        saxFactory.setValidating(true);
        saxFactory.setNamespaceAware(true);

        saxSchemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    }

    private SAXParser parser;

    private boolean parsing = false;

    public boolean isParsing() {
        return parsing;
    }

    public static XMLObjectReader initializeReader(@Nullable String schemaFileName) throws SAXException, ParserConfigurationException {
        XMLObjectReader newReader = new XMLObjectReader();
        try {
            File schF;
            Schema schema;
            if (schemaFileName == null || !(schF = new File(schemaFileName)).isFile()) schema = null;
            else schema = saxSchemaFactory.newSchema(schF);
            saxFactory.setSchema(schema);
            newReader.parser = saxFactory.newSAXParser();
        } catch (NullPointerException e) {
            return null;
        }
        return newReader;
    }

    /**
     * Parses the XML resource into a Deque of objects type T, initializing them by class name and field names.
     * String, integer, double and boolean fields are supported.
     *
     * @param uri             Resource URI
     * @param classNamePrefix Part of the fully qualified object name omitted in xml. For example, to create object
     *                        of class {@code com.example.MyObject} from MyObject tag, specify "com.example" as {@code classNamePrefix}.
     * @param objectFactory   Must produce the objects of class T as required. First produced object is consumed for analysis.
     *                        Pass T::new to create objects with default constructor.
     * @param objectQueue     Queue to fill. If null, the queue will be created
     * @return Filled deque
     */
    // TODO: implement ASYNCHRONOUS parsing to fill the queue by a background thread
    public <T> Deque<T> parseIntoDeque(@NotNull String uri, String classNamePrefix, Supplier<? extends T> objectFactory, @Nullable Deque<T> objectQueue) {
        Class referenceClass = objectFactory.get().getClass();
        String fullName = referenceClass.getCanonicalName();
        if (!fullName.startsWith(classNamePrefix)) throw new RuntimeException("Invalid classNamePrefix");
        String shortName = fullName.substring(classNamePrefix.length());
        if (shortName.startsWith(".")) shortName = shortName.substring(1);

        parsing = true;
        if (objectQueue == null) objectQueue = new LinkedList<T>();

        try {
            parser.parse(uri, new DocHandler<>(objectQueue, objectFactory, shortName));
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }

        return objectQueue;
    }

    /**
     * @param <T> Class to build objects for
     */
    //TODO: implement nested XML objects
    private class DocHandler<T> extends DefaultHandler {
        private final Queue<T> objectQueue;
        private final Supplier<? extends T> objectFactory;
        private final String className;
        private final FieldFiller<T> ff;

        private T currentObject;
        private StringBuilder elValue = new StringBuilder();

        DocHandler(Queue<T> objectQueue, Supplier<? extends T> objectFactory, String className) {
            this.objectQueue = objectQueue;
            this.className = className;
            this.objectFactory = objectFactory;
            ff = new FieldFiller<>(objectFactory.get());
        }

        @Override
        public void endDocument() throws SAXException {
            parsing = false;
            LOG.trace(() -> "endDocument");
        }

        @Override
        public void startDocument() throws SAXException {
            LOG.trace(() -> "startDocument");
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            char[] val = Arrays.copyOfRange(ch, start, start + length);
            elValue.append(val);

            LOG.trace(() -> new String(val).trim());
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (localName.equals(className)) currentObject = objectFactory.get();
            elValue.setLength(0);

            LOG.trace(() -> String.format("startElement: uri = %s, localName = %s, qName = %s", uri, localName, qName));
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (localName.equals(className)) objectQueue.add(currentObject);
            else ff.fill(currentObject, localName, elValue.toString().trim());

            LOG.trace(() -> String.format("endElement: uri = %s, localName = %s, qName = %s", uri, localName, qName));
        }
    }
}
