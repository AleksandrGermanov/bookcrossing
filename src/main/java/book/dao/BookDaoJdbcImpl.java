package book.dao;

import book.model.Book;
import exception.DbException;
import exception.notfound.BookNotFoundException;
import user.dao.UserLazyInitProxy;
import user.model.User;
import util.jdbc.InConnectionSupplier;
import util.jdbc.JdbcUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class BookDaoJdbcImpl implements BookDao {
    @Override
    public Book create(Book book) {
        InConnectionSupplier<Book> bookCreate = connection -> {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                preparedStatement = connection.prepareStatement(QueryPool.BOOK_UPSERT, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setLong(1, book.getId());
                preparedStatement.setString(2, book.getTitle());
                preparedStatement.setString(3, book.getAuthor());
                preparedStatement.setInt(4, book.getPublicationYear());
                preparedStatement.setBoolean(5, book.getIsAvailable());
                preparedStatement.executeUpdate();
                resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    Long generatedId = resultSet.getLong(1);
                    return obtain(generatedId).orElseThrow(() -> new BookNotFoundException(generatedId));
                }
            } catch (SQLException e) {
                throw new DbException("Book creation failed.");
            } finally {
                JdbcUtils.tryClose(resultSet, preparedStatement);
            }
            return null;
        };

        return JdbcUtils.inTransactionGet(bookCreate);
    }


    @Override
    public Book update(Book book) {
            InConnectionSupplier<Book> bookUpdate = connection -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement(QueryPool.BOOK_UPSERT)) {
                    preparedStatement.setLong(1, book.getId());
                    preparedStatement.setString(2, book.getTitle());
                    preparedStatement.setString(3, book.getAuthor());
                    preparedStatement.setInt(4, book.getPublicationYear());
                    preparedStatement.setBoolean(5, book.getIsAvailable());
                    preparedStatement.executeUpdate();
                    return obtain(book.getId()).orElseThrow(() -> new BookNotFoundException(book.getId()));
                } catch (SQLException e) {
                    throw new DbException("Book update failed.");
                }
            };

            return JdbcUtils.inTransactionGet(bookUpdate);
        }

    @Override
    public Optional<Book> obtain(Long id) {
        InConnectionSupplier<Book> bookRead = connection -> {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                preparedStatement = connection.prepareStatement(QueryPool.BOOK_SELECT_BY_ID);
                preparedStatement.setLong(1, id);
                resultSet = preparedStatement.executeQuery();
                return RowMapper.mapSingleResult(resultSet);
            } catch (SQLException e) {
                throw new DbException("Obtaining the book failed.");
            } finally {
                JdbcUtils.tryClose(resultSet, preparedStatement);
            }
        };

        return Optional.ofNullable(JdbcUtils.inTransactionGet(bookRead));
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public Boolean exists(Long id) {
        return null;
    }

    @Override
    public List<Book> findAll() {
        return null;
    }

    private static class QueryPool {
        private static final String BOOK_SELECT_BY_ID = "SELECT "
                + "b.*, oc.owner_id u_id "
                + "FROM books b "
                + "LEFT JOIN owner_cards oc ON b.id = oc.book_id "
                + "WHERE b.id = ? "
                + "ORDER BY oc.owned_since;";
        private static final String BOOK_UPSERT = "MERGE INTO books(id, title, author, "
                + "publication_year, is_available) "
                + "VALUES(?,?,?,?,?);";
    }

    private static class RowMapper {

        private static Book mapSingleResult(ResultSet resultSet) throws SQLException {
            Book book = new Book();
            Map<Long, User> ownedBy = new LinkedHashMap<>();

            while (resultSet.next()) {
                fillBookFields(book, resultSet);
                addOwnedBy(ownedBy, resultSet);
            }
            book.setOwnedBy(new ArrayList<>(ownedBy.values()));

            return book;
        }

        private static void addOwnedBy(Map<Long, User> ownedBy, ResultSet resultSet) throws SQLException {
            Long userId = resultSet.getLong("u_id");

            if (userId != 0 && !ownedBy.containsKey(userId)) {
                ownedBy.put(userId, new UserLazyInitProxy(userId));
            }
        }

        private static void fillBookFields(Book book, ResultSet resultSet) throws SQLException {
            if (book.getId() == null) {
                book.setId(resultSet.getLong("id"));
            }
            if (book.getTitle() == null) {
                book.setTitle(resultSet.getString("title"));
            }
            if (book.getAuthor() == null) {
                book.setAuthor(resultSet.getString("author"));
            }
            if (book.getPublicationYear() == null) {
                book.setPublicationYear(resultSet.getInt("publication_year"));
            }
            if (book.getIsAvailable() == null) {
                book.setIsAvailable(resultSet.getBoolean("is_available"));
            }
        }
    }
}
