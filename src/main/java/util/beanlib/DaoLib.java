package util.beanlib;

import book.dao.BookDao;
import book.dao.BookDaoJdbcImpl;
import bookrequest.dao.BookRequestDao;
import bookrequest.dao.BookRequestDaoJdbcImpl;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import ownercard.dao.OwnerCardDao;
import ownercard.dao.OwnerCardDaoJdbcImpl;
import user.dao.UserDao;
import user.dao.UserDaoJdbcImpl;

@UtilityClass
public final class DaoLib {
    @Getter
    private static final UserDao userJdbc = new UserDaoJdbcImpl();
    @Getter
    private static final BookDao bookJdbc = new BookDaoJdbcImpl();
    @Getter
    private static final BookRequestDao bookRequestJdbc = new BookRequestDaoJdbcImpl();
    @Getter
    private static final OwnerCardDao ownerCardJdbc = new OwnerCardDaoJdbcImpl();

    public static UserDao getDefaultUserDao() {
        return userJdbc;
    }

    public static BookDao getDefaultBookDao() {
        return bookJdbc;
    }

    public static BookRequestDao getDefaultBookRequestDao() {
        return bookRequestJdbc;
    }

    public static OwnerCardDao getDefaultOwnerCardDao() {
        return ownerCardJdbc;
    }
}