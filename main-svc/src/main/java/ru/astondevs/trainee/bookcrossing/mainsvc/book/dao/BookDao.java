package ru.astondevs.trainee.bookcrossing.mainsvc.book.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.model.Book;

@Repository
public interface BookDao extends JpaRepository<Book, Long>, BookCriteriaSearch {

}