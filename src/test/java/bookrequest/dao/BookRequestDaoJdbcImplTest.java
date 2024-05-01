package bookrequest.dao;

import book.dao.BookDao;
import book.dao.BookDaoJdbcImpl;
import book.model.Book;
import bookrequest.model.BookRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import user.dao.UserDao;
import user.dao.UserDaoJdbcImpl;
import user.model.User;
import util.jdbc.JdbcUtils;
import util.jdbc.SqlFileExecutor;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookRequestDaoJdbcImplTest {
    User u1;
    User u2;
    Book b1;
    Book b2;
    BookRequest br1;
    BookRequest br2;
    BookRequestDao requestDao = new BookRequestDaoJdbcImpl();
    BookDao bookDao = new BookDaoJdbcImpl();
    UserDao userDao = new UserDaoJdbcImpl();

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
        u2 = new User(2L, "two", "two@email.fake",
                Collections.emptyList(), List.of(2L), Collections.emptyList());
        userDao.create(u1);
        userDao.create(u2);
        b1 = new Book(1L, "one", "authorOne", 2020, true, Collections.emptyList());
        b2 = new Book(2L, "two", "authorTwo", 1999, true, Collections.emptyList());
        bookDao.create(b1);
        bookDao.create(b2);
        br1 = new BookRequest(u1, b1);
        br1.setCreatedOn(LocalDateTime.of(2024, 4, 26, 22, 22));
        br2 = new BookRequest(u2, b2);
        br1.setCreatedOn(LocalDateTime.of(2023, 3, 25, 21, 21));
    }

    @Test
    void create() {
        BookRequest created = requestDao.create(br1);
        br1.setId(created.getId());

        assertEquals(br1, created);
    }

    @Test
    void update() {
        BookRequest created = requestDao.create(br1);
        br2.setId(created.getId());
        br2.setCreatedOn(created.getCreatedOn());
        BookRequest updated = requestDao.update(br2);
        u2.setRequestsFrom(List.of(1L));

        assertEquals(br2, updated);
    }

    @Test
    void obtain() {
        BookRequest created = requestDao.create(br1);

        assertEquals(created, requestDao.obtain(created.getId()).get());
    }

    @Test
    void delete() {
        Book created = bookDao.create(b1);
        bookDao.delete(created.getId());

        assertTrue(bookDao.obtain(created.getId()).isEmpty());
    }

    @Test
    void existsWhenRequestNotFoundReturnsFalse() {
        assertEquals(false, requestDao.exists(1L));
    }

    @Test
    void existsWhenRequestFoundReturnsTrue() {
        BookRequest created = requestDao.create(br1);

        assertTrue(requestDao.exists(created.getId()));
    }
}