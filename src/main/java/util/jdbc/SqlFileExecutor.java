package util.jdbc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SqlFileExecutor {

    public static void executeSchema(){
    try(Stream<String> queryStream = Files.lines(
            Path.of(System.getProperty("user.dir"),"src/main/resources", "schema.sql"),
            StandardCharsets.UTF_8)){
        JdbcUtils.inTransactionRun((prepareRunnable(queryStream.collect(Collectors.joining()))));
    }catch (IOException e){
        System.out.println("reading from schema.sql failed");
        e.printStackTrace();
    }
    }

    private static InConnectionRunnable prepareRunnable(String query){
        return connection -> {
            try (Statement statement = connection.createStatement()){
                statement.execute(query);
            } catch (SQLException e){
                System.out.println("sql ex");
                e.printStackTrace();
            }
        };
    }
}