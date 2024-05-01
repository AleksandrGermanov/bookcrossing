package ownercard.dao;

import book.dao.BookDao;
import book.dao.BookDaoJdbcImpl;
import book.model.Book;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ownercard.model.OwnerCard;
import user.dao.UserDao;
import user.dao.UserDaoJdbcImpl;
import user.model.User;
import util.jdbc.JdbcUtils;
import util.jdbc.SqlFileExecutor;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OwnerCardDaoJdbcImplTest {

    User u1;
    Book b1;
    OwnerCard oc1;
    OwnerCardDao cardDao = new OwnerCardDaoJdbcImpl();
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
        userDao.create(u1);
        b1 = new Book(1L, "one", "authorOne", 2020, true, Collections.emptyList());
        bookDao.create(b1);
        oc1 = new OwnerCard(1L, u1, b1,
                LocalDateTime.of(2010, 10, 10, 10, 10),
                null);
    }

    @Test
    void create() {
        OwnerCard created = cardDao.create(oc1);

        assertEquals(oc1, created);
    }

    @Test
    void update() {
        OwnerCard created = cardDao.create(oc1);
        created.setOwnedTill(LocalDateTime.of(2011, 11, 11, 11, 11));
        assertTrue(created.getOwner().getBooksInPossession().contains(b1));

        OwnerCard updated = cardDao.update(created);
        assertEquals(created, updated);
        assertTrue(updated.getOwner().getBooksInPossession().isEmpty());
        assertTrue(updated.getBook().getOwnedBy().contains(updated.getOwner()));
    }

    @Test
    void obtain() {
        OwnerCard created = cardDao.create(oc1);

        assertEquals(created, cardDao.obtain(1L).get());
    }

    @Test
    void exists() {
        assertFalse(cardDao.exists(1L));
        cardDao.create(oc1);
        assertTrue(cardDao.exists(1L));
    }
}