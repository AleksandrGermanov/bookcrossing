package bookrequest.model;

import user.model.User;
import book.model.Book;

import java.time.LocalDateTime;

public class BookRequest {

    private Long id;
    private Long requester_id;
    private Long book_id;
    private LocalDateTime createdOn;
}
