package bookrequest.dto;

import book.dao.BookLazyInitProxy;
import bookrequest.model.BookRequest;
import user.dao.UserLazyInitProxy;

public class BookRequestMapperImpl implements BookRequestMapper {
    @Override
    public BookRequestDto dtoFromBookRequest(BookRequest bookRequest) {
        return new BookRequestDto(
                bookRequest.getId(),
                bookRequest.getRequester().getId(),
                bookRequest.getBook().getId(),
                bookRequest.getCreatedOn());
    }

    @Override
    public BookRequest bookRequestFromBook(BookRequestDto bookRequestDto) {
        return new BookRequest(
                bookRequestDto.getId(),
                new UserLazyInitProxy(bookRequestDto.getRequesterId()),
                new BookLazyInitProxy(bookRequestDto.getBookId()),
                bookRequestDto.getCreatedOn());
    }
}
