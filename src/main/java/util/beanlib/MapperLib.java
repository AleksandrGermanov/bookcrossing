package util.beanlib;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import user.dto.UserMapper;
import user.dto.UserMapperImpl;
@UtilityClass
public final class MapperLib {
    @Getter
    private static final UserMapper userImpl = new UserMapperImpl();

    public static UserMapper getDefaultUserMapper(){
        return userImpl;
    }
}
