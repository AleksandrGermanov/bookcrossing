package book.service;

import book.dao.BookDao;
import book.dao.OwnerCardDao;
import book.dto.BookDto;
import book.dto.BookMapper;
import book.model.Book;
import book.model.OwnerCard;
import exception.mismatch.OwnerMismatchException;
import exception.notfound.BookNotFoundException;
import exception.notfound.OwnerCardNotFoundException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import user.model.User;
import user.service.UserService;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
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
    public BookDto createBook(Long ownerId, BookDto bookDto) {
        Book bookToCreate = bookMapper.bookFromDto(bookDto);
        validator.validate(bookToCreate);
        User owner = userService.getUserElseThrow(ownerId);
        Book created = bookDao.save(bookToCreate);

        created.setOwnedBy(List.of(owner));
        ownerCardDao.save(new OwnerCard(null,
                owner,
                created,
                LocalDateTime.now(),
                null));

        return bookMapper.dtoFromBook(created);
    }

    @Override
    public BookDto updateBook(Long userId, Long bookId, BookDto bookDto) {
        bookDto.setId(bookId);
        Book bookToUpdate = getBookElseThrow(bookId);
        Long currentOwnerId = ownerCardDao.obtainCurrentByBookId(bookId).orElseThrow(
                () -> new OwnerCardNotFoundException(String.format(
                        "Current ownerCard for book with id = %d is not found.", bookId))
        ).getOwner().getId();
        checkBookOwner(userId, bookId, currentOwnerId);
        mergeIntoBook(bookDto, bookToUpdate);
        validator.validate(bookToUpdate);
        Book updated = bookDao.save(bookToUpdate);

        return bookMapper.dtoFromBook(updated);
    }

    @Override
    public BookDto retrieveBook(Long id) {
        return bookMapper.dtoFromBook(getBookElseThrow(id));
    }

    @Override
    public void deleteBook(Long userId, Long bookId) {
        OwnerCard currentOwnerCard = ownerCardDao.obtainCurrentByBookId(bookId).orElseThrow(
                () -> new OwnerCardNotFoundException(String.format(
                        "Current ownerCard for book with id = %d was not found.", bookId
                )));
        Long currentOwnerId = currentOwnerCard.getOwner().getId();
        checkBookOwner(userId, bookId, currentOwnerId);
        bookDao.deleteById(bookId);
    }

    @Override
    public List<BookDto> findAll() {
        return bookDao.findAll().stream()
                .map(bookMapper::dtoFromBook)
                .toList();
    }

    @Override
    public List<BookDto> searchByParams(LinkedHashMap<String, String> params, BookFetchOrder order) {
        return bookDao.searchByParams(params, order).stream()
                .map(bookMapper::dtoFromBook)
                .toList();
    }

    @Override
    public void giveBookAway(Long userFromId, Long userToId, Long bookId) {
        OwnerCard currentOwnerCard = ownerCardDao.obtainCurrentByBookId(bookId).orElseThrow(
                () -> new OwnerCardNotFoundException(String.format(
                        "Current ownerCard for book with id = %d was not found.", bookId
                )));
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
}