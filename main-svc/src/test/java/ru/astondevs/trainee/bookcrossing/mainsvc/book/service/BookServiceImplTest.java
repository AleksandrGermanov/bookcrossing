package ru.astondevs.trainee.bookcrossing.mainsvc.book.service;

import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.dao.BookDao;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.dao.OwnerCardDao;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.dto.BookDto;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.dto.BookMapper;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.model.Book;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.model.OwnerCard;
import ru.astondevs.trainee.bookcrossing.mainsvc.exception.mismatch.OwnerMismatchException;
import ru.astondevs.trainee.bookcrossing.mainsvc.user.dao.UserDao;
import ru.astondevs.trainee.bookcrossing.mainsvc.user.model.User;
import ru.astondevs.trainee.bookcrossing.mainsvc.user.service.UserService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    @Mock
    UserService userService;
    @Mock
    private BookDao bookDao;
    @Mock
    private OwnerCardDao ownerCardDao;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private Validator validator;
    @Mock
    private UserDao userDao;
    @InjectMocks
    private BookServiceImpl bookService;
    private BookDto bookDto;
    private Book book;
    private User owner;
    private OwnerCard ownerCard;

    @BeforeEach
    public void setup() {
        bookDto = new BookDto();
        bookDto.setTitle("title");
        bookDto.setAuthor("Author");
        book = new Book();
        book.setId(1L);
        book.setTitle("title");
        book.setAuthor("author");
        owner = new User(1L, "ru/astondevs/trainee/bookcrossing/user", "user@ema.il");
        ownerCard = new OwnerCard(1L, owner, book,
                LocalDateTime.of(2022, 2, 2, 2, 2, 2),
                null);
    }

    @Test
    void createBook() {
        when(bookMapper.bookFromDto(bookDto)).thenReturn(book);
        when(bookDao.save(book)).thenReturn(book);
        when(userService.getUserElseThrow(1L)).thenReturn(owner);
        when(ownerCardDao.save(any())).thenReturn(ownerCard);
        when(bookMapper.dtoFromBook(book)).thenReturn(bookDto);

        assertEquals(bookDto, bookService.createBook(1L, bookDto));
        verify(validator, times(1)).validate(book);
    }

    @Test
    void updateBook() {
        bookDto.setAuthor("new Author");
        book.setOwnedBy(List.of(new User(2L, "", ""), owner));


        when(bookDao.findById(1L)).thenReturn(Optional.of(book));
        when(bookDao.save(book)).thenReturn(book);
        when(ownerCardDao.findByBookIdAndOwnedTillIsNull(1L)).thenReturn(Optional.of(ownerCard));
        when(bookMapper.dtoFromBook(book)).thenReturn(bookDto);

        assertEquals(bookDto, bookService.updateBook(1L, 1L, bookDto));
        verify(validator, times(1)).validate(book);
    }

    @Test
    void retrieveBook() {
        when(bookDao.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.dtoFromBook(book)).thenReturn(bookDto);

        assertEquals(bookDto, bookService.retrieveBook(1L));
    }

    @Test
    void deleteBook() {
        when(ownerCardDao.findByBookIdAndOwnedTillIsNull(1L)).thenReturn(Optional.of(ownerCard));

        assertDoesNotThrow(() -> bookService.deleteBook(1L, 1L));
    }

    @Test
    void deleteBook_WhenUserIsNotBookOwner_ThrowsException() {
        when(ownerCardDao.findByBookIdAndOwnedTillIsNull(1L)).thenReturn(Optional.of(ownerCard));

        assertThrows(OwnerMismatchException.class, () -> bookService.deleteBook(2L, 1L));
    }

    @Test
    void findAll() {
        when(bookDao.findAll()).thenReturn(List.of(book));
        when(bookMapper.dtoFromBook(book)).thenReturn(bookDto);

        assertEquals(List.of(bookDto), bookService.findAll());
    }

    @Test
    void searchByParams() {
        Map<String, Object> params = new HashMap<>();
        BookFetchOrder order = BookFetchOrder.DEFAULT;

        when(bookDao.searchByParams(params, order)).thenReturn(List.of(book));
        when(bookMapper.dtoFromBook(book)).thenReturn(bookDto);

        assertEquals(List.of(bookDto), bookService.searchByParams(params, order));
    }

    @Test
    void searchByParams_WhenNullsArePassed_DoesNotThrow() {
        assertDoesNotThrow(() -> bookService.searchByParams(null, null));
    }

    @Test
    void giveBookAway() {
        when(ownerCardDao.findByBookIdAndOwnedTillIsNull(1L)).thenReturn(Optional.of(ownerCard));
        when(userService.getUserElseThrow(2L)).thenReturn(owner);
        assertNull(ownerCard.getOwnedTill());

        assertDoesNotThrow(() -> bookService.giveBookAway(1L, 2L, 1L));

        assertNotNull(ownerCard.getOwnedTill());
        verify(ownerCardDao, times(1)).save(ownerCard);
        verify(ownerCardDao, times(2)).save(any(OwnerCard.class));
    }

    @Test
    void giveBookAway_WhenUserIsNotBookOwner_ThrowsException() {
        when(ownerCardDao.findByBookIdAndOwnedTillIsNull(1L)).thenReturn(Optional.of(ownerCard));

        assertThrows(OwnerMismatchException.class, () -> bookService.giveBookAway(2L, 3L, 1L));
    }
}