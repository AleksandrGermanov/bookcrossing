package util.jdbc;

import exception.DbException;
import exception.SqlFileExecutionException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SqlFileExecutor {
    private final Path deafultPath = Path.of(System.getProperty("user.dir"),"src/main/resources", "schema.sql");

    public void executeSchema(){
        executeSchema(deafultPath);
    }

    public void executeSchema(Path path){
    try(Stream<String> queryStream = Files.lines(
            path,
            StandardCharsets.UTF_8)){
        JdbcUtils.inTransactionRun((prepareRunnable(queryStream.collect(Collectors.joining()))));
    }catch (IOException e){
        throw new SqlFileExecutionException("Reading from schema.sql failed.");
    }
    }

    private InConnectionRunnable prepareRunnable(String query){
        return connection -> {
            try (Statement statement = connection.createStatement()){
                statement.execute(query);
            } catch (SQLException e){
                throw new DbException("Execution of schema.sql failed.");
            }
        };
    }
}