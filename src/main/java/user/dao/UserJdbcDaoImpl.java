package user.dao;

import user.model.User;
import util.jdbc.InConnectionSupplier;

import java.util.List;
import java.util.Optional;

public class UserJdbcDaoImpl implements UserDao{

    @Override
    public User create(User user) {
        return null;
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public Optional<User> obtain(Long userId) {
        return Optional.empty();
    }

    @Override
    public Boolean delete(Long userId) {
        return null;
    }

    @Override
    public Boolean exists(Long id) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return null;
    }

    private static class QueryPool{
        private static final String USER_UPSERT = "MERGE INTO users ";
    }
}
