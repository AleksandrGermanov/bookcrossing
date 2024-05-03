package exception.exists;

public class UserExistsException extends ExistsException {
    public UserExistsException(Long userId) {
        super(String.format("User with id = %d already exists.", userId));
    }
}
