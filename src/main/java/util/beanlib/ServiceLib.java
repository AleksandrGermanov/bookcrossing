package util.beanlib;

import book.service.BookService;
import book.service.BookServiceImpl;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import user.service.UserService;
import user.service.UserServiceImpl;

@UtilityClass
public final class ServiceLib {
    @Getter
    private static final UserService userImpl = new UserServiceImpl();
    @Getter
    private static final BookService bookImpl = new BookServiceImpl();

    public static UserService getDefaultUserService(){
        return userImpl;
    }
    public static BookService getDefaultBookService(){
        return bookImpl;
    }
}
