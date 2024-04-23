package util.dao;

import java.util.List;
import java.util.Optional;

public interface CommonDao<T, I>{
    T create(T t);
    T update (T t);
    Optional<T> obtain(I id);
    void delete(I id);
    Boolean exists(I id);
    List<T> findAll();

}
