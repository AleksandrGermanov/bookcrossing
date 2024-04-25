package user.dao;

import book.model.Book;
import exception.DbException;
import exception.notfound.UserNotFoundException;
import user.model.User;
import util.jdbc.InConnectionRunnable;
import util.jdbc.InConnectionSupplier;
import util.jdbc.JdbcUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class UserDaoJdbcImpl implements UserDao {

    @Override
    public User create(User user) {
        InConnectionSupplier<User> userCreate = connection -> {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                preparedStatement = connection.prepareStatement(QueryPool.USER_UPSERT, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setLong(1, user.getId());
                preparedStatement.setString(2, user.getName());
                preparedStatement.setString(3, user.getEmail());
                preparedStatement.executeUpdate();
                resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    Long generatedId = resultSet.getLong(1);
                    return obtain(generatedId).orElseThrow(()-> new UserNotFoundException(generatedId));
                }
            } catch (SQLException e) {
                throw new DbException("User creation failed.");
            } finally {
                JdbcUtils.tryClose(resultSet, preparedStatement);
            }
            return null;
        };

        return JdbcUtils.inTransactionGet(userCreate);
    }

    @Override
    public User update(User user) {
        InConnectionSupplier<User> userUpdate = connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(QueryPool.USER_UPSERT)) {
                preparedStatement.setLong(1, user.getId());
                preparedStatement.setString(2, user.getName());
                preparedStatement.setString(3, user.getEmail());
                preparedStatement.executeUpdate();
                return obtain(user.getId()).orElseThrow(()-> new UserNotFoundException(user.getId()));
            } catch (SQLException e) {
                throw new DbException("User update failed.");
            }
        };

        return JdbcUtils.inTransactionGet(userUpdate);
    }

    @Override
    public Optional<User> obtain(Long userId) {
        InConnectionSupplier<User> userRead = connection -> {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                preparedStatement = connection.prepareStatement(QueryPool.USER_SELECT_BY_ID);
                preparedStatement.setLong(1, userId);
                resultSet = preparedStatement.executeQuery();
                return RowMapper.mapSingleResult(resultSet);
            } catch (SQLException e) {
                throw new DbException("Obtaining the user failed.");
            } finally {
                JdbcUtils.tryClose(resultSet, preparedStatement);
            }
        };

        return Optional.ofNullable(JdbcUtils.inTransactionGet(userRead));
    }

    @Override
    public void delete(Long userId) {
        InConnectionRunnable userDelete = connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(QueryPool.USER_DELETE)) {
                preparedStatement.setLong(1, userId);
                preparedStatement.execute();
            } catch (SQLException e) {
                throw new DbException("User deletion failed.");
            }
        };

        JdbcUtils.inTransactionRun(userDelete);
    }

    @Override
    public Boolean exists(Long id) {
        InConnectionSupplier<Boolean> userExists = connection -> {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                preparedStatement = connection.prepareStatement(QueryPool.USER_EXISTS);
                preparedStatement.setLong(1, id);
                resultSet = preparedStatement.executeQuery();
                return resultSet.next();
            } catch (SQLException e) {
                throw new DbException("User existence check failed");
            } finally {
                JdbcUtils.tryClose(resultSet, preparedStatement);
            }
        };

        return JdbcUtils.inTransactionGet(userExists);
    }

    @Override
    public List<User> findAll() {
        InConnectionSupplier<List<User>> userFindAll = connection -> {
            Statement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.createStatement();
                resultSet = statement.executeQuery(QueryPool.USER_FIND_ALL);
                return RowMapper.mapResultList(resultSet);
            } catch (SQLException e) {
                throw new DbException("User findAll operation failed.");
            } finally {
                JdbcUtils.tryClose(resultSet, statement);
            }
        };

        return JdbcUtils.inTransactionGet(userFindAll);
    }


    private static class QueryPool {
        private static final String USER_UPSERT = "MERGE INTO users(id, name, email) VALUES (?,?,?);";
        private static final String USER_SELECT_BY_ID = "SELECT "
                + "u.id u_id, u.name u_name, u.email u_email, "
                + "b.id b_id, b.title b_title, b.author b_author, "
                + "b.publication_year b_year, b.is_available b_available, "
                + "br_from.id br_from_id, "
                + "br_to.id br_to_id "
                + "FROM users u "
                + "LEFT JOIN owner_cards oc ON oc.owner_id = u.id "
                + "LEFT JOIN books b ON b.id = oc.book_id "
                + "LEFT JOIN book_requests br_from ON br_from.requester_id = u.id "
                + "LEFT JOIN book_requests br_to ON br_to.book_id = b.id "
                + "WHERE u.id = ? "
                + "AND oc.owned_till IS NULL "
                + "ORDER BY oc.owned_since, br_from.created_on, br_to.created_on;";
        private static final String USER_DELETE = "DELETE FROM users WHERE id = ?;";
        private static final String USER_EXISTS = "SELECT EXISTS(SELECT 1 FROM users WHERE id = ?);";
        private static final String USER_FIND_ALL = "SELECT "
                + "u.id u_id, u.name u_name, u.email u_email, "
                + "b.id b_id, b.title b_title, b.author b_author, "
                + "b.publication_year b_year, b.is_available b_available, "
                + "br_from.id br_from_id, "
                + "br_to.id br_to_id "
                + "FROM users u "
                + "LEFT JOIN owner_cards oc ON oc.owner_id = u.id "
                + "LEFT JOIN books b ON b.id = oc.book_id "
                + "LEFT JOIN book_requests br_from ON br_from.requester_id = u.id "
                + "LEFT JOIN book_requests br_to ON br_to.book_id = b.id "
                + "WHERE oc.owned_till IS NULL "
                + "ORDER BY u.id, oc.owned_since, br_from.created_on, br_to.created_on;";
    }

    private static class RowMapper {
        private static User mapSingleResult(ResultSet resultSet) throws SQLException {
            User user = new User();
            Map<Long, Book> booksOwned = new LinkedHashMap<>();
            Set<Long> requestsFrom = new LinkedHashSet<>();
            Set<Long> requestsTo = new LinkedHashSet<>();

            while (resultSet.next()) {
                fillUserFields(user, resultSet);
                generateBookOwned(booksOwned, resultSet);
                addRequest(requestsFrom, "br_from_id", resultSet);
                addRequest(requestsTo, "br_to_id", resultSet);
            }
            mergeIntoUser(user, booksOwned, requestsFrom, requestsTo);

            return user.getId() != null
                    ? user
                    : null;
        }

        private static List<User> mapResultList(ResultSet resultSet) throws SQLException {
            List<User> users = new ArrayList<>();
            Map<Long, Book> booksOwned = new LinkedHashMap<>();
            Set<Long> requestsFrom = new LinkedHashSet<>();
            Set<Long> requestsTo = new LinkedHashSet<>();
            User user = new User();
            while (resultSet.next()) {
                if (user.getId() != null
                        && !user.getId().equals(resultSet.getLong("u_id"))) {
                    mergeIntoUser(user, booksOwned, requestsFrom, requestsTo);
                    users.add(user);
                    user = new User();
                    booksOwned.clear();
                    requestsFrom.clear();
                    requestsTo.clear();
                }
                fillUserFields(user, resultSet);
                generateBookOwned(booksOwned, resultSet);
                addRequest(requestsFrom, "br_from_id", resultSet);
                addRequest(requestsTo, "br_to_id", resultSet);
            }

            return users;
        }

        private static void mergeIntoUser(User user,
                                          Map<Long, Book> booksOwned,
                                          Set<Long> requestsFrom,
                                          Set<Long> requestsTo) {
            user.setBooksOwned(new ArrayList<>(booksOwned.values()));
            user.setRequestsFrom(new ArrayList<>(requestsFrom));
            user.setRequestsTo(new ArrayList<>(requestsTo));
        }

        private static void addRequest(Set<Long> requests, String columnLabel,
                                       ResultSet resultSet) throws SQLException {
            Long requestId = resultSet.getLong(columnLabel);
            if (requestId == 0) {
                return;
            }

            requests.add(requestId);
        }

        private static void fillUserFields(User user, ResultSet resultSet) throws SQLException {
            if (user.getId() == null) {
                user.setId(resultSet.getLong("u_id"));
            }
            if (user.getName() == null) {
                user.setName(resultSet.getString("u_name"));
            }
            if (user.getEmail() == null) {
                user.setEmail(resultSet.getString("u_email"));
            }
        }

        private static void generateBookOwned(Map<Long, Book> booksOwned,
                                              ResultSet resultSet) throws SQLException {
            Long bookId = resultSet.getLong("b_id");
            if (bookId == 0 || booksOwned.containsKey(bookId)) {
                return;
            }

            Book book = new Book();
            book.setId(bookId);
            book.setTitle(resultSet.getString("title"));
            book.setAuthor(resultSet.getString("author"));
            book.setPublicationYear(
                    resultSet.getInt("publication_year") != 0
                            ? resultSet.getInt("publication_year")
                            : null
            );
            book.setIsAvailable(resultSet.getBoolean("is_available"));
            booksOwned.put(bookId, book);
        }
    }
}
