package util.jdbc;

import java.sql.Connection;

public interface InConnectionSupplier<T> {
    T get(Connection connection);
}
