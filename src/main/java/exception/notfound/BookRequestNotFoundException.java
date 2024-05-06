package exception.notfound;

public class BookRequestNotFoundException extends NotFoundException {
    public BookRequestNotFoundException(Long id) {
        super(String.format("BookRequest with id = %d was not found.", id));
    }
}
