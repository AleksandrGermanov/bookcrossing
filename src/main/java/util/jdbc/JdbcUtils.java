package util.jdbc;

import exception.DbException;

import java.sql.*;

public class JdbcUtils {

    private static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/books",
                    "postgres", "postgres");
        } catch (SQLException e) {
            System.out.println("Handler needed");
            e.printStackTrace();
        }
        return connection;
    }

    public static void inTransactionRun(InConnectionRunnable runnable) {
        Connection connection = getConnection();
        if (connection == null) {
            return;
        }

        try (connection) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            runnable.run(connection);
            connection.commit();
        } catch (SQLException e) {
            try (connection) {
                connection.rollback();
                e.printStackTrace();
            } catch (SQLException ex) {
                System.out.println("Rollback failed.");
                e.printStackTrace();
            }
        }
    }

    public static <T> T inTransactionGet(InConnectionSupplier<T> supplier) {
        Connection connection = getConnection();
        if (connection == null) {
            return null;
        }
        T value = null;

        try (connection) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
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

    public static void main(String[] args) {
        inTransactionRun((c) -> System.out.println("ok"));
        System.out.println((String) inTransactionGet((c) -> "ok"));
    }
}
