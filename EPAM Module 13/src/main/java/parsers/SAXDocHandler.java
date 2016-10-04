package parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Arrays;
import java.util.Queue;
import java.util.function.Supplier;

/**
 * @param <T> Class to build objects for
 */
//TODO: implement nested XML objects
class SAXDocHandler<T> extends DefaultHandler {
    private final Queue<T> objectQueue;
    private final Supplier<? extends T> objectFactory;
    private final String className;
    private final FieldFiller<T> ff;

    private T currentObject;
    private StringBuilder elValue = new StringBuilder();

    SAXDocHandler(Queue<T> objectQueue, Supplier<? extends T> objectFactory, String className) {
        this.objectQueue = objectQueue;
        this.className = className;
        this.objectFactory = objectFactory;
        ff = new FieldFiller<>(objectFactory.get());
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        char[] val = Arrays.copyOfRange(ch, start, start + length);
        elValue.append(val);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (localName.equals(className)) currentObject = objectFactory.get();
        for (int i = 0; i < attributes.getLength(); i++) {
            ff.fill(currentObject, attributes.getLocalName(i), attributes.getValue(i));
        }
        elValue.setLength(0);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals(className)) objectQueue.add(currentObject);
        else ff.fill(currentObject, localName, elValue.toString().trim());
    }

}
