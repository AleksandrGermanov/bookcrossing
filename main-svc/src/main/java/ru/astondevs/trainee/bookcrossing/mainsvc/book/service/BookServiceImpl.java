package ru.astondevs.trainee.bookcrossing.mainsvc.book.service;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.dao.BookDao;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.dao.OwnerCardDao;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.dto.BookDto;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.dto.BookMapper;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.model.Book;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.model.OwnerCard;
import ru.astondevs.trainee.bookcrossing.mainsvc.exception.mismatch.OwnerMismatchException;
import ru.astondevs.trainee.bookcrossing.mainsvc.exception.notfound.BookNotFoundException;
import ru.astondevs.trainee.bookcrossing.mainsvc.exception.notfound.OwnerCardNotFoundException;
import ru.astondevs.trainee.bookcrossing.mainsvc.user.model.User;
import ru.astondevs.trainee.bookcrossing.mainsvc.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookDao bookDao;
    private final UserService userService;
    private final OwnerCardDao ownerCardDao;
    private final BookMapper bookMapper;
    private final Validator validator;

    @Override
    @Transactional
    public BookDto createBook(Long ownerId, BookDto bookDto) {
        Book bookToCreate = bookMapper.bookFromDto(bookDto);
        validator.validate(bookToCreate);
        User owner = userService.getUserElseThrow(ownerId);
        Book created = bookDao.save(bookToCreate);

        ownerCardDao.save(new OwnerCard(null,
                owner,
                created,
                LocalDateTime.parse(LocalDateTime.now().toString()),
                null));

        return bookMapper.dtoFromBook(created);
    }

    @Override
    @Transactional
    public BookDto updateBook(Long userId, Long bookId, BookDto bookDto) {
        bookDto.setId(bookId);
        Book bookToUpdate = getBookElseThrow(bookId);
        Long currentOwnerId = findCurrentOwnerCard(bookId).getOwner().getId();
        checkBookOwner(userId, bookId, currentOwnerId);
        mergeIntoBook(bookDto, bookToUpdate);
        validator.validate(bookToUpdate);
        Book updated = bookDao.save(bookToUpdate);

        return bookMapper.dtoFromBook(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public BookDto retrieveBook(Long id) {
        return bookMapper.dtoFromBook(getBookElseThrow(id));
    }

    @Override
    @Transactional
    public void deleteBook(Long userId, Long bookId) {
        OwnerCard currentOwnerCard = findCurrentOwnerCard(bookId);
        Long currentOwnerId = currentOwnerCard.getOwner().getId();
        checkBookOwner(userId, bookId, currentOwnerId);
        bookDao.deleteById(bookId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> findAll() {
        return bookDao.findAll().stream()
                .map(bookMapper::dtoFromBook)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> searchByParams(Map<String, Object> params, BookFetchOrder order) {
        return bookDao.searchByParams(params, order).stream()
                .map(bookMapper::dtoFromBook)
                .toList();
    }

    @Override
    @Transactional
    public void giveBookAway(Long userFromId, Long userToId, Long bookId) {
        OwnerCard currentOwnerCard = findCurrentOwnerCard(bookId);
        Long currentOwnerId = currentOwnerCard.getOwner().getId();
        checkBookOwner(userFromId, bookId, currentOwnerId);
        User userTo = userService.getUserElseThrow(userToId);

        currentOwnerCard.setOwnedTill(LocalDateTime.now());
        ownerCardDao.save(currentOwnerCard);
        ownerCardDao.save(new OwnerCard(
                null,
                userTo,
                currentOwnerCard.getBook(),
                LocalDateTime.now(),
                null));
    }

    @Override
    public Book getBookElseThrow(Long id) {
        return bookDao.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    private void checkBookOwner(Long userFromId, Long bookId, Long currentOwnerId) {
        if (!Objects.equals(currentOwnerId, userFromId)) {
            throw new OwnerMismatchException(String.format(
                    "User with id = %d is doesn't own the book with id = %d.", userFromId, bookId));
        }
    }

    private void mergeIntoBook(BookDto updated, Book bookToUpdate) {
        String title = updated.getAuthor();
        if (title != null) {
            bookToUpdate.setTitle(title);
        }
        String author = updated.getAuthor();
        if (author != null) {
            bookToUpdate.setAuthor(author);
        }
        Integer publicationYear = updated.getPublicationYear();
        if (publicationYear != null) {
            bookToUpdate.setPublicationYear(publicationYear);
        }
        Boolean isAvailable = updated.getIsAvailable();
        if (isAvailable != null) {
            bookToUpdate.setIsAvailable(isAvailable);
        }
    }

    private OwnerCard findCurrentOwnerCard(Long bookId) {
        return ownerCardDao.findByBookIdAndOwnedTillIsNull(bookId).orElseThrow(
                () -> new OwnerCardNotFoundException(String.format(
                        "Current ownerCard for book with id = %d was not found.", bookId
                )));
    }
}