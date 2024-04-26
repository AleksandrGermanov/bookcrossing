package user.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import user.model.User;
import util.jdbc.JdbcUtils;
import util.jdbc.SqlFileExecutor;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserDaoJdbcImplTest {
    User one;
    User two;
    User three;
    UserDaoJdbcImpl userDao = new UserDaoJdbcImpl();

    @BeforeAll
    static void setJdbc(){
        JdbcUtils.setPath(
                Path.of(System.getProperty("user.dir"), "/src/test/resources", "jdbc.properties"));
    }
    @BeforeEach
    public void setup() {
        SqlFileExecutor.executeSchema(
                Path.of(System.getProperty("user.dir"), "src/test/resources", "schema.sql"));
        one = new User(null, "one", "one@email.fake");
        two = new User(null, "two", "two@email.fake");
        three = new User(null, "three", "three@email.fake");
    }

    @Test
    void create() {
        User fetched = userDao.create(one);

        assertTrue(fetched.getId()>0);
        assertEquals(one.getName(), fetched.getName());
        assertEquals(one.getEmail(), fetched.getEmail());
    }

    @Test
    void update() {
        User created = userDao.create(two);
        one.setId(created.getId());
        User updated = userDao.update(one);

        assertEquals(created.getId(), updated.getId());
        assertEquals(one.getName(), updated.getName());
        assertEquals(one.getEmail(), updated.getEmail());
    }

    @Test
    void obtain() {
        User created = userDao.create(one);
        User fetched = userDao.obtain(created.getId()).get();

        assertEquals(created, fetched);
    }

    @Test
    void delete() {
        User created = userDao.create(one);

        userDao.delete(created.getId());

        assertTrue(userDao.obtain(created.getId()).isEmpty());
    }

    @Test
    void existsWhenUserExistsReturnsTrue() {
        User created = userDao.create(one);

        assertTrue(userDao.exists(created.getId()));
    }

    @Test
    void existsWhenUserNotExistsReturnsFalse() {
        assertFalse(userDao.exists(1L));
    }

    @Test
    void findAllWhenNoUsersReturnsEmptyList() {
        List<User> users = userDao.findAll();

        assertTrue(users.isEmpty());
    }

    @Test
    void findAllWhenHasUsersReturnsListWithValues() {
        User created1 = userDao.create(one);
        User created2 =userDao.create(two);
        User created3 =userDao.create(three);


        List<User> users = userDao.findAll();

        assertEquals(List.of(created1, created2, created3), users);
    }
}