import out.TomcatStarter;
import util.jdbc.SqlFileExecutor;

public class Main {
    public static void main(String[] args) throws Exception {
        SqlFileExecutor.executeSchema();
        TomcatStarter.start();
    }
}
