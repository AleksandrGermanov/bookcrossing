package ru.astondevs.trainee.bookcrossing.mainsvc.book.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.model.OwnerCard;

import java.util.Optional;

public interface OwnerCardDao extends JpaRepository<OwnerCard, Long> {
    Optional<OwnerCard> findByBookIdAndOwnedTillIsNull(Long bookId);
}
