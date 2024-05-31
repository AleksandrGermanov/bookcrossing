package util.jdbc;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

public class SqlFileExecutorTest {
    SqlFileExecutor sqlFileExecutor = new SqlFileExecutor();

    @Test
    void executeSchema() {
        Path testPath = Path.of(System.getProperty("user.dir"), "src/test/resources", "schema.sql");
        Assertions.assertDoesNotThrow(() -> sqlFileExecutor.executeSchema(testPath));
    }
}