package dao.library;

import dao.DaoClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;

import java.sql.Date;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("UnnecessaryReturnStatement")
@Log4j2
@AllArgsConstructor
public class Author implements DaoClass {
    private static final Set<String> AUTHOR_FIELDS = new HashSet<>(Arrays.asList("FIRSTNAME", "LASTNAME", "MIDDLENAMES", "SHORTNAME", "BIRTHDATE"));

    @Getter
    private String firstName;
    @Getter
    private String lastName;
    @Getter
    private String middleNames;
    @Getter
    private String shortName;
    @Getter
    @Setter
    private Date birthDate;

    @Override
    public void setField(String name, Object value) {
        switch (name) {
            case "FIRSTNAME":
                firstName = (String) value;
                return;
            case "LASTNAME":
                lastName = (String) value;
                return;
            case "MIDDLENAMES":
                middleNames = (String) value;
                return;
            case "SHORTNAME":
                shortName = (String) value;
                return;
            case "BIRTHDATE":
                birthDate = (Date) value;
                return;
        }
    }

    @Override
    public Object getField(String name) {
        switch (name) {
            case "FIRSTNAME":
                return firstName;
            case "LASTNAME":
                return lastName;
            case "MIDDLENAMES":
                return middleNames;
            case "SHORTNAME":
                return shortName;
            case "BIRTHDATE":
                return birthDate;
        }
        if (log != null) log.error("Trying to get unknown field: {}", name);
        return null;
    }

    public void setFirstName(String firstName) {
        this.firstName = DaoClass.trimToSize(firstName, 30);
    }

    public void setLastName(String lastName) {
        this.lastName = DaoClass.trimToSize(lastName, 30);
    }

    public void setMiddleNames(String middleNames) {
        this.middleNames = DaoClass.trimToSize(middleNames, 30);
    }

    public void setShortName(String shortName) {
        this.shortName = DaoClass.trimToSize(shortName, 50);
    }

    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    public Set<String> getFieldNames() {
        return AUTHOR_FIELDS;
    }

    public Author() {
        this("", "", "", "", Date.valueOf("1900-01-01"));
    }
}
