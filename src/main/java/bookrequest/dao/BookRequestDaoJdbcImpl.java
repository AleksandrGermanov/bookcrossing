package bookrequest.dao;

import book.dao.BookLazyInitProxy;
import bookrequest.model.BookRequest;
import exception.DbException;
import exception.notfound.BookRequestNotFoundException;
import user.dao.UserLazyInitProxy;
import util.jdbc.InConnectionRunnable;
import util.jdbc.InConnectionSupplier;
import util.jdbc.JdbcUtils;

import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BookRequestDaoJdbcImpl implements BookRequestDao {
    @Override
    public BookRequest create(BookRequest bookRequest) {
        InConnectionSupplier<Long> requestCreate = connection -> {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                preparedStatement = connection.prepareStatement(QueryPool.REQUEST_INSERT, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setLong(1, bookRequest.getRequester().getId());
                preparedStatement.setLong(2, bookRequest.getBook().getId());
                preparedStatement.setTimestamp(3, Timestamp.valueOf(bookRequest.getCreatedOn()));
                preparedStatement.executeUpdate();
                resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
            } catch (SQLException e) {
                throw new DbException(String.format("BookRequest creation failed due to %s with message %s."
                        , e.getClass(), e.getMessage()));
            } finally {
                JdbcUtils.tryClose(resultSet, preparedStatement);
            }
            return null;
        };

        Long generatedId = JdbcUtils.inTransactionGet(requestCreate);
        return obtain(generatedId).orElseThrow(() -> new BookRequestNotFoundException(generatedId));
    }

    @Override
    public BookRequest update(BookRequest bookRequest) {
        InConnectionRunnable requestUpdate = connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(QueryPool.REQUEST_UPDATE)) {
                preparedStatement.setLong(1, bookRequest.getRequester().getId());
                preparedStatement.setLong(2, bookRequest.getBook().getId());
                preparedStatement.setTimestamp(3, Timestamp.valueOf(bookRequest.getCreatedOn()));
                preparedStatement.setLong(4, bookRequest.getId());
                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                throw new DbException("BookRequest update failed.");
            }
        };

        JdbcUtils.inTransactionRun(requestUpdate);
        return obtain(bookRequest.getId()).orElseThrow(
                () -> new BookRequestNotFoundException(bookRequest.getId()));
    }

    @Override
    public Optional<BookRequest> obtain(Long id) {
        InConnectionSupplier<BookRequest> requestRead = connection -> {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                preparedStatement = connection.prepareStatement(QueryPool.REQUEST_SELECT_BY_ID);
                preparedStatement.setLong(1, id);
                resultSet = preparedStatement.executeQuery();
                return RowMapper.mapSingleResult(resultSet);
            } catch (SQLException e) {
                throw new DbException("Obtaining the bookRequest failed.");
            } finally {
                JdbcUtils.tryClose(resultSet, preparedStatement);
            }
        };

        return Optional.ofNullable(JdbcUtils.inTransactionGet(requestRead));
    }

    @Override
    public void delete(Long id) {
        InConnectionRunnable bookDelete = connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(QueryPool.REQUEST_DELETE)) {
                preparedStatement.setLong(1, id);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DbException("BookRequest deletion failed.");
            }
        };

        JdbcUtils.inTransactionRun(bookDelete);
    }

    @Override
    public Boolean exists(Long id) {
        InConnectionSupplier<Boolean> requestExists = connection -> {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                preparedStatement = connection.prepareStatement(QueryPool.REQUEST_EXISTS);
                preparedStatement.setLong(1, id);
                resultSet = preparedStatement.executeQuery();
                if(resultSet.next()){
                    return resultSet.getBoolean(1);
                };
            } catch (SQLException e) {
                throw new DbException("Book existence check failed.");
            } finally {
                JdbcUtils.tryClose(resultSet, preparedStatement);
            }
            return null;
        };

        return JdbcUtils.inTransactionGet(requestExists);
    }

    @Override
    public List<BookRequest> findAll() {
        //Implementation is not supposed, returns empty list according to LSP;
        return Collections.emptyList();
    }

    private static class QueryPool {
        private static final String REQUEST_INSERT = "INSERT INTO book_requests("
                + "requester_id, book_id, created_on) "
                + "VALUES (?,?,?);";
        private static final String REQUEST_UPDATE = "UPDATE book_requests "
                + "SET requester_id = ?, book_id = ?, created_on = ? "
                + "WHERE id = ?;";
        private static final String REQUEST_SELECT_BY_ID = "SELECT * "
            + "FROM book_requests "
            + "WHERE id = ?";
        private static final String REQUEST_DELETE = "DELETE FROM book_requests "
                + " WHERE id = ?;";
        private static final String REQUEST_EXISTS = "SELECT EXISTS(SELECT 1 "
                + "FROM book_requests "
                + "WHERE id = ?)";
    }

    private static class RowMapper {
        private static BookRequest mapSingleResult(ResultSet resultSet) throws SQLException {
            BookRequest bookRequest = new BookRequest();
            if (resultSet.next()) {
                bookRequest.setId(resultSet.getLong("id"));
                bookRequest.setRequester(new UserLazyInitProxy(resultSet.getLong("requester_id")));
                bookRequest.setBook(new BookLazyInitProxy(resultSet.getLong("book_id")));
                bookRequest.setCreatedOn(resultSet.getTimestamp("created_on").toLocalDateTime());
            }

            return bookRequest.getId() != null
                    ? bookRequest
                    : null;
        }
    }
}