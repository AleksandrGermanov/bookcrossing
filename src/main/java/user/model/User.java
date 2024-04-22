package user.model;

import book.model.Book;
import bookrequest.model.BookRequest;

import java.util.Set;

public class User {
    private Long id;
    private String name;
    private String email;
    private Set<Book> booksOwned;
    private Set<BookRequest> requestsFrom;
    private Set<BookRequest> requestsTo;
}
