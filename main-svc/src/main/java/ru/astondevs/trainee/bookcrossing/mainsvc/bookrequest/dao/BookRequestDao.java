package ru.astondevs.trainee.bookcrossing.mainsvc.bookrequest.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.astondevs.trainee.bookcrossing.mainsvc.bookrequest.model.BookRequest;

public interface BookRequestDao extends JpaRepository<BookRequest, Long> {
}
