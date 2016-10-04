package dbconnecton;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.Properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class ConnectionPoolTest {
    ConnectionPool cp;

    @Before
    public void setUp() throws Exception {
        Properties props = new Properties();

        props.load(Files.newInputStream(Paths.get("build/resources/test/dbconfig.properties")));

        cp = ConnectionPool.builder()
                .setDriver(props.getProperty("driver"))
                .setUrl(props.getProperty("url"))
                .setPoolSize(Integer.valueOf(props.getProperty("poolsize")))
                .create();

    }

    @Test
    public void testGetConnection() throws Exception {
        int availableNow = cp.available();
        try (Connection conn = cp.getConnection()) {
            assertNotNull(conn);
            assertThat(cp.available(), is(availableNow - 1));
        }
        assertThat(cp.available(), is(availableNow));
    }

    @Test
    public void testAutoReturnConnection() throws Exception {
        Connection conn = cp.getConnection();
        cp.close();
        Thread.sleep(1500);
        conn.close();
    }

}