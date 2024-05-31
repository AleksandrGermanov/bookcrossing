package ru.astondevs.trainee.bookcrossing.mainsvc.util.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.service.BookFetchOrder;

@Configuration
public class BookFetchOrderConverter implements Converter<String, BookFetchOrder> {
    @Override
    public BookFetchOrder convert(String source) {
        return BookFetchOrder.valueOf(source.toUpperCase());
    }
}
