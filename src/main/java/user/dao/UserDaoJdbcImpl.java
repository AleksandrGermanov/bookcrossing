package user.dao;

import book.dao.BookLazyInitProxy;
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
        InConnectionSupplier<Long> userCreate = connection -> {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                preparedStatement = connection.prepareStatement(QueryPool.USER_INSERT, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, user.getName());
                preparedStatement.setString(2, user.getEmail());
                preparedStatement.executeUpdate();
                resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
            } catch (SQLException e) {
                throw new DbException(String.format("User creation failed due to %s with message %s."
                        , e.getClass(), e.getMessage()));
            } finally {
                JdbcUtils.tryClose(resultSet, preparedStatement);
            }
            return null;
        };

        Long generatedId = JdbcUtils.inTransactionGet(userCreate);
        return obtain(generatedId).orElseThrow(() -> new UserNotFoundException(generatedId));
    }

    @Override
    public User update(User user) {
        InConnectionRunnable userUpdate = connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(QueryPool.USER_UPDATE)) {
                preparedStatement.setString(1, user.getName());
                preparedStatement.setString(2, user.getEmail());
                preparedStatement.setLong(3, user.getId());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DbException(String.format("User update failed due to %s with message %s."
                        , e.getClass(), e.getMessage()));
            }
        };

        JdbcUtils.inTransactionRun(userUpdate);
        return obtain(user.getId()).orElseThrow(() -> new UserNotFoundException(user.getId()));
    }

    @Override
    public Optional<User> obtain(Long id) {
        InConnectionSupplier<User> userRead = connection -> {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                preparedStatement = connection.prepareStatement(QueryPool.USER_SELECT_BY_ID);
                preparedStatement.setLong(1, id);
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
                if (resultSet.next()) {
                    return resultSet.getBoolean(1);
                }
                ;
            } catch (SQLException e) {
                throw new DbException("User existence check failed.");
            } finally {
                JdbcUtils.tryClose(resultSet, preparedStatement);
            }
            return null;
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
        private static final String USER_INSERT = "INSERT INTO users(name, email) VALUES (?,?);";
        private static final String USER_UPDATE = "UPDATE users "
                + "SET name = ?, email = ? "
                + "WHERE id = ?;";
        private static final String USER_SELECT_BY_ID = "SELECT "
                + "u.id u_id, u.name u_name, u.email u_email, "
                + "oc.book_id oc_book_id, "
                + "br_from.id br_from_id, "
                + "br_to.id br_to_id "
                + "FROM users u "
                + "LEFT JOIN (SELECT book_id, owner_id, owned_since "
                + "FROM owner_cards "
                + "WHERE owned_till IS NULL) oc "
                + "ON oc.owner_id = u.id "
                + "LEFT JOIN book_requests br_from ON br_from.requester_id = u.id "
                + "LEFT JOIN book_requests br_to ON br_to.book_id = oc.book_id "
                + "WHERE u.id = ? "
                + "ORDER BY oc.owned_since, br_from.created_on, br_to.created_on;";
        private static final String USER_DELETE = "DELETE FROM users WHERE id = ?;";
        private static final String USER_EXISTS = "SELECT EXISTS(SELECT 1 FROM users WHERE id = ?);";
        private static final String USER_FIND_ALL = "SELECT "
                + "u.id u_id, u.name u_name, u.email u_email, "
                + "oc.book_id oc_book_id, "
                + "br_from.id br_from_id, "
                + "br_to.id br_to_id "
                + "FROM users u "
                + "LEFT JOIN owner_cards oc ON oc.owner_id = u.id "
                + "LEFT JOIN book_requests br_from ON br_from.requester_id = u.id "
                + "LEFT JOIN book_requests br_to ON br_to.book_id = oc.book_id "
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
                    addUser(user, booksOwned, requestsFrom, requestsTo, users);
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
            if (user.getId() != null) {
                addUser(user, booksOwned, requestsFrom, requestsTo, users);
            }

            return users;
        }

        private static void addUser(User user, Map<Long, Book> booksOwned, Set<Long> requestsFrom, Set<Long> requestsTo, List<User> users) {
            mergeIntoUser(user, booksOwned, requestsFrom, requestsTo);
            users.add(user);
        }

        private static void mergeIntoUser(User user,
                                          Map<Long, Book> booksOwned,
                                          Set<Long> requestsFrom,
                                          Set<Long> requestsTo) {
            user.setBooksInPossession(new ArrayList<>(booksOwned.values()));
            user.setRequestsFrom(new ArrayList<>(requestsFrom));
            user.setRequestsTo(new ArrayList<>(requestsTo));
        }

        private static void addRequest(Set<Long> requests, String columnLabel,
                                       ResultSet resultSet) throws SQLException {
            long requestId = resultSet.getLong(columnLabel);
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
            Long bookId = resultSet.getLong("oc_book_id");
            if (bookId == 0 || booksOwned.containsKey(bookId)) {
                return;
            }

            Book book = new BookLazyInitProxy(bookId);
            booksOwned.put(bookId, book);
        }
    }
}
