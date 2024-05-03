package util.validation;

import exception.BookcrossingException;
import jakarta.validation.*;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import java.util.Set;

public class ValidationServiceImpl implements ValidationService {
    private Validator validator;

    @Override
    public <T> void validate(T t) {
        initValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(t);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private void initValidator() {
        if (validator == null) {
            try (ValidatorFactory factory = Validation.byDefaultProvider()
                    .configure()
                    .messageInterpolator(new ParameterMessageInterpolator())
                    .buildValidatorFactory();) {
                validator = factory.getValidator();
            } catch (Exception e) {
                throw new BookcrossingException(String.format("Could not initiate "
                        + "validator due to %s with message %s.", e.getClass().getName(), e.getMessage()));
            }
        }
    }
}
