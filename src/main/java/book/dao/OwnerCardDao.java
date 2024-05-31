package book.dao;

import book.model.OwnerCard;
import util.dao.CommonDao;

import java.util.Optional;

public interface OwnerCardDao extends CommonDao<OwnerCard, Long> {
    Optional<OwnerCard> obtainCurrentByBookId(Long bookId);
}
