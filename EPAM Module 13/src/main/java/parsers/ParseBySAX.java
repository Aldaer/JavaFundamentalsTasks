package parsers;

import model.Food;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ParseBySAX {
    public static Map<Integer, Food> parseMenu(File f) {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);

        Queue<Food> menuQ = new LinkedList<>();

        try {
            SAXParser saxParser = spf.newSAXParser();

            saxParser.parse(f, new SAXDocHandler<Food>(menuQ, Food::new, "food"));
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new RuntimeException(e);
        }

        return menuQ.stream().collect(Collectors.toMap(Food::getId, Function.identity()));
    }

}
