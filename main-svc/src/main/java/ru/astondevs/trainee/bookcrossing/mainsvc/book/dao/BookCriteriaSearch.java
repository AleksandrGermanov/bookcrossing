package ru.astondevs.trainee.bookcrossing.mainsvc.book.dao;

import org.springframework.stereotype.Repository;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.model.Book;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.service.BookFetchOrder;

import java.util.List;
import java.util.Map;

@Repository
public interface BookCriteriaSearch {

    List<Book> searchByParams(Map<String, Object> params, BookFetchOrder order);

}
