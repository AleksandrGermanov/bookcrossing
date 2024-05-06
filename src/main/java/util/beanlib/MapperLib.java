package util.beanlib;

import book.dto.BookMapper;
import book.dto.BookMapperImpl;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import user.dto.UserMapper;
import user.dto.UserMapperImpl;
@UtilityClass
public final class MapperLib {
    @Getter
    private static final UserMapper userImpl = new UserMapperImpl();
    @Getter
    private static final BookMapper bookImpl = new BookMapperImpl();

    public static UserMapper getDefaultUserMapper(){
        return userImpl;
    }
    public static BookMapper getDefaultBookMapper(){
        return bookImpl;
    }
}
