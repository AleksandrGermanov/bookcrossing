package exception.notfound;

public class OwnerCardNotFoundException extends NotFoundException {
    public OwnerCardNotFoundException(Long id) {
        super(String.format("Owner card with id = %d was not found.", id));
    }
}