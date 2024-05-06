package util.jdbc;

import exception.BookcrossingIOException;
import exception.DbException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SqlFileExecutor {
    private static final Path deafultPath = Path.of(System.getProperty("user.dir"), "src/main/resources", "schema.sql");

    public static void executeSchema() {
        executeSchema(deafultPath);
    }

    public static void executeSchema(Path path) {
        try (Stream<String> queryStream = Files.lines(
                path,
                StandardCharsets.UTF_8)) {
            JdbcUtils.inTransactionRun((prepareRunnable(queryStream.collect(Collectors.joining()))));
        } catch (IOException e) {
            throw new BookcrossingIOException("Reading from schema.sql failed.");
        }
    }

    private static InConnectionRunnable prepareRunnable(String query) {
        return connection -> {
            try (Statement statement = connection.createStatement()) {
                statement.execute(query);
            } catch (SQLException e) {
                throw new DbException("Execution of schema.sql failed.");
            }
        };
    }
}