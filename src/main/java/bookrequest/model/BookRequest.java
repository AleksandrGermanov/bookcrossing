package bookrequest.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import user.model.User;
import book.model.Book;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRequest {
    private Long id;
    @NotNull
    private User requester;
    @NotNull
    private Book book;
    @NotNull
    private LocalDateTime createdOn;
}
