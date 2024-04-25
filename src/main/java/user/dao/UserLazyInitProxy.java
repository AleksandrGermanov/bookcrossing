package user.dao;

import book.model.Book;
import exception.notfound.UserNotFoundException;
import user.model.User;

import java.util.List;

public class UserLazyInitProxy extends User {
    private final Long id;
    private final UserDao userDao = new UserDaoJdbcImpl();
    private User user;


    public UserLazyInitProxy(Long id){
        this.id = id;
    }

    @Override
    public Long getId(){
        return id;
    }

    @Override
    public String getName(){
        initUser();
        return user.getName();
    }

    @Override
    public String getEmail(){
        initUser();
        return user.getEmail();
    }

    @Override
    public List<Book> getBooksOwned(){
        initUser();
        return user.getBooksOwned();
    }

    @Override
    public List<Long> getRequestsFrom(){
        initUser();
        return user.getRequestsFrom();
    }

    @Override
    public List<Long> getRequestsTo(){
        initUser();
        return user.getRequestsTo();
    }

    private void initUser(){
        if(user == null){
            user = userDao.obtain(id).orElseThrow(() -> new UserNotFoundException(id)
            );
        }
    }
}
