package parsers;

import model.Food;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ParseByDOM {
    public static Map<Integer, Food> parseMenu(File f) {
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            Document menu = db.parse(f);

            Map<Integer, Food> parsedMenu = new HashMap<>();

            Element brMenu = menu.getDocumentElement();
            Node foodNode = brMenu.getFirstChild();
            while (foodNode != null) {
                if (foodNode.getNodeName().equals("food")) {
                    Food food = new Food();
                    food.setId(Integer.parseInt(foodNode.getAttributes().getNamedItem("id").getNodeValue()));
                    Node foodProperty = foodNode.getFirstChild();
                    while (foodProperty != null) {
                        switch (foodProperty.getNodeName()) {
                            case "name":
                                food.setName(foodProperty.getFirstChild().getNodeValue().trim());
                                break;
                            case "price":
                                food.setPrice(foodProperty.getFirstChild().getNodeValue().trim());
                                break;
                            case "description":
                                food.setDescription(foodProperty.getFirstChild().getNodeValue().trim());
                                break;
                            case "calories":
                                food.setCalories(Integer.parseInt(foodProperty.getFirstChild().getNodeValue()));
                                break;
                        }
                        foodProperty = foodProperty.getNextSibling();
                    }
                    parsedMenu.put(food.getId(), food);
                }
                foodNode = foodNode.getNextSibling();
            }

            return parsedMenu;

        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
