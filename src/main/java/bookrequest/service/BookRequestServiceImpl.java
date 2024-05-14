package bookrequest.service;

import book.model.Book;
import bookrequest.dao.BookRequestDao;
import bookrequest.dto.BookRequestDto;
import bookrequest.dto.BookRequestMapper;
import bookrequest.model.BookRequest;
import exception.mismatch.OwnerMismatchException;
import exception.mismatch.BookStatusMismatchException;
import exception.notfound.BookRequestNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import user.model.User;
import util.beanlib.DaoLib;
import util.beanlib.MapperLib;
import util.beanlib.ProxyFactory;
import util.validation.ValidationService;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class BookRequestServiceImpl implements BookRequestService {
    private final BookRequestDao bookRequestDao;
    private final BookRequestMapper bookRequestMapper;
    private final ValidationService validationService;
    @Setter
    private ProxyFactory proxyFactory;

    public BookRequestServiceImpl(){
        bookRequestDao = DaoLib.getDefaultBookRequestDao();
        bookRequestMapper = MapperLib.getDefaultBookRequestMapper();
        validationService = ValidationService.DEFAULT_INSTANCE;
        proxyFactory = new ProxyFactory();
    }
    @Override
    public BookRequestDto createBookRequest(Long requesterId, Long bookId) {
        Book book = proxyFactory.proxyOfBook(bookId);
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

        BookRequest requestToSave = new BookRequest(proxyFactory.proxyOfUser(requesterId), book);
        validationService.validate(requestToSave);
        BookRequest saved = bookRequestDao.create(requestToSave);

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

        bookRequestDao.delete(bookRequestId);
    }

    private BookRequest getRequestOrElseThrow(Long bookRequestId) {
        return bookRequestDao.obtain(bookRequestId).orElseThrow(
                () -> new BookRequestNotFoundException(bookRequestId));
    }
}
