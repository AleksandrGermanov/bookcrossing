package util.beanlib;

import book.service.BookService;
import book.service.BookServiceImpl;
import bookrequest.model.BookRequest;
import bookrequest.service.BookRequestService;
import bookrequest.service.BookRequestServiceImpl;
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
    @Getter
    private static final BookRequestService bookRequestImpl = new BookRequestServiceImpl();

    public static UserService getDefaultUserService() {
        return userImpl;
    }

    public static BookService getDefaultBookService() {
        return bookImpl;
    }
    public static BookRequestService getDefaultBookRequestService() {
        return bookRequestImpl;
    }
}
