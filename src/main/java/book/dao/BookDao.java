package book.dao;

import book.model.Book;
import book.service.BookFetchOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import util.dao.CommonDao;

import java.util.LinkedHashMap;
import java.util.List;

public interface BookDao extends JpaRepository<Book, Long> {
    List<Book> searchByParams(LinkedHashMap<String, String> params, BookFetchOrder order);
}
