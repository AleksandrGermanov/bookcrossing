package ru.astondevs.trainee.bookcrossing.mainsvc.book.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.astondevs.trainee.bookcrossing.mainsvc.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "owner_cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private User owner;
    @OneToOne
    private Book book;
    @Column(name = "owned_since", columnDefinition = "TIMESTAMP NOT NULL")
    private LocalDateTime ownedSince;
    @Column(name = "owned_till", columnDefinition = "TIMESTAMP")
    private LocalDateTime ownedTill;
}
