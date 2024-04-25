package book.dao;

import book.model.Book;
import exception.notfound.BookNotFoundException;
import user.model.User;
import util.beanlib.dao.DaoLib;

import java.util.List;
import java.util.Objects;

public class BookLazyInitProxy extends Book {
    private final Long id;
    private final BookDao bookDao = DaoLib.getDefaultBookDao();
    private Book book;

    public BookLazyInitProxy(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getTitle() {
        initBook();
        return book.getTitle();
    }

    @Override
    public String getAuthor() {
        initBook();
        return book.getAuthor();
    }

    @Override
    public Integer getPublicationYear() {
        initBook();
        return book.getPublicationYear();
    }

    @Override
    public Boolean getIsAvailable() {
        initBook();
        return book.getIsAvailable();
    }

    @Override
    public List<User> getOwnedBy() {
        initBook();
        return book.getOwnedBy();
    }

    @Override
    public String toString() {
        initBook();
        return book.toString();
    }

    private void initBook() {
        if (book == null) {
            book = bookDao.obtain(id).orElseThrow(() -> new BookNotFoundException(id));
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object != null && Book.class.equals(object.getClass())) {
            initBook();
            return book.equals(object);
        }
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        BookLazyInitProxy that = (BookLazyInitProxy) object;
        return Objects.equals(id, that.id) && Objects.equals(bookDao, that.bookDao) && Objects.equals(book, that.book);
    }

    @Override
    public int hashCode() {
        initBook();
        return Objects.hash(super.hashCode(), id, bookDao, book);
    }
}
