package exception.notfound;

public class BookNotFoundException extends NotFoundException{
    public BookNotFoundException(Long id) {
        super(String.format("Book with id = %d was not found.", id));
    }

}
