import util.jdbc.SqlFileExecutor;
import out.TomcatStarter;

public class Main {
    public static void main(String[] args) throws Exception{
        SqlFileExecutor.executeSchema();
        TomcatStarter.start();
    }
}
