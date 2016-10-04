package parsers;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static parsers.FieldFiller.SupportedTypes.*;

/**
 * Fills class fields by names using reflection
 */
@SuppressWarnings("WeakerAccess")
public class FieldFiller<T> {
    //TODO: add more supported types
    enum SupportedTypes {
        INT, LONG, DOUBLE, BOOLEAN, STRING
    }

    private final Map<String, SupportedTypes> classTypes;
    private final Map<String, Field> classFields;

    <T1> FieldFiller(T1 sampleObject) {
        classTypes = new HashMap<>();
        classFields = new HashMap<>();

        Field[] objFields = sampleObject.getClass().getDeclaredFields();
        Arrays.asList(objFields).forEach(f -> {
            SupportedTypes fType = null;
            final String name = f.getName();
            final String type = f.getType().getCanonicalName();
            if (type.equals("int") || type.equals("java.lang.Integer")) fType = INT;
            if (type.equals("long") || type.equals("java.lang.Long")) fType = LONG;
            if (type.equals("double") || type.equals("java.lang.Double")) fType = DOUBLE;
            if (type.equals("boolean") || type.equals("java.lang.Boolean")) fType = BOOLEAN;
            if (type.equals("java.lang.String")) fType = STRING;
            f.setAccessible(true);
            classTypes.put(name, fType);
            classFields.put(name, f);
        });
    }

    /**
     * Fills a field of an object with a value.
     *
     * @param object    Object containing the field to be set
     * @param fieldName Field name. Field must be public
     * @param value     Value to put in. Numeric types are decoded
     */
    public void fill(T object, String fieldName, String value) {
        try {
            Field f = classFields.get(fieldName);
            switch (classTypes.get(fieldName)) {
                case INT:
                    f.set(object, Integer.valueOf(value));
                    break;
                case LONG:
                    f.set(object, Long.valueOf(value));
                    break;
                case DOUBLE:
                    f.set(object, Double.valueOf(value));
                    break;
                case BOOLEAN:
                    f.set(object, Boolean.valueOf(value));
                    break;
                case STRING:
                    f.set(object, value);
                    break;
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Illegal access to field", e);
        } catch (NullPointerException npe) {
            // Do nothing for unknown fields
        }
    }

    /**
     * Fills a aet of an object's fields with values.
     *
     * @param object      Object containing the fields to be set
     * @param fieldValues Field-value map
     */
    public void fill(T object, Map<String, String> fieldValues) {
        fieldValues.forEach((k, v) -> fill(object, k, v));
    }


}
