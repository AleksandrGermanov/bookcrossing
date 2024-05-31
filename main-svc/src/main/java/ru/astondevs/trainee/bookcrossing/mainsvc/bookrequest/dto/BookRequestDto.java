package ru.astondevs.trainee.bookcrossing.mainsvc.bookrequest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRequestDto {
    private Long id;
    private Long requesterId;
    private Long bookId;
    private LocalDateTime createdOn;
}
