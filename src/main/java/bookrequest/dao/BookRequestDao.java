package bookrequest.dao;

import bookrequest.model.BookRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRequestDao extends JpaRepository<BookRequest, Long> {
}
