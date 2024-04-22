package book.model;

import user.model.User;

import java.util.Set;

public class Book {
    private Long id;
    private String name;
    private String author;
    private Integer publicationYear;
    private Boolean isAvailable;
    private Set<User> ownedBy;
}
