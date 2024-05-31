package ru.astondevs.trainee.bookcrossing.mainsvc.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.astondevs.trainee.bookcrossing.mainsvc.user.model.User;

public interface UserDao extends JpaRepository<User, Long> {
}
