package ownercard.model;

import book.model.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import user.model.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerCard {
    private Long id;
    private User owner;
    private Book book;
    private LocalDateTime ownedSince;
    private LocalDateTime ownedTill;
}
