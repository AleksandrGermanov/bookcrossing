package bookrequest.dto;

import bookrequest.model.BookRequest;

public interface BookRequestMapper {
    BookRequestDto dtoFromBookRequest(BookRequest bookRequest);
    BookRequest bookRequestFromBook(BookRequestDto bookRequestDto);
}
