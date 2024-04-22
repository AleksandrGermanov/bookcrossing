package ownercard.model;

import book.model.Book;
import user.model.User;

import java.time.LocalDateTime;

public class OwnerCard {
    private Long id;
    private User owner;
    private Book book;
    private LocalDateTime ownedSince;
    private LocalDateTime ownedTill;
}
