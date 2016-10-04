package parsers;

import model.Food;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ParseByStAX {
    private static final String FOOD_CLASS = "food";

    public static Map<Integer, Food> parseMenu(File f) {

        Food currentObj = null;
        StringBuilder currentText = new StringBuilder();
        FieldFiller<Food> ff = new FieldFiller<>(new Food());

        Map<Integer, Food> menu = new HashMap<>();

        try {
            XMLEventReader xReader = XMLInputFactory.newFactory().createXMLEventReader(new FileInputStream(f));

            while (xReader.hasNext()) {
                XMLEvent event = xReader.nextEvent();
                if (event.isStartElement()) {
                    StartElement stel = event.asStartElement();
                    String currElement = stel.getName().getLocalPart();
                    if (currElement.equals(FOOD_CLASS)) {
                        currentObj = new Food();
                        Iterator<Attribute> attrs = stel.getAttributes();
                        while (attrs.hasNext()) {
                            Attribute attr = attrs.next();
                            ff.fill(currentObj, attr.getName().toString(), attr.getValue());
                        }
                    }
                } else if (event.isCharacters() && currentObj != null) {
                    currentText.append(event.asCharacters().getData().trim());
                } else if (event.isEndElement()) {
                    String currElement = event.asEndElement().getName().getLocalPart();
                    if (currElement.equals(FOOD_CLASS)) {
                        menu.put(currentObj.getId(), currentObj);
                        currentObj = null;
                    } else if (currentObj != null) {
                        ff.fill(currentObj, currElement, currentText.toString());
                        currentText.setLength(0);
                    }
                }
            }

        } catch (FileNotFoundException | XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return menu;
    }
}
