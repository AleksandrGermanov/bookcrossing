package util.jdbc;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SqlFileExecutorTest {

    @Test
    void executeSchema() {
        Assertions.assertDoesNotThrow(SqlFileExecutor::executeSchema);
    }
}