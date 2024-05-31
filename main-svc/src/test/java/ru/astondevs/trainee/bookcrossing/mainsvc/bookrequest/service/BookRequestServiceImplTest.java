package ru.astondevs.trainee.bookcrossing.mainsvc.bookrequest.service;

import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.model.Book;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.service.BookService;
import ru.astondevs.trainee.bookcrossing.mainsvc.bookrequest.dao.BookRequestDao;
import ru.astondevs.trainee.bookcrossing.mainsvc.bookrequest.dto.BookRequestDto;
import ru.astondevs.trainee.bookcrossing.mainsvc.bookrequest.dto.BookRequestMapper;
import ru.astondevs.trainee.bookcrossing.mainsvc.bookrequest.model.BookRequest;
import ru.astondevs.trainee.bookcrossing.mainsvc.user.model.User;
import ru.astondevs.trainee.bookcrossing.mainsvc.user.service.UserService;

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
    BookService bookService;
    @Mock
    UserService userService;
    @Mock
    private BookRequestDao bookRequestDao;
    @Mock
    private BookRequestMapper bookRequestMapper;
    @Mock
    private Validator validator;
    @InjectMocks
    private BookRequestServiceImpl bookRequestService;

    private Book book;
    private User owner;
    private User requester;
    private BookRequest bookRequest;
    private BookRequestDto bookRequestDto;

    @BeforeEach
    public void setup() {
        owner = new User(1L, "ru/astondevs/trainee/bookcrossing/user", "user@ema.il");
        requester = new User(2L, "user2", "user2@ema.il");
        book = new Book();
        book.setId(1L);
        book.setTitle("title");
        book.setAuthor("author");
        book.setIsAvailable(true);
        book.setOwnedBy(List.of(owner));
        owner = new User(1L, "ru/astondevs/trainee/bookcrossing/user", "user@ema.il");
        bookRequest = new BookRequest(1L, requester, book,
                LocalDateTime.of(2022, 2, 2, 2, 2, 2));
        bookRequestDto = new BookRequestDto(1L, requester.getId(), book.getId(),
                LocalDateTime.of(2022, 2, 2, 2, 2, 2));
    }

    @Test
    void createBookRequest() {
        when(bookService.getBookElseThrow(1L)).thenReturn(book);
        when(userService.getUserElseThrow(2L)).thenReturn(requester);
        when(bookRequestDao.save(any(BookRequest.class))).thenReturn(bookRequest);
        when(bookRequestMapper.dtoFromBookRequest(bookRequest)).thenReturn(bookRequestDto);

        assertEquals(bookRequestDto, bookRequestService.createBookRequest(2L, 1L));
        verify(validator, times(1)).validate(any(BookRequest.class));
    }

    @Test
    void retrieveBookRequest() {
        when(bookRequestDao.findById(1L)).thenReturn(Optional.of(bookRequest));
        when(bookRequestMapper.dtoFromBookRequest(bookRequest)).thenReturn(bookRequestDto);

        assertEquals(bookRequestDto, bookRequestService.retrieveBookRequest(1L));
    }

    @Test
    void deleteBookRequest() {
        when(bookRequestDao.findById(1L)).thenReturn(Optional.of(bookRequest));

        assertDoesNotThrow(() -> bookRequestService.deleteBookRequest(2L, 1L));
        verify(bookRequestDao, times(1)).deleteById(1L);
    }
}