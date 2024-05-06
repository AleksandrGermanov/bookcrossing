package book.dao;

import exception.DbException;
import exception.notfound.OwnerCardNotFoundException;
import book.model.OwnerCard;
import user.dao.UserLazyInitProxy;
import util.jdbc.InConnectionRunnable;
import util.jdbc.InConnectionSupplier;
import util.jdbc.JdbcUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class OwnerCardDaoJdbcImpl implements OwnerCardDao {
    @Override
    public OwnerCard create(OwnerCard ownerCard) {
        InConnectionSupplier<Long> cardCreate = connection -> {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                preparedStatement = connection.prepareStatement(QueryPool.CARD_INSERT, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setLong(1, ownerCard.getOwner().getId());
                preparedStatement.setLong(2, ownerCard.getBook().getId());
                preparedStatement.setTimestamp(3, Timestamp.valueOf(ownerCard.getOwnedSince()));
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
            try (PreparedStatement preparedStatement = connection.prepareStatement(QueryPool.CARD_UPDATE)) {
                preparedStatement.setLong(1, ownerCard.getOwner().getId());
                preparedStatement.setLong(2, ownerCard.getBook().getId());
                preparedStatement.setTimestamp(3, Timestamp.valueOf(ownerCard.getOwnedSince()));
                preparedStatement.setTimestamp(4, Timestamp.valueOf(ownerCard.getOwnedTill()));
                preparedStatement.setLong(5, ownerCard.getId());
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
    public Optional<OwnerCard> obtainCurrentByBookId(Long bookId) {
        InConnectionSupplier<OwnerCard> cardRead = connection -> {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                preparedStatement = connection.prepareStatement(QueryPool.CARD_SELECT_CURRENT_BY_BOOK_ID);
                preparedStatement.setLong(1, bookId);
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
        //Implementation is not supposed, method does nothing according to LSP;
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
                if (resultSet.next()){
                    return resultSet.getBoolean(1);
                };
            } catch (SQLException e) {
                throw new DbException("OwnerCard existence check failed.");
            } finally {
                JdbcUtils.tryClose(resultSet, preparedStatement);
            }
            return null;
        };

        return JdbcUtils.inTransactionGet(cardExists);
    }

    @Override
    public List<OwnerCard> findAll() {
        //Implementation is not supposed, returns empty list according to LSP;
        return Collections.emptyList();
    }

    private static class QueryPool {
        private static final String CARD_INSERT = "INSERT INTO owner_cards(owner_id, book_id, "
                + "owned_since) "
                + "VALUES (?,?,?)";
        private static final String CARD_UPDATE = "UPDATE owner_cards "
                +"SET owner_id = ?, book_id = ?, owned_since = ?, owned_till = ? "
                +"WHERE id = ?";
        private static final String CARD_SELECT_BY_ID = "SELECT * "
                + "FROM owner_cards "
                + "WHERE id = ? ";
        private static final String CARD_SELECT_CURRENT_BY_BOOK_ID = "SELECT * "
                + "FROM owner_cards "
                + "WHERE book_id = ? "
                + "AND owned_till IS NULL;";
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
                LocalDateTime till = resultSet.getTimestamp("owned_till") != null
                        ? resultSet.getTimestamp("owned_till").toLocalDateTime()
                        : null;
                ownerCard.setOwnedTill(till);
            }
            return ownerCard.getId() != null
                    ? ownerCard
                    : null;
        }
    }
}
