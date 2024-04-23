package user.model;

import book.model.Book;
import bookrequest.model.BookRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private String email;
    private List<Book> booksOwned;
    private List<Long> requestsFrom;
    private List<Long> requestsTo;
}
