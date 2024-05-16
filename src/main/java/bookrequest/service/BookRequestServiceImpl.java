package bookrequest.service;

import book.model.Book;
import book.service.BookService;
import bookrequest.dao.BookRequestDao;
import bookrequest.dto.BookRequestDto;
import bookrequest.dto.BookRequestMapper;
import bookrequest.model.BookRequest;
import exception.mismatch.OwnerMismatchException;
import exception.mismatch.BookStatusMismatchException;
import exception.notfound.BookRequestNotFoundException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import user.model.User;
import user.service.UserService;
import util.beanlib.DaoLib;
import util.beanlib.MapperLib;
import util.beanlib.ProxyFactory;
import util.validation.ValidationService;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class BookRequestServiceImpl implements BookRequestService {
    private final BookService bookService;
    private final UserService userService;
    private final BookRequestDao bookRequestDao;
    private final BookRequestMapper bookRequestMapper;
    private final Validator validator;

    @Override
    public BookRequestDto createBookRequest(Long requesterId, Long bookId) {
        Book book = bookService.getBookElseThrow(bookId);
        if(!book.getIsAvailable()){
            throw new BookStatusMismatchException(String.format(
                    "Book with id = %d is not available for requests.", bookId));
        }
        List<User> owners = book.getOwnedBy();
        Long currentOwnerId = owners.get(owners.size() - 1).getId();
        if (Objects.equals(requesterId, currentOwnerId)) {
            throw new OwnerMismatchException(
                    String.format("Book with id = %d is already owned by user with id = %d",
                            bookId, requesterId)
            );
        }

        BookRequest requestToSave = new BookRequest(userService.getUserElseThrow(requesterId), book);
        validator.validate(requestToSave);
        BookRequest saved = bookRequestDao.save(requestToSave);

        return bookRequestMapper.dtoFromBookRequest(saved);
    }

    @Override
    public BookRequestDto retrieveBookRequest(Long bookRequestId) {
        return bookRequestMapper.dtoFromBookRequest(
                getRequestOrElseThrow(bookRequestId)
        );
    }

    @Override
    public void deleteBookRequest(Long userId, Long bookRequestId) {
        BookRequest requestToDelete = getRequestOrElseThrow(bookRequestId);
        if (!Objects.equals(requestToDelete.getRequester().getId(), userId)) {
            throw new OwnerMismatchException(
                    String.format("Request with id = %d was not posted by user with id = %d",
                            requestToDelete.getBook().getId(), userId)
            );
        }

        bookRequestDao.deleteById(bookRequestId);
    }

    private BookRequest getRequestOrElseThrow(Long bookRequestId) {
        return bookRequestDao.findById(bookRequestId).orElseThrow(
                () -> new BookRequestNotFoundException(bookRequestId));
    }
}
