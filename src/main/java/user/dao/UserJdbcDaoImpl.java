package user.dao;

import book.model.Book;
import user.model.User;
import util.jdbc.InConnectionSupplier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UserJdbcDaoImpl implements UserDao {

    @Override
    public User create(User user) {

    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public Optional<User> obtain(Long userId) {
        InConnectionSupplier<User> create = connection -> {
            try{
                PreparedStatement preparedStatement = connection.prepareStatement(QueryPool.USER_SELECT);
                preparedStatement.setLong(1, userId);
                ResultSet resultSet = preparedStatement.executeQuery();
            }
        }
        return Optional.empty();
    }

    @Override
    public void delete(Long userId) {
    }

    @Override
    public Boolean exists(Long id) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return null;
    }

    private static class QueryPool {
        private static final String USER_UPSERT = "MERGE INTO users ";
        private static final String USER_SELECT = "SELECT "
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
    }

    private class RowMapper{
        private User mapSingleResult(ResultSet resultSet) throws SQLException {
            User user = new User();
            List<Book> booksOwned = new ArrayList<>();
            List<Long> requestsFrom = new ArrayList<>();
            List<Long> requestsTo = new ArrayList<>();

            while(resultSet.next()){
                if(user.getId() == null) {
                    user.setName(resultSet.getString("u_name"));
                }
                if(user.getName() == null) {
                    user.setName(resultSet.getString("u_name"));
                }
            }
        }
    }
}
