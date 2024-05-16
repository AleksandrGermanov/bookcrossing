package user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import user.model.User;
import util.dao.CommonDao;

public interface UserDao extends JpaRepository<User, Long> {
}
