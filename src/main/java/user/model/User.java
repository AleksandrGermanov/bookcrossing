package user.model;

import book.model.Book;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    @Email
    private String email;
    @EqualsAndHashCode.Exclude
    private List<Book> booksInPossession;
    @EqualsAndHashCode.Exclude
    private List<Long> requestsFrom;
    @EqualsAndHashCode.Exclude
    private List<Long> requestsTo;

    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
