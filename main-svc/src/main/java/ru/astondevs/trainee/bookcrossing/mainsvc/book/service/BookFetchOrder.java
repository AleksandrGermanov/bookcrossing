package ru.astondevs.trainee.bookcrossing.mainsvc.book.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.model.Book;

public enum BookFetchOrder {
    DEFAULT {
        @Override
        public Order[] getOrder(CriteriaBuilder criteriaBuilder, Root<Book> bookRoot) {
            return new Order[]{criteriaBuilder.asc(bookRoot.get("id"))};
        }
    },
    PUBLICATION_YEAR_DESC {
        @Override
        public Order[] getOrder(CriteriaBuilder criteriaBuilder, Root<Book> bookRoot) {
            return new Order[]{
                    criteriaBuilder.desc(bookRoot.get("publicationYear")),
                    criteriaBuilder.asc(bookRoot.get("id"))
            };
        }
    },
    IS_AVAILABLE_DESC {
        @Override
        public Order[] getOrder(CriteriaBuilder criteriaBuilder, Root<Book> bookRoot) {
            return new Order[]{
                    criteriaBuilder.desc(bookRoot.get("isAvailable")),
                    criteriaBuilder.asc(bookRoot.get("id"))
            };
        }
    };

    public abstract Order[] getOrder(CriteriaBuilder criteriaBuilder, Root<Book> bookRoot);

}
