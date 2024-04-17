package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public class JdbcUtils {


    private static void inConnectionRun(InConnectionRunnable runnable) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/books",
                "postgres", "postgres")) {
            runnable.run(connection);
        } catch (SQLException e) {
            System.out.println("Handler needed");
            e.printStackTrace();
        }
    }

    private static <T> Optional<T> inConnectionObtain(InConnectionSupplier<T> supplier) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/books",
                "postgres", "postgres")) {
            return Optional.ofNullable(supplier.get(connection));
        } catch (SQLException e) {
            System.out.println("Handler needed");
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static void main(String[] args) {
        inConnectionRun((c) -> System.out.println("Ok"));
        System.out.println(inConnectionObtain((c) -> "Ok").get());
    }

    interface InConnectionRunnable {
        void run(Connection connection);
    }

    interface InConnectionSupplier<T> {
        T get(Connection connection);
    }
}
