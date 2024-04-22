package bookrequest.model;

import user.model.User;
import book.model.Book;

import java.time.LocalDateTime;

public class BookRequest {

    private Long id;
    private User requester;
    private Book book;
    private LocalDateTime createdOn;
}
