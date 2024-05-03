package user.dao;

import book.model.Book;
import exception.notfound.UserNotFoundException;
import user.model.User;
import util.beanlib.DaoLib;

import java.util.List;
import java.util.Objects;

public class UserLazyInitProxy extends User {
    private final Long id;
    private final UserDao userDao = DaoLib.getDefaultUserDao();
    private User user;


    public UserLazyInitProxy(Long id) {
        this.id = id;
    }


    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        initUser();
        return user.getName();
    }

    @Override
    public String getEmail() {
        initUser();
        return user.getEmail();
    }

    @Override
    public List<Book> getBooksInPossession() {
        initUser();
        return user.getBooksInPossession();
    }

    @Override
    public List<Long> getRequestsFrom() {
        initUser();
        return user.getRequestsFrom();
    }

    @Override
    public List<Long> getRequestsTo() {
        initUser();
        return user.getRequestsTo();
    }

    public User getUser(){
        initUser();
        return user;
    }

    private void initUser() {
        if (user == null) {
            user = userDao.obtain(id).orElseThrow(() -> new UserNotFoundException(id)
            );
        }
    }

    @Override
    public String toString() {
        initUser();
        return user.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object != null && User.class.equals(object.getClass())) {
            initUser();
            return user.equals(object);
        }
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        UserLazyInitProxy that = (UserLazyInitProxy) object;
        return Objects.equals(id, that.id) && Objects.equals(userDao, that.userDao) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        initUser();
        return user.hashCode();
    }
}
