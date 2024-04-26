package book.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import user.model.User;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    private Long id;
    @NotNull
    @NotBlank
    private String title;
    @NotNull
    @NotBlank
    private String author;
    @Positive
    private Integer publicationYear;
    @NotNull
    private Boolean isAvailable;
    private List<User> ownedBy;


    public Book(Long id, String title, String author, Integer publicationYear, Boolean isAvailable) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.isAvailable = isAvailable;
    }
}
