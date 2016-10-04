package task1;

import dao.DaoClass;
import dbconnecton.ConnectionPool;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Performs database operations
 */
@Log4j2
public class DBAccessor {
    private final static Pattern SQL_INSERT_COLUMN = Pattern.compile("(?:\\(|,\\s*)(\\w+)(?=[\\s*,|\\s*)])");

    private ConnectionPool cp;

    DBAccessor(Path configFile) {
        Properties props = new Properties();
        try (InputStream is = Files.newInputStream(configFile)) {
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Error reading DB config file " + configFile.getFileName());
        }

        cp = ConnectionPool.builder()
                .setDriver(props.getProperty("driver"))
                .setUrl(props.getProperty("url"))
                .setPoolSize(Integer.valueOf(props.getProperty("poolsize")))
                .create();
    }

    boolean testConnection() {
        try (Connection conn = cp.getConnection()) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    boolean runSQLBatch(String[] queries) {
        try (Connection conn = cp.getConnection(); Statement st = conn.createStatement()) {
            for (String q : queries)
                try {
                    st.addBatch(q);
                } catch (SQLException e) {
                    log.error("Cannot add '{}' to SQL batch", q);
                }
            st.executeBatch();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    int runSQLUpdate(String update) {
        try (Connection conn = cp.getConnection(); Statement st = conn.createStatement()) {
            st.executeUpdate(update);

            return st.getUpdateCount();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private <T extends DaoClass> List<T> packIntoList(Supplier<T> objFactory, ResultSet rs) throws SQLException {
        List<T> resultList = new ArrayList<>(20);
        while (rs.next()) {
            T instance = T.createFromResultSet(objFactory, rs);
            resultList.add(instance);
        }
        return resultList;
    }

    public <T extends DaoClass> List<T> loadListFromDB(Supplier<T> resultFactory, String query) {
        try (Connection conn = cp.getConnection(); Statement st = conn.createStatement()) {
            return packIntoList(resultFactory, st.executeQuery(query));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends DaoClass> List<T> loadListFromDB(Supplier<T> resultFactory, String parameterizedQuery, Object... params) {
        try (Connection conn = cp.getConnection(); PreparedStatement st = conn.prepareStatement(parameterizedQuery)) {
            for (int i = 0; i < params.length; i++)
                st.setObject(i + 1, params[i]);

            return packIntoList(resultFactory, st.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends DaoClass> int addObjectToDB(T object, String parameterizedQuery) {
        List<String> requiredColumns = new ArrayList<>();

        Matcher m = SQL_INSERT_COLUMN.matcher(parameterizedQuery);
        while (m.find())
            requiredColumns.add(m.group(1));

        Object[] objArray = requiredColumns.stream().map(object::getField).toArray();
        try (Connection conn = cp.getConnection(); PreparedStatement st = conn.prepareStatement(parameterizedQuery)) {
            for (int i = 0; i < objArray.length; i++)
                st.setObject(i + 1, objArray[i]);
            return st.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
