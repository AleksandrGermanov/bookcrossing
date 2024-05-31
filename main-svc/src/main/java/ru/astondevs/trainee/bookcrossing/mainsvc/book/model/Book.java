package ru.astondevs.trainee.bookcrossing.mainsvc.book.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.astondevs.trainee.bookcrossing.mainsvc.bookrequest.model.BookRequest;
import ru.astondevs.trainee.bookcrossing.mainsvc.user.model.User;

import java.util.List;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String title;
    @NotBlank
    private String author;
    @Positive
    @Column(name = "publication_year")
    private Integer publicationYear;
    @NotNull
    @Column(name = "is_Available")
    private Boolean isAvailable;
    @EqualsAndHashCode.Exclude
    @ManyToMany
    @JoinTable(name = "owner_cards",
            joinColumns = {@JoinColumn(name = "book_id")},
            inverseJoinColumns = {@JoinColumn(name = "owner_id")})
    private List<User> ownedBy;
    @OneToMany(mappedBy = "book")
    private List<BookRequest> requestsForBook;


    public Book(Long id, String title, String author, Integer publicationYear, Boolean isAvailable) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.isAvailable = isAvailable;
    }


    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publicationYear=" + publicationYear +
                ", isAvailable=" + isAvailable +
                ", ownedBy=" + ownedBy.stream() //recursion: book <=> user
                .map(User::getId)
                .toList() +
                '}';
    }
}
