package ru.astondevs.trainee.bookcrossing.mainsvc.bookrequest.service;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.model.Book;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.service.BookService;
import ru.astondevs.trainee.bookcrossing.mainsvc.bookrequest.dao.BookRequestDao;
import ru.astondevs.trainee.bookcrossing.mainsvc.bookrequest.dto.BookRequestDto;
import ru.astondevs.trainee.bookcrossing.mainsvc.bookrequest.dto.BookRequestMapper;
import ru.astondevs.trainee.bookcrossing.mainsvc.bookrequest.model.BookRequest;
import ru.astondevs.trainee.bookcrossing.mainsvc.exception.mismatch.BookStatusMismatchException;
import ru.astondevs.trainee.bookcrossing.mainsvc.exception.mismatch.OwnerMismatchException;
import ru.astondevs.trainee.bookcrossing.mainsvc.exception.notfound.BookRequestNotFoundException;
import ru.astondevs.trainee.bookcrossing.mainsvc.user.model.User;
import ru.astondevs.trainee.bookcrossing.mainsvc.user.service.UserService;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookRequestServiceImpl implements BookRequestService {
    private final BookService bookService;
    private final UserService userService;
    private final BookRequestDao bookRequestDao;
    private final BookRequestMapper bookRequestMapper;
    private final Validator validator;

    @Override
    @Transactional
    public BookRequestDto createBookRequest(Long requesterId, Long bookId) {
        Book book = bookService.getBookElseThrow(bookId);
        if (!book.getIsAvailable()) {
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
    @Transactional(readOnly = true)
    public BookRequestDto retrieveBookRequest(Long bookRequestId) {
        return bookRequestMapper.dtoFromBookRequest(
                getRequestOrElseThrow(bookRequestId)
        );
    }

    @Override
    @Transactional
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