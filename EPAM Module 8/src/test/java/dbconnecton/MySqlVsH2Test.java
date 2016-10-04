package dbconnecton;

import org.junit.Test;

import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.Assert.assertNull;

/**
 * Testing for H2/MySql bug
 */
public class MySqlVsH2Test {
    @Test(expected = SQLException.class)
    public void testConnectToH2WithMySql() throws Exception {
        assertNull(DriverManager.getConnection("jdbc:h2:mem:test", null, null));
    }
}
