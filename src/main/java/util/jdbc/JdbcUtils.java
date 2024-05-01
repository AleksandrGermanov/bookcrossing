package util.jdbc;

import exception.BookcrossingIOException;
import exception.DbException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.List;
import java.util.stream.Stream;

public class JdbcUtils {
    static {
    init();
    }

    private static Path propertiesPath;
    private static String url;
    private static String user;
    private static String password;


    public static void setPath(Path propertiesPath) {
        JdbcUtils.propertiesPath = propertiesPath;
        init();
    }



    private static void init() {
        PropertiesReader reader = propertiesPath != null
                ? new PropertiesReader(propertiesPath)
                : new PropertiesReader();
        url = reader.getProperty(PropertiesReader.DB_URL);
        user = reader.getProperty(PropertiesReader.DB_USER);
        password = reader.getProperty(PropertiesReader.DB_PASSWORD);
    }

    private static Connection getConnection() {
        Connection connection;
        try {
            connection = DriverManager.getConnection(url,
                    user, password);
        } catch (SQLException e) {
            throw new DbException("Could not obtain connection.");
        }
        return connection;
    }

    public static void inTransactionRun(InConnectionRunnable runnable) {
        Connection connection = getConnection();
        if (connection == null) {
            return;
        }

        try {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            runnable.run(connection);
            connection.commit();
        } catch (SQLException | DbException e) {
            try (connection) {
                connection.rollback();
                throw new DbException("Transaction rollback performed due to "
                        + e.getClass() + " with message " + "'" + e.getMessage() + "'.");
            } catch (SQLException ex) {
                System.out.println("Rollback failed.");
                throw new DbException("Rollback failed.");
            }
        }
    }

    public static <T> T inTransactionGet(InConnectionSupplier<T> supplier) {
        Connection connection = getConnection();
        if (connection == null) {
            return null;
        }
        T value;

        try {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            value = supplier.get(connection);
            connection.commit();
        } catch (SQLException | DbException e) {
            try (connection) {
                connection.rollback();
                throw new DbException("Transaction rollback performed due to "
                        + e.getClass() + " with message " + "'" + e.getMessage() + "'.");
            } catch (SQLException ex) {
                throw new DbException("Rollback failed.");
            }
        }

        return value;
    }

    public static void tryClose(ResultSet resultSet, Statement statement) {
        try {
            if (resultSet != null
                    && !resultSet.isClosed()) {
                resultSet.close();
            }
            if (statement != null
                    && statement.isClosed())
                statement.close();
        } catch (SQLException e) {
            throw new DbException("Could not close resultSet or statement.");
        }
    }

    public static class PropertiesReader {
        private static final Path defaultPath = Path.of(System.getProperty("user.dir"),
                "src/main/resources", "jdbc.properties");
        private static final String DB_URL = "db.url";
        private static final String DB_USER = "db.user";
        private static final String DB_PASSWORD = "db.password";
        private final Path directoryPath;
        private List<String> properties;

        public PropertiesReader() {
            this.directoryPath = defaultPath;
            fetchProperties();
        }

        public PropertiesReader(Path directoryPath) {
            this.directoryPath = directoryPath;
            fetchProperties();
        }

        private void fetchProperties() {
            try (Stream<String> props = Files.lines(directoryPath, StandardCharsets.UTF_8)) {
                properties = props.toList();
            } catch (IOException e) {
                throw new BookcrossingIOException("Reading from 'jdbc.properties failed.");
            }
        }

        private String getProperty(String propertyName) {
            String prop = properties.stream()
                    .filter(s -> s.startsWith(propertyName))
                    .findFirst()
                    .orElseThrow(() -> new BookcrossingIOException(
                            String.format("Cannot find property = %s.", propertyName)));

            return prop.substring(prop.indexOf('=') + 1).trim();
        }
    }
}
