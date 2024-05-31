package ru.astondevs.trainee.bookcrossing.mainsvc.bookrequest.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.model.Book;
import ru.astondevs.trainee.bookcrossing.mainsvc.user.model.User;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "book_requests")
@NoArgsConstructor
@AllArgsConstructor
public class BookRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @OneToOne
    @JoinColumn(name = "requester_id")
    private User requester;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
    @NotNull
    private LocalDateTime createdOn;

    public BookRequest(User requester, Book book) {
        this.requester = requester;
        this.book = book;
        createdOn = LocalDateTime.now();
    }
}
