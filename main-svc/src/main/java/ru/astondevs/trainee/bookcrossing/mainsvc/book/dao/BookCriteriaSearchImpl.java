package ru.astondevs.trainee.bookcrossing.mainsvc.book.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.model.Book;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.service.BookFetchOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class BookCriteriaSearchImpl implements BookCriteriaSearch {
    private final EntityManager entityManager;

    @Override
    public List<Book> searchByParams(Map<String, Object> params, BookFetchOrder order) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> bookQuery = criteriaBuilder.createQuery(Book.class);
        Root<Book> bookRoot = bookQuery.from(Book.class);
        order = order != null ? order : BookFetchOrder.DEFAULT;

        bookQuery
                .select(bookRoot)
                .where(passPredicateList(params, criteriaBuilder, bookRoot))
                .orderBy(order.getOrder(criteriaBuilder, bookRoot));

        return entityManager
                .createQuery(bookQuery)
                .getResultList();
    }

    private Predicate[] passPredicateList(Map<String, Object> params, CriteriaBuilder criteriaBuilder, Root<Book> bookRoot) {
        List<Predicate> predicates = new ArrayList<>();
        Object title = params.get("title");
        Object author = params.get("author");
        Object publishedSince = params.get("publishedSince");
        Object isAvailable = params.get("isAvailable");

        if (title instanceof String) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.upper(bookRoot.get("title")),
                    ("%" + title + "%").toUpperCase()));
        }
        if (author instanceof String) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.upper(bookRoot.get("author")),
                    ("%" + author + "%").toUpperCase()));
        }
        if (publishedSince instanceof Integer) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo((bookRoot.get("publicationYear").as(Integer.class)),
                    (int) publishedSince));
        }
        if (isAvailable instanceof Boolean) {
            predicates.add(criteriaBuilder.equal((bookRoot.get("isAvailable")).as(Boolean.class), isAvailable));
        }

        return predicates.toArray(new Predicate[0]);
    }
}
