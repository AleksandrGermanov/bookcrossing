package book.service;

import book.dao.BookDao;
import book.dao.OwnerCardDao;
import book.dto.BookDto;
import book.dto.BookMapper;
import book.model.Book;
import book.model.OwnerCard;
import exception.mismatch.OwnerMismatchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import user.dao.UserDao;
import user.model.User;
import util.beanlib.ProxyFactory;
import util.validation.ValidationService;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    @Mock
    private BookDao bookDao;
    @Mock
    private OwnerCardDao ownerCardDao;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private ValidationService validationService;
    @Mock
    private UserDao userDao;
    @InjectMocks
    private ProxyFactory proxyFactory;
    @InjectMocks
    private BookServiceImpl bookService;
    private BookDto bookDto;
    private Book book;
    private User owner;
    private OwnerCard ownerCard;

    @BeforeEach
    public void setup() {
        bookService.setProxyFactory(proxyFactory);

        bookDto = new BookDto();
        bookDto.setTitle("title");
        bookDto.setAuthor("Author");
        book = new Book();
        book.setId(1L);
        book.setTitle("title");
        book.setAuthor("author");
        owner = new User(1L, "user", "user@ema.il");
        ownerCard = new OwnerCard(1L, owner, book,
                LocalDateTime.of(2022, 2, 2, 2, 2, 2),
                null);
    }

    @Test
    void createBook() {
        when(bookMapper.bookFromDto(bookDto)).thenReturn(book);
        when(bookDao.create(book)).thenReturn(book);
        when(userDao.exists(1L)).thenReturn(true);
        when(ownerCardDao.create(any())).thenReturn(ownerCard);
        when(bookMapper.dtoFromBook(book)).thenReturn(bookDto);

        assertEquals(bookDto, bookService.createBook(1L, bookDto));
        verify(validationService, times(1)).validate(book);
    }

    @Test
    void updateBook() {
        bookDto.setAuthor("new Author");
        book.setOwnedBy(List.of(new User(2L, "", ""), owner));


        when(bookDao.obtain(1L)).thenReturn(Optional.of(book));
        when(bookDao.update(book)).thenReturn(book);
        when(bookMapper.dtoFromBook(book)).thenReturn(bookDto);

        assertEquals(bookDto, bookService.updateBook(1L, 1L, bookDto));
        verify(validationService, times(1)).validate(book);
    }

    @Test
    void retrieveBook() {
        when(bookDao.obtain(1L)).thenReturn(Optional.of(book));
        when(bookMapper.dtoFromBook(book)).thenReturn(bookDto);

        assertEquals(bookDto, bookService.retrieveBook(1L));
    }

    @Test
    void deleteBook() {
        when(ownerCardDao.obtainCurrentByBookId(1L)).thenReturn(Optional.of(ownerCard));

        assertDoesNotThrow(() -> bookService.deleteBook(1L, 1L));
    }

    @Test
    void deleteBook_WhenUserIsNotBookOwner_ThrowsException() {
        when(ownerCardDao.obtainCurrentByBookId(1L)).thenReturn(Optional.of(ownerCard));

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
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
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
        when(ownerCardDao.obtainCurrentByBookId(1L)).thenReturn(Optional.of(ownerCard));
        when(userDao.exists(2L)).thenReturn(true);
        assertNull(ownerCard.getOwnedTill());

        assertDoesNotThrow(() -> bookService.giveBookAway(1L, 2L, 1L));

        assertNotNull(ownerCard.getOwnedTill());
        verify(ownerCardDao, times(1)).update(ownerCard);
        verify(ownerCardDao, times(1)).create(any(OwnerCard.class));
    }

    @Test
    void giveBookAway_WhenUserIsNotBookOwner_ThrowsException() {
        when(ownerCardDao.obtainCurrentByBookId(1L)).thenReturn(Optional.of(ownerCard));

        assertThrows(OwnerMismatchException.class, () -> bookService.giveBookAway(2L, 3L, 1L));
    }
}