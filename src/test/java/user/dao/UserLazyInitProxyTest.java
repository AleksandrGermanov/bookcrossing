package user.dao;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import user.model.User;
import util.beanlib.dao.DaoLib;
import util.jdbc.JdbcUtils;
import util.jdbc.SqlFileExecutor;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserLazyInitProxyTest {
    User u1;
    UserDao userDao = DaoLib.getDefaultUserDao();

    @BeforeAll
    static void setJdbc() {
        JdbcUtils.setPath(
                Path.of(System.getProperty("user.dir"), "/src/test/resources", "jdbc.properties"));
    }

    @BeforeEach
    public void setup() {
        SqlFileExecutor.executeSchema(
                Path.of(System.getProperty("user.dir"), "src/test/resources", "schema.sql"));
        u1 = new User(1L, "one", "one@email.fake",
                Collections.emptyList(), List.of(1L), Collections.emptyList());
        userDao.create(u1);
    }

    @Test
    void toStringDoesNotThrow(){
        UserLazyInitProxy proxy = new UserLazyInitProxy(1L);
        assertDoesNotThrow(proxy::toString);
    }

    @Test
    void testEquals() {
        assertEquals(new UserLazyInitProxy(1L), userDao.obtain(1L).get());
        assertEquals(userDao.obtain(1L).get(), new UserLazyInitProxy(1L));
    }

    @Test
    void testHashCode() {
        assertEquals(userDao.obtain(1L).get().hashCode(), new UserLazyInitProxy(1L).hashCode());
        assertEquals(new UserLazyInitProxy(1L).hashCode(), userDao.obtain(1L).get().hashCode());
    }
}