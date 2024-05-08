package util.beanlib;

import book.dao.BookDao;
import book.dao.BookLazyInitProxy;
import lombok.RequiredArgsConstructor;
import user.dao.UserDao;
import user.dao.UserLazyInitProxy;

@RequiredArgsConstructor
public class ProxyFactory {
    private final UserDao userDao;
    private final BookDao bookDao;

    public ProxyFactory(){
        userDao = DaoLib.getDefaultUserDao();
        bookDao = DaoLib.getDefaultBookDao();
    }

    public UserLazyInitProxy proxyOfUser(Long userId){
        return new UserLazyInitProxy(userId, userDao);
    }

    public BookLazyInitProxy proxyOfBook(Long bookId){
        return new BookLazyInitProxy(bookId, bookDao);
    }
}
