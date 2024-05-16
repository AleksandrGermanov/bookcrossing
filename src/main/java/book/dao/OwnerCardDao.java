package book.dao;

import book.model.OwnerCard;
import org.springframework.data.jpa.repository.JpaRepository;
import util.dao.CommonDao;

import java.util.Optional;

public interface OwnerCardDao extends JpaRepository<OwnerCard, Long> {
    Optional<OwnerCard> obtainCurrentByBookId(Long bookId);
}
