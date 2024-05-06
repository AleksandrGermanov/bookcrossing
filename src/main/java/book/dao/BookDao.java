package book.dao;

import book.model.Book;
import book.service.BookFetchOrder;
import util.dao.CommonDao;

import java.util.LinkedHashMap;
import java.util.List;

public interface BookDao extends CommonDao<Book,Long> {
    List<Book> searchByParams(LinkedHashMap<String, String> params, BookFetchOrder order);
}
