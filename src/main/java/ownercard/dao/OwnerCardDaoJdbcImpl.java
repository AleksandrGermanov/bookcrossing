package ownercard.dao;

import book.dao.BookLazyInitProxy;
import exception.BookcrossingException;
import exception.DbException;
import exception.notfound.OwnerCardNotFoundException;
import ownercard.model.OwnerCard;
import user.dao.UserLazyInitProxy;
import util.jdbc.InConnectionRunnable;
import util.jdbc.InConnectionSupplier;
import util.jdbc.JdbcUtils;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class OwnerCardDaoJdbcImpl implements OwnerCardDao {
    @Override
    public OwnerCard create(OwnerCard ownerCard) {
        InConnectionSupplier<Long> cardCreate = connection -> {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                preparedStatement = connection.prepareStatement(QueryPool.CARD_UPSERT, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setLong(1, ownerCard.getId());
                preparedStatement.setLong(2, ownerCard.getOwner().getId());
                preparedStatement.setLong(3, ownerCard.getBook().getId());
                preparedStatement.setTimestamp(4, Timestamp.valueOf(ownerCard.getOwnedSince()));
                preparedStatement.setTimestamp(5, Timestamp.valueOf(ownerCard.getOwnedTill()));
                preparedStatement.executeUpdate();
                resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
            } catch (SQLException e) {
                throw new DbException("OwnerCard creation failed.");
            } finally {
                JdbcUtils.tryClose(resultSet, preparedStatement);
            }
            return null;
        };

        Long generatedId = JdbcUtils.inTransactionGet(cardCreate);
        return obtain(generatedId).orElseThrow(() -> new OwnerCardNotFoundException(generatedId));
    }

    @Override
    public OwnerCard update(OwnerCard ownerCard) {
        InConnectionRunnable cardUpdate = connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(QueryPool.CARD_UPSERT)) {
                preparedStatement.setLong(1, ownerCard.getId());
                preparedStatement.setLong(2, ownerCard.getOwner().getId());
                preparedStatement.setLong(3, ownerCard.getBook().getId());
                preparedStatement.setTimestamp(4, Timestamp.valueOf(ownerCard.getOwnedSince()));
                preparedStatement.setTimestamp(5, Timestamp.valueOf(ownerCard.getOwnedTill()));
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DbException("OwnerCard update failed.");
            }
        };

        JdbcUtils.inTransactionRun(cardUpdate);
        return obtain(ownerCard.getId())
                .orElseThrow(() -> new OwnerCardNotFoundException(ownerCard.getId()));
    }

    @Override
    public Optional<OwnerCard> obtain(Long id) {
        InConnectionSupplier<OwnerCard> cardRead = connection -> {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                preparedStatement = connection.prepareStatement(QueryPool.CARD_SELECT_BY_ID);
                preparedStatement.setLong(1, id);
                resultSet = preparedStatement.executeQuery();
                return RowMapper.mapSingleResult(resultSet);
            } catch (SQLException e) {
                throw new DbException("Obtaining the ownerCard failed.");
            } finally {
                JdbcUtils.tryClose(resultSet, preparedStatement);
            }
        };

        return Optional.ofNullable(JdbcUtils.inTransactionGet(cardRead));
    }

    @Override
    public void delete(Long id) {
        throw new BookcrossingException("Delete is not implemented for ownerCard entity.");
    }

    @Override
    public Boolean exists(Long id) {
        InConnectionSupplier<Boolean> cardExists = connection -> {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                preparedStatement = connection.prepareStatement(QueryPool.CARD_EXISTS);
                preparedStatement.setLong(1, id);
                resultSet = preparedStatement.executeQuery();
                return resultSet.next();
            } catch (SQLException e) {
                throw new DbException("OwnerCard existence check failed.");
            } finally {
                JdbcUtils.tryClose(resultSet, preparedStatement);
            }
        };

        return JdbcUtils.inTransactionGet(cardExists);
    }

    @Override
    public List<OwnerCard> findAll() {
        throw new BookcrossingException("FindAll is not implemented for ownerCard entity.");
    }

    private static class QueryPool {
        private static final String CARD_SELECT_BY_ID = "SELECT * "
                + "FROM owner_cards "
                + "WHERE id = ? ";
        private static final String CARD_UPSERT = "MERGE INTO owner_cards(id, owner_id, book_id, "
                + "owned_since, owned_till) "
                + "VALUES (?,?,?,?,?)";
        private static final String CARD_EXISTS = "SELECT EXISTS(SELECT 1 FROM owner_cards WHERE id = ?);";
    }

    private static class RowMapper {
        public static OwnerCard mapSingleResult(ResultSet resultSet) throws SQLException {
            OwnerCard ownerCard = new OwnerCard();
            if (resultSet.next()) {
                ownerCard.setId(resultSet.getLong("id"));
                ownerCard.setOwner(new UserLazyInitProxy(resultSet.getLong("owner_id")));
                ownerCard.setBook(new BookLazyInitProxy(resultSet.getLong("book_id")));
                ownerCard.setOwnedSince(resultSet.getTimestamp("owned_since").toLocalDateTime());
                ownerCard.setOwnedTill(resultSet.getTimestamp("owned_till").toLocalDateTime());
            }
            return ownerCard.getId() != null
                    ? ownerCard
                    : null;
        }
    }
}
