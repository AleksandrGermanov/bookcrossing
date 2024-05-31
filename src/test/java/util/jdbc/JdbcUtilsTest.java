package util.jdbc;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class JdbcUtilsTest {
    Path h2Properties = Path.of(System.getProperty("user.dir"),"/src/test/resources", "jdbc.properties");
    @Test
    void inTransactionRunWhenJdbcPostgresDoesNotThrow() {
        assertDoesNotThrow(() -> JdbcUtils.inTransactionRun((c) -> System.out.println("ok")));
    }

    @Test
    void inTransactionRunWhenJdbcH2DoesNotThrow() {
        JdbcUtils.setPath(h2Properties);
        assertDoesNotThrow(() -> JdbcUtils.inTransactionRun((c) -> System.out.println("ok")));
    }


    @Test
    void inTransactionGetWhenJdbcPostgresReturnsValue() {
        assertEquals("ok", JdbcUtils.inTransactionGet((c) -> "ok"));
    }

    @Test
    void inTransactionGetWhenJdbcH2ReturnsValue() {
        JdbcUtils.setPath(h2Properties);
        assertEquals("ok", JdbcUtils.inTransactionGet((c) -> "ok"));
    }
}