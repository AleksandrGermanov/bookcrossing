package util.jdbc;

import exception.BookcrossingIOException;
import exception.DbException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SqlFileExecutor {
    private static final String FILE_NAME = "schema.sql";
    private static final Path DEAFULT_SCHEMA_PATH = Path.of(
            System.getProperty("user.dir"), "src/main/resources", FILE_NAME);

    public static void executeSchema() {
        executeSchema(DEAFULT_SCHEMA_PATH);
    }

    public static void executeSchema(Path schemaPath) {
        try (Stream<String> queryStream = Files.lines(
                schemaPath,
                StandardCharsets.UTF_8)) {
            JdbcUtils.inTransactionRun((prepareRunnable(queryStream.collect(Collectors.joining()))));
        } catch (IOException e) {
            try (InputStream inStream = SqlFileExecutor.class.getClassLoader().getResourceAsStream(FILE_NAME)) {
                if(inStream == null){
                    throw new IOException();
                }
                Stream<String> queryStream = new BufferedReader(new InputStreamReader(
                        inStream, StandardCharsets.UTF_8)).lines();
                JdbcUtils.inTransactionRun((prepareRunnable(queryStream.collect(Collectors.joining()))));
            } catch (IOException ex) {
                throw new BookcrossingIOException("Reading from schema.sql failed.");
            }
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