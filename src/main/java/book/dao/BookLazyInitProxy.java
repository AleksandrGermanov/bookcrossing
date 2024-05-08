package book.dao;

import book.model.Book;
import exception.notfound.BookNotFoundException;
import lombok.RequiredArgsConstructor;
import user.model.User;
import util.beanlib.DaoLib;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class BookLazyInitProxy extends Book {
    private final Long id;
    private final BookDao bookDao;
    private Book book;

    public BookLazyInitProxy(Long id) {
        this.id = id;
        bookDao = DaoLib.getDefaultBookDao();
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

    public boolean referencesExisting(){
        return bookDao.exists(id);
    }

    private void initBook() {
        if (book == null) {
            book = bookDao.obtain(id).orElseThrow(() -> new BookNotFoundException(id));
        }
    }

    @Override
    public String toString() {
        initBook();
        return book.toString();
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
        return book.hashCode();
    }
}
