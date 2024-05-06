package exception.notfound;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(Long id) {
        super(String.format("User with id = %d was not found.", id));
    }
}
