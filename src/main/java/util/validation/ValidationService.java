package util.validation;

public interface ValidationService {
    final ValidationService DEFAULT_INSTANCE = new ValidationServiceImpl();

    <T> void validate(T t);
}
