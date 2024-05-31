package ru.astondevs.trainee.bookcrossing.mainsvc.bookrequest.service;


import ru.astondevs.trainee.bookcrossing.mainsvc.bookrequest.dto.BookRequestDto;

public interface BookRequestService {
    BookRequestDto createBookRequest(Long requesterId, Long bookId);

    BookRequestDto retrieveBookRequest(Long bookRequestId);

    void deleteBookRequest(Long userId, Long bookRequestId);
}
