package bookrequest.service;

import book.dao.BookLazyInitProxy;
import book.model.Book;
import bookrequest.dao.BookRequestDao;
import bookrequest.dto.BookRequestDto;
import bookrequest.dto.BookRequestMapper;
import bookrequest.model.BookRequest;
import exception.OwnerMismatchException;
import exception.notfound.BookRequestNotFoundException;
import lombok.RequiredArgsConstructor;
import user.dao.UserLazyInitProxy;
import user.model.User;
import util.beanlib.DaoLib;
import util.beanlib.MapperLib;
import util.validation.ValidationService;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class BookRequestServiceImpl implements BookRequestService {
    private final BookRequestDao bookRequestDao;
    private final BookRequestMapper bookRequestMapper;
    private final ValidationService validationService;

    public BookRequestServiceImpl(){
        bookRequestDao = DaoLib.getDefaultBookRequestDao();
        bookRequestMapper = MapperLib.getDefaultBookRequestMapper();
        validationService = ValidationService.DEFAULT_INSTANCE;
    }
    @Override
    public BookRequestDto createBookRequest(Long requesterId, Long bookId) {
        Book book = new BookLazyInitProxy(bookId);
        List<User> owners = book.getOwnedBy();
        Long currentUserId = owners.get(owners.size() - 1).getId();
        if (Objects.equals(requesterId, currentUserId)) {
            throw new OwnerMismatchException(
                    String.format("Book with id = %d is already owned by user with id = %d",
                            bookId, requesterId)
            );
        }

        BookRequest requestToSave = new BookRequest(new UserLazyInitProxy(requesterId), book);
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
        if (Objects.equals(requestToDelete.getRequester().getId(), userId)) {
            throw new OwnerMismatchException(
                    String.format("Book with id = %d is not owned by user with id = %d",
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
