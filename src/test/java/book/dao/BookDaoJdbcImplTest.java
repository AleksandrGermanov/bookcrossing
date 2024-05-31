package book.dao;

import book.model.Book;
import book.service.BookFetchOrder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.jdbc.JdbcUtils;
import util.jdbc.SqlFileExecutor;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookDaoJdbcImplTest {
    Book b1;
    Book b2;
    Book b3;
    BookDaoJdbcImpl bookDao = new BookDaoJdbcImpl();

    @BeforeAll
    static void setJdbc() {
        JdbcUtils.setPath(
                Path.of(System.getProperty("user.dir"), "/src/test/resources", "jdbc.properties"));
    }

    @BeforeEach
    public void setup() {
        SqlFileExecutor.executeSchema(
                Path.of(System.getProperty("user.dir"), "src/test/resources", "schema.sql"));
        b1 = new Book(null, "one", "authorOne", 2020, true);
        b2 = new Book(null, "two", "authorTwo", 1999, false);
        b3 = new Book(null, "three", "authorThree", 2024, true);
    }

    @Test
    void create() {
        Book created = bookDao.create(b1);
        b1.setId(created.getId());
        b1.setOwnedBy(Collections.emptyList());

        assertEquals(b1, created);
    }

    @Test
    void update() {
        Book created = bookDao.create(b2);
        b1.setId(created.getId());
        b1.setOwnedBy(Collections.emptyList());
        Book updated = bookDao.update(b1);

        assertEquals(b1, updated);
    }

    @Test
    void obtain() {
        Book created = bookDao.create(b1);

        assertEquals(created, bookDao.obtain(created.getId()).get());
    }

    @Test
    void delete() {
        Book created = bookDao.create(b1);
        bookDao.delete(created.getId());

        assertTrue(bookDao.obtain(created.getId()).isEmpty());
    }

    @Test
    void existsWhenBookNotFoundReturnsFalse() {
        assertFalse(bookDao.exists(1L));
    }

    @Test
    void existsWhenBookFoundReturnsTrue() {
        Book created = bookDao.create(b1);

        assertTrue(bookDao.exists(created.getId()));
    }

    @Test
    void findAll() {
        Book created1 = bookDao.create(b1);
        Book created2 = bookDao.create(b2);
        Book created3 = bookDao.create(b3);

        assertEquals(List.of(created1, created2, created3), bookDao.findAll());
    }

    @Test
    void searchByParamsWhenAllParamsReturnsList() {
        bookDao.create(b1);
        bookDao.create(b2);
        Book created3 = bookDao.create(b3);

        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("title", "TH");
        params.put("author", "thort");
        params.put("published-since", "1900");
        params.put("is-available", "true");

        assertEquals(List.of(created3), bookDao.searchByParams(params, null));
    }

    @Test
    void searchByParamsWhenOneParamReturnsList() {
        bookDao.create(b1);
        Book created2 = bookDao.create(b2);
        Book created3 = bookDao.create(b3);

        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("author", "thort");

        assertEquals(List.of(created2, created3), bookDao.searchByParams(params, null));
    }

    @Test
    void searchByParamsWhenHasOrderOrders() {
        Book created1 = bookDao.create(b1);
        Book created2 = bookDao.create(b2);
        Book created3 = bookDao.create(b3);
        LinkedHashMap<String, String> emptyMap = new LinkedHashMap<>();

        assertEquals(List.of(created1, created2, created3),
                bookDao.searchByParams(emptyMap, BookFetchOrder.DEFAULT));
        assertEquals(List.of(created1, created3, created2),
                bookDao.searchByParams(emptyMap, BookFetchOrder.IS_AVAILABLE_DESC));
        assertEquals(List.of(created3, created1, created2),
                bookDao.searchByParams(emptyMap, BookFetchOrder.PUBLICATION_YEAR_DESC));
    }
}