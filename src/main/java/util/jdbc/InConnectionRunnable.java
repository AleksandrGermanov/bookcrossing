package util.jdbc;

import java.sql.Connection;

public interface InConnectionRunnable {
    void run(Connection connection);
}
