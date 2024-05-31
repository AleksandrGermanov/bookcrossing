package bookrequest.service;

import book.dao.BookDao;
import book.model.Book;
import bookrequest.dao.BookRequestDao;
import bookrequest.dto.BookRequestDto;
import bookrequest.dto.BookRequestMapper;
import bookrequest.model.BookRequest;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookRequestServiceImplTest {
    @Mock
    private BookRequestDao bookRequestDao;
    @Mock
    private BookRequestMapper bookRequestMapper;
    @Mock
    private ValidationService validationService;
    @InjectMocks
    private BookRequestServiceImpl bookRequestService;
    @Mock
    private BookDao bookDao;
    @Mock
    private UserDao userDao;
    @InjectMocks
    private ProxyFactory proxyFactory;

    private Book book;
    private User owner;
    private User requester;
    private BookRequest bookRequest;
    private BookRequestDto bookRequestDto;

    @BeforeEach
    public void setup() {
        bookRequestService.setProxyFactory(proxyFactory);

        owner = new User(1L, "user", "user@ema.il");
        requester = new User(2L, "user2", "user2@ema.il");
        book = new Book();
        book.setId(1L);
        book.setTitle("title");
        book.setAuthor("author");
        book.setIsAvailable(true);
        book.setOwnedBy(List.of(owner));
        owner = new User(1L, "user", "user@ema.il");
        bookRequest = new BookRequest(1L, requester, book,
                LocalDateTime.of(2022, 2, 2, 2, 2, 2));
        bookRequestDto = new BookRequestDto(1L, requester.getId(), book.getId(),
                LocalDateTime.of(2022, 2, 2, 2, 2, 2));
    }

    @Test
    void createBookRequest() {
        when(bookDao.obtain(1L)).thenReturn(Optional.of(book));
        when(bookRequestDao.create(any(BookRequest.class))).thenReturn(bookRequest);
        when(bookRequestMapper.dtoFromBookRequest(bookRequest)).thenReturn(bookRequestDto);

        assertEquals(bookRequestDto, bookRequestService.createBookRequest(2L, 1L));
        verify(validationService, times(1)).validate(any(BookRequest.class));
    }

    @Test
    void retrieveBookRequest() {
        when(bookRequestDao.obtain(1L)).thenReturn(Optional.of(bookRequest));
        when(bookRequestMapper.dtoFromBookRequest(bookRequest)).thenReturn(bookRequestDto);

        assertEquals(bookRequestDto, bookRequestService.retrieveBookRequest(1L));
    }

    @Test
    void deleteBookRequest() {
        when(bookRequestDao.obtain(1L)).thenReturn(Optional.of(bookRequest));

        assertDoesNotThrow(()-> bookRequestService.deleteBookRequest(2L, 1L));
        verify(bookRequestDao, times(1)).delete(1L);
    }
}