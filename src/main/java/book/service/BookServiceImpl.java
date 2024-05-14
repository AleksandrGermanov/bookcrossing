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
import exception.notfound.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import user.dao.UserLazyInitProxy;
import user.model.User;
import util.beanlib.DaoLib;
import util.beanlib.MapperLib;
import util.beanlib.ProxyFactory;
import util.validation.ValidationService;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookDao bookDao;
    private final OwnerCardDao ownerCardDao;
    private final BookMapper bookMapper;
    private final ValidationService validationService;
    @Setter
    private ProxyFactory proxyFactory;

    public BookServiceImpl(){
        bookDao = DaoLib.getDefaultBookDao();
        ownerCardDao = DaoLib.getDefaultOwnerCardDao();
        bookMapper = MapperLib.getDefaultBookMapper();
        validationService = ValidationService.DEFAULT_INSTANCE;
        proxyFactory = new ProxyFactory();
    }

    @Override
    public BookDto createBook(Long ownerId, BookDto bookDto) {
        Book bookToCreate = bookMapper.bookFromDto(bookDto);
        validationService.validate(bookToCreate);
        UserLazyInitProxy ownerProxy =  proxyFactory.proxyOfUser(ownerId);
        if(!ownerProxy.referencesExisting()){
            throw new UserNotFoundException(ownerId);
        }
        Book created = bookDao.create(bookToCreate);

        created.setOwnedBy(List.of(ownerProxy));
        ownerCardDao.create(new OwnerCard(null,
                ownerProxy,
                created,
                LocalDateTime.now(),
                null));

        return bookMapper.dtoFromBook(created);
    }

    @Override
    public BookDto updateBook(Long userId, Long bookId, BookDto bookDto) {
        bookDto.setId(bookId);
        Book bookToUpdate = getBookElseThrow(bookId);
        List<User> owners = bookToUpdate.getOwnedBy();
        Long currentOwnerId = owners.get(owners.size() - 1).getId();
        checkBookOwner(userId, bookId, currentOwnerId);
        mergeIntoBook(bookDto, bookToUpdate);
        validationService.validate(bookToUpdate);
        Book updated = bookDao.update(bookToUpdate);

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
        bookDao.delete(bookId);
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
        UserLazyInitProxy userToProxy =  proxyFactory.proxyOfUser(userToId);

        if(!userToProxy.referencesExisting()){
            throw new UserNotFoundException(userToId);
        }
        currentOwnerCard.setOwnedTill(LocalDateTime.now());
        ownerCardDao.update(currentOwnerCard);
        ownerCardDao.create(new OwnerCard(
                null,
                userToProxy,
                currentOwnerCard.getBook(),
                LocalDateTime.now(),
                null));
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

    private Book getBookElseThrow(Long id) {
        return bookDao.obtain(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }
}
