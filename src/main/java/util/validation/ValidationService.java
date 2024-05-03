package util.validation;

import lombok.Getter;

public interface ValidationService {
    final ValidationService DEFAULT_INSTANCE = new ValidationServiceImpl();

    <T> void validate(T t);
}
