package util.beanlib;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import user.service.UserService;
import user.service.UserServiceImpl;

@UtilityClass
public final class ServiceLib {
    @Getter
    private static final UserService userImpl = new UserServiceImpl();

    public static UserService getDefaultUserService(){
        return userImpl;
    }
}
