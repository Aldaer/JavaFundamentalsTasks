package properties;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Locale;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class PropReaderTest {
    static PropReader pr;


    @BeforeClass
    public static void setUp() throws Exception {
        Locale.setDefault(Locale.forLanguageTag("ru"));
        pr = PropReader.createReader("test");
    }

    @Test
    public void checkDefaultPropertiesAreReadNormally() throws Exception {
        assertThat(pr.readProperty("someproperty"), is("propvalue_default"));
    }

    @Test
    public void checkNondefaultPropertiesAreReadNormally() throws Exception {
        assertThat(pr.readProperty("someproperty", Locale.ITALIAN), is("propvalue_it"));
    }

    @Test
    public void checkFallbackForNondefaultPropertiesIsWorking() throws Exception {
        assertThat(pr.readProperty("someproperty", Locale.ITALY), is("propvalue_it"));
    }

    @Test
    public void checkNonexistentPropertiesAreReadfromDefault() throws Exception {
        assertThat(pr.readProperty("only_default_property", Locale.ITALIAN), is("here_default"));
        assertThat(pr.readProperty("only_default_property", Locale.ITALY), is("here_default"));
        assertThat(pr.readProperty("only_default_property", Locale.ENGLISH), is("here_default"));
    }

    @Test
    public void checkOrphanPropertiesReturnNull() throws Exception {
        assertThat(pr.readProperty("only_it_property", Locale.ITALIAN), is("here_it"));
        assertNull(pr.readProperty("only_it_property"));
        assertNull(pr.readProperty("only_it_property", Locale.ENGLISH));
    }
}
